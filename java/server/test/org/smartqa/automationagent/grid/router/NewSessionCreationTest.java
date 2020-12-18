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

package org.smartqa.automationagent.grid.router;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.smartqa.automationagent.WebDriverInfo;
import org.smartqa.automationagent.chrome.ChromeDriverInfo;
import org.smartqa.automationagent.events.EventBus;
import org.smartqa.automationagent.events.local.GuavaEventBus;
import org.smartqa.automationagent.firefox.GeckoDriverInfo;
import org.smartqa.automationagent.grid.config.MapConfig;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.grid.distributor.Distributor;
import org.smartqa.automationagent.grid.distributor.local.LocalDistributor;
import org.smartqa.automationagent.grid.node.Node;
import org.smartqa.automationagent.grid.node.config.DriverServiceSessionFactory;
import org.smartqa.automationagent.grid.node.local.LocalNode;
import org.smartqa.automationagent.grid.router.ProxyCdpIntoGrid;
import org.smartqa.automationagent.grid.router.Router;
import org.smartqa.automationagent.grid.server.BaseServerOptions;
import org.smartqa.automationagent.grid.server.Server;
import org.smartqa.automationagent.grid.sessionmap.SessionMap;
import org.smartqa.automationagent.grid.sessionmap.local.LocalSessionMap;
import org.smartqa.automationagent.grid.testing.TestSessionFactory;
import org.smartqa.automationagent.grid.web.EnsureSpecCompliantHeaders;
import org.smartqa.automationagent.netty.server.NettyServer;
import org.smartqa.automationagent.remote.http.Contents;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.http.Routable;
import org.smartqa.automationagent.remote.service.DriverService;
import org.smartqa.automationagent.remote.tracing.DefaultTestTracer;
import org.smartqa.automationagent.remote.tracing.Tracer;
import org.smartqa.automationagent.testing.drivers.Browser;

import java.net.URI;
import java.net.URISyntaxException;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.smartqa.automationagent.json.Json.JSON_UTF_8;
import static org.smartqa.automationagent.remote.http.HttpMethod.POST;

public class NewSessionCreationTest {

  private Tracer tracer;
  private EventBus events;
  private HttpClient.Factory clientFactory;

  @Before
  public void setup() {
    tracer = DefaultTestTracer.createTracer();
    events = new GuavaEventBus();
    clientFactory = HttpClient.Factory.createDefault();
  }

  @Test
  public void ensureJsCannotCreateANewSession() throws URISyntaxException {
    ChromeDriverInfo chromeDriverInfo = new ChromeDriverInfo();
    assumeThat(chromeDriverInfo.isAvailable()).isTrue();
    GeckoDriverInfo geckoDriverInfo = new GeckoDriverInfo();
    assumeThat(geckoDriverInfo.isAvailable()).isTrue();

    SessionMap sessions = new LocalSessionMap(tracer, events);
    Distributor distributor = new LocalDistributor(tracer, events, clientFactory, sessions, null);
    Routable router = new Router(tracer, clientFactory, sessions, distributor).with(new EnsureSpecCompliantHeaders(ImmutableList.of()));

    Server<?> server = new NettyServer(
      new BaseServerOptions(new MapConfig(ImmutableMap.of())),
      router,
      new ProxyCdpIntoGrid(clientFactory, sessions))
      .start();

    URI uri = server.getUrl().toURI();
    Node node = LocalNode.builder(
      tracer,
      events,
      uri,
      uri,
      null)
      .add(Browser.detect().getCapabilities(), new TestSessionFactory((id, caps) -> new Session(id, uri, caps)))
      .build();
    distributor.add(node);

    HttpClient client = HttpClient.Factory.createDefault().createClient(server.getUrl());

    // Attempt to create a session without setting the content type
    HttpResponse res = client.execute(
      new HttpRequest(POST, "/session")
        .setContent(Contents.asJson(ImmutableMap.of(
          "capabilities", ImmutableMap.of(
            "alwaysMatch", Browser.detect().getCapabilities())))));

    assertThat(res.getStatus()).isEqualTo(HTTP_INTERNAL_ERROR);

    // Attempt to create a session with an origin header but content type set
    res = client.execute(
      new HttpRequest(POST, "/session")
        .addHeader("Content-Type", JSON_UTF_8)
        .addHeader("Origin", "localhost")
        .setContent(Contents.asJson(ImmutableMap.of(
          "capabilities", ImmutableMap.of(
            "alwaysMatch", Browser.detect().getCapabilities())))));

    assertThat(res.getStatus()).isEqualTo(HTTP_INTERNAL_ERROR);

    // And now make sure the session is just fine
    res = client.execute(
      new HttpRequest(POST, "/session")
        .addHeader("Content-Type", JSON_UTF_8)
        .setContent(Contents.asJson(ImmutableMap.of(
          "capabilities", ImmutableMap.of(
            "alwaysMatch", Browser.detect().getCapabilities())))));

    assertThat(res.isSuccessful()).isTrue();
  }

  private LocalNode.Builder addDriverFactory(
    LocalNode.Builder builder,
    WebDriverInfo info,
    DriverService.Builder<?, ?> driverService) {
    return builder.add(
      info.getCanonicalCapabilities(),
      new DriverServiceSessionFactory(
        tracer,
        clientFactory,
        info::isSupporting,
        driverService));
  }
}
