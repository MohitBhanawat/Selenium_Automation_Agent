// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.smartqa.automationagent.grid.node.k8s;

import com.google.common.collect.ImmutableMap;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.NoSuchSessionException;
import org.smartqa.automationagent.PersistentCapabilities;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebDriverInfo;
import org.smartqa.automationagent.events.EventBus;
import org.smartqa.automationagent.grid.component.HealthCheck;
import org.smartqa.automationagent.grid.config.Config;
import org.smartqa.automationagent.grid.config.ConfigException;
import org.smartqa.automationagent.grid.data.CreateSessionRequest;
import org.smartqa.automationagent.grid.data.CreateSessionResponse;
import org.smartqa.automationagent.grid.data.NodeDrainComplete;
import org.smartqa.automationagent.grid.data.NodeStatus;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.grid.data.SessionClosedEvent;
import org.smartqa.automationagent.grid.log.LoggingOptions;
import org.smartqa.automationagent.grid.node.Node;
import org.smartqa.automationagent.grid.node.config.NodeOptions;
import org.smartqa.automationagent.grid.server.BaseServerOptions;
import org.smartqa.automationagent.grid.server.EventBusOptions;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.CommandExecutor;
import org.smartqa.automationagent.remote.RemoteWebDriver;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.Tracer;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.smartqa.automationagent.json.Json.MAP_TYPE;
import static org.smartqa.automationagent.remote.http.HttpMethod.DELETE;

/**
 * An implementation of {@link Node} that marks itself as draining immediately
 * after starting, and which then shuts down after usage. This will allow an
 * appropriately configured Kubernetes cluster to start a new node once the
 * session is finished.
 */
public class OneShotNode extends Node {

  private static final Logger LOG = Logger.getLogger(OneShotNode.class.getName());
  private static final Json JSON = new Json();

  private final EventBus events;
  private final WebDriverInfo driverInfo;
  private final Capabilities stereotype;
  private final String registrationSecret;
  private final URI gridUri;
  private RemoteWebDriver driver;
  private SessionId sessionId;
  private HttpClient client;
  private Capabilities capabilities;

  private OneShotNode(
    Tracer tracer,
    EventBus events,
    String registrationSecret,
    UUID id,
    URI uri,
    URI gridUri,
    Capabilities stereotype,
    WebDriverInfo driverInfo) {
    super(tracer, id, uri);

    this.registrationSecret = registrationSecret;
    this.events = Require.nonNull("Event bus", events);
    this.gridUri = Require.nonNull("Public Grid URI", gridUri);
    this.stereotype = ImmutableCapabilities.copyOf(Require.nonNull("Stereotype", stereotype));
    this.driverInfo = Require.nonNull("Driver info", driverInfo);
  }

  public static Node create(Config config) {
    LoggingOptions loggingOptions = new LoggingOptions(config);
    EventBusOptions eventOptions = new EventBusOptions(config);
    BaseServerOptions serverOptions = new BaseServerOptions(config);
    NodeOptions nodeOptions = new NodeOptions(config);

    Map<String, Object> raw = new Json().toType(
      config.get("k8s", "stereotype")
        .orElseThrow(() -> new ConfigException("Unable to find node stereotype")),
      MAP_TYPE);

    Capabilities stereotype = new ImmutableCapabilities(raw);

    Optional<String> driverName = config.get("k8s", "driver_name").map(String::toLowerCase);

    // Find the webdriver info corresponding to the driver name
    WebDriverInfo driverInfo = StreamSupport.stream(ServiceLoader.load(WebDriverInfo.class).spliterator(), false)
      .filter(info -> info.isSupporting(stereotype))
      .filter(info -> driverName.map(name -> name.equals(info.getDisplayName().toLowerCase())).orElse(true))
      .findFirst()
      .orElseThrow(() -> new ConfigException(
        "Unable to find matching driver for %s and %s", stereotype, driverName.orElse("any driver")));

    LOG.info(String.format("Creating one-shot node for %s with stereotype %s", driverInfo, stereotype));
    LOG.info("Grid URI is: " + nodeOptions.getPublicGridUri());

    return new OneShotNode(
      loggingOptions.getTracer(),
      eventOptions.getEventBus(),
      serverOptions.getRegistrationSecret(),
      UUID.randomUUID(),
      serverOptions.getExternalUri(),
      nodeOptions.getPublicGridUri().orElseThrow(() -> new ConfigException("Unable to determine public grid address")),
      stereotype,
      driverInfo);
  }

  @Override
  public Optional<CreateSessionResponse> newSession(CreateSessionRequest sessionRequest) {
    if (driver != null) {
      throw new IllegalStateException("Only expected one session at a time");
    }

    Optional<WebDriver> driver = driverInfo.createDriver(sessionRequest.getCapabilities());
    if (!driver.isPresent()) {
      return Optional.empty();
    }

    if (!(driver.get() instanceof RemoteWebDriver)) {
      driver.get().quit();
      return Optional.empty();
    }

    this.driver = (RemoteWebDriver) driver.get();
    this.sessionId = this.driver.getSessionId();
    this.client = extractHttpClient(this.driver);
    this.capabilities = rewriteCapabilities(this.driver);

    LOG.info("Encoded response: " + JSON.toJson(ImmutableMap.of(
      "value", ImmutableMap.of(
        "sessionId", sessionId,
        "capabilities", capabilities))));

    return Optional.of(
      new CreateSessionResponse(
        getSession(sessionId),
        JSON.toJson(ImmutableMap.of(
          "value", ImmutableMap.of(
            "sessionId", sessionId,
            "capabilities", capabilities))).getBytes(UTF_8)));
  }

  private HttpClient extractHttpClient(RemoteWebDriver driver) {
    CommandExecutor executor = driver.getCommandExecutor();

    try {
      Field client = null;
      Class<?> current = executor.getClass();
      while (client == null && (current != null || Object.class.equals(current))) {
        client = findClientField(current);
        current = current.getSuperclass();
      }

      if (client == null) {
        throw new IllegalStateException("Unable to find client field in " + executor.getClass());
      }

      if (!HttpClient.class.isAssignableFrom(client.getType())) {
        throw new IllegalStateException("Client field is not assignable to http client");
      }
      client.setAccessible(true);
      return (HttpClient) client.get(executor);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  private Field findClientField(Class<?> clazz) {
    try {
      return clazz.getDeclaredField("client");
    } catch (NoSuchFieldException e) {
      return null;
    }
  }

  private Capabilities rewriteCapabilities(RemoteWebDriver driver) {
    // Rewrite the se:options if necessary
    Object rawSeleniumOptions = driver.getCapabilities().getCapability("se:options");
    if (rawSeleniumOptions == null || rawSeleniumOptions instanceof Map) {
      @SuppressWarnings("unchecked") Map<String, Object> original = (Map<String, Object>) rawSeleniumOptions;
      Map<String, Object> updated = new TreeMap<>(original == null ? new HashMap<>() : original);

      String cdpPath = String.format("/session/%s/se/cdp", driver.getSessionId());
      updated.put("cdp", rewrite(cdpPath));

      return new PersistentCapabilities(driver.getCapabilities()).setCapability("se:options", updated);
    }

    return ImmutableCapabilities.copyOf(driver.getCapabilities());
  }

  private URI rewrite(String path) {
    try {
      return new URI(
        gridUri.getScheme(),
        gridUri.getUserInfo(),
        gridUri.getHost(),
        gridUri.getPort(),
        path,
        null,
        null);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public HttpResponse executeWebDriverCommand(HttpRequest req) {
    LOG.info("Executing " + req);

    HttpResponse res = client.execute(req);

    if (DELETE.equals(req.getMethod()) && req.getUri().equals("/session/" + sessionId)) {
      // Ensure the response is sent before we viciously kill the node

      new Thread(
        () -> {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
          }
          LOG.info("Stopping session: " + sessionId);
          stop(sessionId);
        },
        "Node clean up: " + getId())
      .start();
    }

    return res;
  }

  @Override
  public Session getSession(SessionId id) throws NoSuchSessionException {
    if (!isSessionOwner(id)) {
      throw new NoSuchSessionException("Unable to find session with id: " + id);
    }

    return new Session(
      sessionId,
      getUri(),
      capabilities);
  }

  @Override
  public HttpResponse uploadFile(HttpRequest req, SessionId id) {
    return null;
  }

  @Override
  public void stop(SessionId id) throws NoSuchSessionException {
    LOG.info("Stop has been called: " + id);
    Require.nonNull("Session ID", id);

    if (!isSessionOwner(id)) {
      throw new NoSuchSessionException("Unable to find session " + id);
    }

    LOG.info("Quitting session " + id);
    try {
      driver.quit();
    } catch (Exception e) {
      // It's possible that the driver has already quit.
    }

    events.fire(new SessionClosedEvent(id));
    LOG.info("Firing node drain complete message");
    events.fire(new NodeDrainComplete(getId()));
  }

  @Override
  public boolean isSessionOwner(SessionId id) {
    return driver != null && sessionId.equals(id);
  }

  @Override
  public boolean isSupporting(Capabilities capabilities) {
    return driverInfo.isSupporting(capabilities);
  }

  @Override
  public NodeStatus getStatus() {
    return new NodeStatus(
      getId(),
      getUri(),
      1,
      ImmutableMap.of(stereotype, 1),
      driver == null ?
        Collections.emptySet() :
        Collections.singleton(new NodeStatus.Active(stereotype, sessionId, capabilities)),
      registrationSecret);
  }

  @Override
  public HealthCheck getHealthCheck() {
    return () -> new HealthCheck.Result(true, "Everything is fine", registrationSecret);
  }

  @Override
  public boolean isReady() {
    return events.isReady();
  }
}
