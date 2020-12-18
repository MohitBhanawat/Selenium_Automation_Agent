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

import org.junit.Before;
import org.junit.Test;
import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.events.EventBus;
import org.smartqa.automationagent.events.local.GuavaEventBus;
import org.smartqa.automationagent.grid.component.HealthCheck;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.grid.distributor.Distributor;
import org.smartqa.automationagent.grid.distributor.local.LocalDistributor;
import org.smartqa.automationagent.grid.node.Node;
import org.smartqa.automationagent.grid.node.local.LocalNode;
import org.smartqa.automationagent.grid.router.Router;
import org.smartqa.automationagent.grid.sessionmap.SessionMap;
import org.smartqa.automationagent.grid.sessionmap.local.LocalSessionMap;
import org.smartqa.automationagent.grid.testing.PassthroughHttpClient;
import org.smartqa.automationagent.grid.testing.TestSessionFactory;
import org.smartqa.automationagent.grid.web.CombinedHandler;
import org.smartqa.automationagent.grid.web.Values;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.DefaultTestTracer;
import org.smartqa.automationagent.remote.tracing.Tracer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.smartqa.automationagent.json.Json.MAP_TYPE;
import static org.smartqa.automationagent.remote.http.HttpMethod.GET;

public class RouterTest {

  private Tracer tracer;
  private EventBus bus;
  private CombinedHandler handler;
  private SessionMap sessions;
  private Distributor distributor;
  private Router router;

  @Before
  public void setUp() {
    tracer = DefaultTestTracer.createTracer();
    bus = new GuavaEventBus();

    handler = new CombinedHandler();
    HttpClient.Factory clientFactory = new PassthroughHttpClient.Factory(handler);

    sessions = new LocalSessionMap(tracer, bus);
    handler.addHandler(sessions);

    distributor = new LocalDistributor(tracer, bus, clientFactory, sessions, null);
    handler.addHandler(distributor);

    router = new Router(tracer, clientFactory, sessions, distributor);
  }

  @Test
  public void shouldListAnEmptyDistributorAsMeaningTheGridIsNotReady() {
    Map<String, Object> status = getStatus(router);
    assertFalse((Boolean) status.get("ready"));
  }

  @Test
  public void addingANodeThatIsDownMeansTheGridIsNotReady() throws URISyntaxException {
    Capabilities capabilities = new ImmutableCapabilities("cheese", "peas");
    URI uri = new URI("http://exmaple.com");

    AtomicBoolean isUp = new AtomicBoolean(false);

    Node node = LocalNode.builder(tracer, bus, uri, uri, null)
        .add(capabilities, new TestSessionFactory((id, caps) -> new Session(id, uri, caps)))
        .advanced()
        .healthCheck(() -> new HealthCheck.Result(isUp.get(), "TL;DR"))
        .build();
    distributor.add(node);

    Map<String, Object> status = getStatus(router);
    assertFalse(status.toString(), (Boolean) status.get("ready"));
  }

  @Test
  public void aNodeThatIsUpAndHasSpareSessionsMeansTheGridIsReady() throws URISyntaxException {
    Capabilities capabilities = new ImmutableCapabilities("cheese", "peas");
    URI uri = new URI("http://exmaple.com");

    AtomicBoolean isUp = new AtomicBoolean(true);

    Node node = LocalNode.builder(tracer, bus, uri, uri, null)
        .add(capabilities, new TestSessionFactory((id, caps) -> new Session(id, uri, caps)))
        .advanced()
        .healthCheck(() -> new HealthCheck.Result(isUp.get(), "TL;DR"))
        .build();
    distributor.add(node);

    Map<String, Object> status = getStatus(router);
    assertTrue(status.toString(), (Boolean) status.get("ready"));
  }

  @Test
  public void shouldListAllNodesTheDistributorIsAwareOf() {

  }

  @Test
  public void ifNodesHaveSpareSlotsButAlreadyHaveMaxSessionsGridIsNotReady() {

  }

  private Map<String, Object> getStatus(Router router) {
    HttpResponse response = router.execute(new HttpRequest(GET, "/status"));
    Map<String, Object> status = Values.get(response, MAP_TYPE);
    assertNotNull(status);
    return status;
  }
}
