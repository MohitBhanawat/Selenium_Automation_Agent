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

package org.smartqa.automationagent.grid.distributor;

import org.smartqa.automationagent.SessionNotCreatedException;
import org.smartqa.automationagent.grid.data.CreateSessionResponse;
import org.smartqa.automationagent.grid.data.DistributorStatus;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.grid.node.Node;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.http.Routable;
import org.smartqa.automationagent.remote.http.Route;
import org.smartqa.automationagent.remote.tracing.SpanDecorator;
import org.smartqa.automationagent.remote.tracing.Tracer;
import org.smartqa.automationagent.status.HasReadyState;

import static org.smartqa.automationagent.remote.http.Contents.bytes;
import static org.smartqa.automationagent.remote.http.Route.delete;
import static org.smartqa.automationagent.remote.http.Route.get;
import static org.smartqa.automationagent.remote.http.Route.post;

import java.io.UncheckedIOException;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Responsible for being the central place where the {@link Node}s on which {@link Session}s run
 * are determined.
 * <p>
 * This class responds to the following URLs:
 * <table summary="HTTP commands the Distributor understands">
 * <tr>
 *   <th>Verb</th>
 *   <th>URL Template</th>
 *   <th>Meaning</th>
 * </tr>
 * <tr>
 *   <td>POST</td>
 *   <td>/session</td>
 *   <td>This is exactly the same as the New Session command from the WebDriver spec.</td>
 * </tr>
 * <tr>
 *   <td>POST</td>
 *   <td>/se/grid/distributor/node</td>
 *   <td>Adds a new {@link Node} to this distributor. Please read the javadocs for {@link Node} for
 *     how the Node should be serialized.</td>
 * </tr>
 * <tr>
 *   <td>DELETE</td>
 *   <td>/se/grid/distributor/node/{nodeId}</td>
 *   <td>Remove the {@link Node} identified by {@code nodeId} from this distributor. It is expected
 *     that any sessions running on the Node are allowed to complete: this simply means that no new
 *     sessions will be scheduled on this Node.</td>
 * </tr>
 * </table>
 */
public abstract class Distributor implements HasReadyState, Predicate<HttpRequest>, Routable {

  private final Route routes;
  protected final Tracer tracer;

  protected Distributor(Tracer tracer, HttpClient.Factory httpClientFactory) {
    this.tracer = Require.nonNull("Tracer", tracer);
    Require.nonNull("HTTP client factory", httpClientFactory);

    Json json = new Json();
    routes = Route.combine(
      post("/session").to(() -> req -> {
        CreateSessionResponse sessionResponse = newSession(req);
        return new HttpResponse().setContent(bytes(sessionResponse.getDownstreamEncodedResponse()));
      }),
      post("/se/grid/distributor/session")
          .to(() -> new CreateSession(this)),
      post("/se/grid/distributor/node")
          .to(() -> new AddNode(tracer, this, json, httpClientFactory)),
      delete("/se/grid/distributor/node/{nodeId}")
          .to(params -> new RemoveNode(this, UUID.fromString(params.get("nodeId")))),
      get("/se/grid/distributor/status")
          .to(() -> new GetDistributorStatus(this))
          .with(new SpanDecorator(tracer, req -> "distributor.status")));
  }

  public abstract CreateSessionResponse newSession(HttpRequest request)
    throws SessionNotCreatedException;

  public abstract Distributor add(Node node);

  public abstract void remove(UUID nodeId);

  public abstract DistributorStatus getStatus();

  @Override
  public boolean test(HttpRequest httpRequest) {
    return matches(httpRequest);
  }

  @Override
  public boolean matches(HttpRequest req) {
    return routes.matches(req);
  }

  @Override
  public HttpResponse execute(HttpRequest req) throws UncheckedIOException {
    return routes.execute(req);
  }
}
