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

package org.smartqa.automationagent.grid.sessionmap;

import org.smartqa.automationagent.NoSuchSessionException;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.http.Routable;
import org.smartqa.automationagent.remote.http.Route;
import org.smartqa.automationagent.remote.tracing.Tracer;
import org.smartqa.automationagent.status.HasReadyState;

import static org.smartqa.automationagent.remote.http.Route.combine;
import static org.smartqa.automationagent.remote.http.Route.delete;
import static org.smartqa.automationagent.remote.http.Route.post;

import java.net.URI;
import java.util.Map;

/**
 * Provides a stable API for looking up where on the Grid a particular webdriver instance is
 * running.
 * <p>
 * This class responds to the following URLs:
 * <table summary="HTTP commands the SessionMap understands">
 * <tr>
 *   <th>Verb</th>
 *   <th>URL Template</th>
 *   <th>Meaning</th>
 * </tr>
 * <tr>
 *   <td>DELETE</td>
 *   <td>/se/grid/session/{sessionId}</td>
 *   <td>Removes a {@link URI} from the session map. Calling this method more than once for the same
 *     {@link SessionId} will not throw an error.</td>
 * </tr>
 * <tr>
 *   <td>GET</td>
 *   <td>/se/grid/session/{sessionId}</td>
 *   <td>Retrieves the {@link URI} associated the {@link SessionId}, or throws a
 *     {@link org.smartqa.automationagent.NoSuchSessionException} should the session not be present.</td>
 * </tr>
 * <tr>
 *   <td>POST</td>
 *   <td>/se/grid/session/{sessionId}</td>
 *   <td>Registers the session with session map. In theory, the session map never expires a session
 *     from its mappings, but realistically, sessions may end up being removed for many reasons.
 *     </td>
 * </tr>
 * </table>
 */
public abstract class SessionMap implements HasReadyState, Routable {

  protected final Tracer tracer;

  private final Route routes;

  public abstract boolean add(Session session);

  public abstract Session get(SessionId id) throws NoSuchSessionException;

  public abstract void remove(SessionId id);

  public URI getUri(SessionId id) throws NoSuchSessionException {
    return get(id).getUri();
  }

  public SessionMap(Tracer tracer) {
    this.tracer = Require.nonNull("Tracer", tracer);

    Json json = new Json();
    routes = combine(
        Route.get("/se/grid/session/{sessionId}/uri")
            .to(params -> new GetSessionUri(this, sessionIdFrom(params))),
        post("/se/grid/session")
            .to(() -> new AddToSessionMap(tracer, json, this)),
        Route.get("/se/grid/session/{sessionId}")
            .to(params -> new GetFromSessionMap(tracer, this, sessionIdFrom(params))),
        delete("/se/grid/session/{sessionId}")
            .to(params -> new RemoveFromSession(tracer, this, sessionIdFrom(params))));
  }

  private SessionId sessionIdFrom(Map<String, String> params) {
    return new SessionId(params.get("sessionId"));
  }

  @Override
  public boolean matches(HttpRequest req) {
    return routes.matches(req);
  }

  @Override
  public HttpResponse execute(HttpRequest req) {
    return routes.execute(req);
  }
}
