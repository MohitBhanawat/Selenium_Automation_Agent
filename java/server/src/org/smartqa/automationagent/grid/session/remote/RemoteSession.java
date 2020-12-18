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

package org.smartqa.automationagent.grid.session.remote;

import com.google.common.base.StandardSystemProperty;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.grid.session.ActiveSession;
import org.smartqa.automationagent.grid.session.SessionFactory;
import org.smartqa.automationagent.grid.web.ProtocolConverter;
import org.smartqa.automationagent.grid.web.ReverseProxyHandler;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.io.TemporaryFilesystem;
import org.smartqa.automationagent.remote.Augmenter;
import org.smartqa.automationagent.remote.Command;
import org.smartqa.automationagent.remote.CommandExecutor;
import org.smartqa.automationagent.remote.Dialect;
import org.smartqa.automationagent.remote.DriverCommand;
import org.smartqa.automationagent.remote.ProtocolHandshake;
import org.smartqa.automationagent.remote.RemoteWebDriver;
import org.smartqa.automationagent.remote.Response;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.Tracer;

import static org.smartqa.automationagent.remote.Dialect.OSS;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class designed to do things like protocol conversion.
 */
public abstract class RemoteSession implements ActiveSession {

  protected static Logger log = Logger.getLogger(ActiveSession.class.getName());

  private final SessionId id;
  private final Dialect downstream;
  private final Dialect upstream;
  private final HttpHandler codec;
  private final Map<String, Object> capabilities;
  private final TemporaryFilesystem filesystem;
  private final WebDriver driver;

  protected RemoteSession(
      Dialect downstream,
      Dialect upstream,
      HttpHandler codec,
      SessionId id,
      Map<String, Object> capabilities) {
    this.downstream = Require.nonNull("Downstream dialect", downstream);
    this.upstream = Require.nonNull("Upstream dialect", upstream);
    this.codec = Require.nonNull("Codec", codec);
    this.id = Require.nonNull("Session id", id);
    this.capabilities = Require.nonNull("Capabilities", capabilities);

    File tempRoot = new File(StandardSystemProperty.JAVA_IO_TMPDIR.value(), id.toString());
    Require.stateCondition(tempRoot.mkdirs(), "Could not create directory %s", tempRoot);
    this.filesystem = TemporaryFilesystem.getTmpFsBasedOn(tempRoot);

    CommandExecutor executor = new ActiveSessionCommandExecutor(this);
    this.driver = new Augmenter().augment(new RemoteWebDriver(
        executor,
        new ImmutableCapabilities(getCapabilities())));
  }

  @Override
  public SessionId getId() {
    return id;
  }

  @Override
  public Dialect getUpstreamDialect() {
    return upstream;
  }

  @Override
  public Dialect getDownstreamDialect() {
    return downstream;
  }

  @Override
  public Map<String, Object> getCapabilities() {
    return capabilities;
  }

  @Override
  public TemporaryFilesystem getFileSystem() {
    return filesystem;
  }

  @Override
  public WebDriver getWrappedDriver() {
    return driver;
  }

  @Override
  public HttpResponse execute(HttpRequest req) throws UncheckedIOException {
    return codec.execute(req);
  }

  public abstract static class Factory<X> implements SessionFactory {

    protected Optional<ActiveSession> performHandshake(
        Tracer tracer,
        X additionalData,
        URL url,
        Set<Dialect> downstreamDialects,
        Capabilities capabilities) {
      try {
        HttpClient client = HttpClient.Factory.createDefault().createClient(url);

        Command command = new Command(
            null,
            DriverCommand.NEW_SESSION(capabilities));

        ProtocolHandshake.Result result = new ProtocolHandshake().createSession(client, command);

        HttpHandler codec;
        Dialect upstream = result.getDialect();
        Dialect downstream;
        if (downstreamDialects.contains(result.getDialect())) {
          codec = new ReverseProxyHandler(tracer, client);
          downstream = upstream;
        } else {
          downstream = downstreamDialects.isEmpty() ? OSS : downstreamDialects.iterator().next();
          codec = new ProtocolConverter(tracer, client, downstream, upstream);
        }

        Response response = result.createResponse();
        //noinspection unchecked
        Optional<ActiveSession> activeSession = Optional.of(newActiveSession(
            additionalData,
            downstream,
            upstream,
            codec,
            new SessionId(response.getSessionId()),
            (Map<String, Object>) response.getValue()));
        activeSession.ifPresent(session -> log.info("Started new session " + session));
        return activeSession;
      } catch (IOException | IllegalStateException | NullPointerException e) {
        log.log(Level.WARNING, e.getMessage(), e);
        return Optional.empty();
      }
    }

    protected abstract ActiveSession newActiveSession(
        X additionalData,
        Dialect downstream,
        Dialect upstream,
        HttpHandler codec,
        SessionId id,
        Map<String, Object> capabilities);
  }
}
