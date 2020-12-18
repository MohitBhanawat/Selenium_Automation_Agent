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

package org.smartqa.automationagent.grid.node;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.NoSuchSessionException;
import org.smartqa.automationagent.grid.component.HealthCheck;
import org.smartqa.automationagent.grid.data.CreateSessionRequest;
import org.smartqa.automationagent.grid.data.CreateSessionResponse;
import org.smartqa.automationagent.grid.data.NodeStatus;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.io.TemporaryFilesystem;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.http.Routable;
import org.smartqa.automationagent.remote.http.Route;
import org.smartqa.automationagent.remote.tracing.SpanDecorator;
import org.smartqa.automationagent.remote.tracing.Tracer;
import org.smartqa.automationagent.status.HasReadyState;

import static org.smartqa.automationagent.remote.HttpSessionId.getSessionId;
import static org.smartqa.automationagent.remote.http.Contents.asJson;
import static org.smartqa.automationagent.remote.http.Route.combine;
import static org.smartqa.automationagent.remote.http.Route.delete;
import static org.smartqa.automationagent.remote.http.Route.get;
import static org.smartqa.automationagent.remote.http.Route.matching;
import static org.smartqa.automationagent.remote.http.Route.post;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A place where individual webdriver sessions are running. Those sessions may be in-memory, or
 * only reachable via localhost and a network. Or they could be something else entirely.
 * <p>
 * This class responds to the following URLs:
 * <table summary="HTTP commands the Node understands">
 * <tr>
 * <th>Verb</th>
 * <th>URL Template</th>
 * <th>Meaning</th>
 * </tr>
 * <tr>
 * <td>POST</td>
 * <td>/se/grid/node/session</td>
 * <td>Attempts to start a new session for the given node. The posted data should be a
 * json-serialized {@link Capabilities} instance. Returns a serialized {@link Session}.
 * Subclasses of {@code Node} are expected to register the session with the
 * {@link org.smartqa.automationagent.grid.sessionmap.SessionMap}.</td>
 * </tr>
 * <tr>
 * <td>GET</td>
 * <td>/se/grid/node/session/{sessionId}</td>
 * <td>Finds the {@link Session} identified by {@code sessionId} and returns the JSON-serialized
 * form.</td>
 * </tr>
 * <tr>
 * <td>DELETE</td>
 * <td>/se/grid/node/session/{sessionId}</td>
 * <td>Stops the {@link Session} identified by {@code sessionId}. It is expected that this will
 * also cause the session to removed from the
 * {@link org.smartqa.automationagent.grid.sessionmap.SessionMap}.</td>
 * </tr>
 * <tr>
 * <td>GET</td>
 * <td>/se/grid/node/owner/{sessionId}</td>
 * <td>Allows the node to be queried about whether or not it owns the {@link Session} identified
 * by {@code sessionId}. This returns a boolean.</td>
 * </tr>
 * <tr>
 * <td>*</td>
 * <td>/session/{sessionId}/*</td>
 * <td>The request is forwarded to the {@link Session} identified by {@code sessionId}. When the
 * Quit command is called, the {@link Session} should remove itself from the
 * {@link org.smartqa.automationagent.grid.sessionmap.SessionMap}.</td>
 * </tr>
 * </table>
 */
public abstract class Node implements HasReadyState, Routable {

  protected final Tracer tracer;
  private final UUID id;
  private final URI uri;
  private final Route routes;

  protected Node(Tracer tracer, UUID id, URI uri) {
    this.tracer = Require.nonNull("Tracer", tracer);
    this.id = Require.nonNull("Node id", id);
    this.uri = Require.nonNull("URI", uri);

    Json json = new Json();
    routes = combine(
        // "getSessionId" is aggressive about finding session ids, so this needs to be the last
        // route the is checked.
        matching(req -> getSessionId(req.getUri()).map(SessionId::new).map(this::isSessionOwner).orElse(false))
            .to(() -> new ForwardWebDriverCommand(this))
            .with(spanDecorator("node.forward_command")),
        post("/session/{sessionId}/file")
            .to(params -> new UploadFile(this, sessionIdFrom(params)))
            .with(spanDecorator("node.upload_file")),
        get("/se/grid/node/owner/{sessionId}")
            .to(params -> new IsSessionOwner(this, sessionIdFrom(params)))
            .with(spanDecorator("node.is_session_owner")),
        delete("/se/grid/node/session/{sessionId}")
            .to(params -> new StopNodeSession(this, sessionIdFrom(params)))
            .with(spanDecorator("node.stop_session")),
        get("/se/grid/node/session/{sessionId}")
            .to(params -> new GetNodeSession(this, sessionIdFrom(params)))
            .with(spanDecorator("node.get_session")),
        post("/se/grid/node/session")
            .to(() -> new NewNodeSession(this, json))
            .with(spanDecorator("node.new_session")),
        get("/se/grid/node/status")
            .to(() -> req -> new HttpResponse().setContent(asJson(getStatus())))
            .with(spanDecorator("node.node_status")),
        get("/status")
            .to(() -> new StatusHandler(this))
            .with(spanDecorator("node.status")));
  }

  private SessionId sessionIdFrom(Map<String, String> params) {
    return new SessionId(params.get("sessionId"));
  }

  private SpanDecorator spanDecorator(String name) {
    return new SpanDecorator(tracer, req -> name);
  }

  public UUID getId() {
    return id;
  }

  public URI getUri() {
    return uri;
  }

  public abstract Optional<CreateSessionResponse> newSession(CreateSessionRequest sessionRequest);

  public abstract HttpResponse executeWebDriverCommand(HttpRequest req);

  public abstract Session getSession(SessionId id) throws NoSuchSessionException;

  public TemporaryFilesystem getTemporaryFilesystem(SessionId id) throws IOException {
    throw new UnsupportedOperationException();
  }

  public abstract HttpResponse uploadFile(HttpRequest req, SessionId id);

  public abstract void stop(SessionId id) throws NoSuchSessionException;

  public abstract boolean isSessionOwner(SessionId id);

  public abstract boolean isSupporting(Capabilities capabilities);

  public abstract NodeStatus getStatus();

  public abstract HealthCheck getHealthCheck();

  @Override
  public boolean matches(HttpRequest req) {
    return routes.matches(req);
  }

  @Override
  public HttpResponse execute(HttpRequest req) {
    return routes.execute(req);
  }
}
