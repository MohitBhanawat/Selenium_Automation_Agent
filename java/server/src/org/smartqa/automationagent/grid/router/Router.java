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

import com.google.common.collect.ImmutableSet;

import static org.smartqa.automationagent.remote.http.Route.combine;
import static org.smartqa.automationagent.remote.http.Route.get;
import static org.smartqa.automationagent.remote.http.Route.matching;

import org.smartqa.automationagent.grid.distributor.Distributor;
import org.smartqa.automationagent.grid.sessionmap.SessionMap;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.http.Routable;
import org.smartqa.automationagent.remote.tracing.SpanDecorator;
import org.smartqa.automationagent.remote.tracing.Tracer;
import org.smartqa.automationagent.status.HasReadyState;

/**
 * A simple router that is aware of the automationagent-protocol.
 */
public class Router implements HasReadyState, Routable {

  private final Routable routes;
  private final SessionMap sessions;
  private final Distributor distributor;

  public Router(
    Tracer tracer,
    HttpClient.Factory clientFactory,
    SessionMap sessions,
    Distributor distributor) {
    Require.nonNull("Tracer to use", tracer);
    Require.nonNull("HTTP client factory", clientFactory);

    this.sessions = Require.nonNull("Session map", sessions);
    this.distributor = Require.nonNull("Distributor", distributor);

    routes =
      combine(
        get("/status")
          .to(() -> new GridStatusHandler(new Json(), tracer, clientFactory, distributor)),
        sessions.with(new SpanDecorator(tracer, req -> "session_map")),
        distributor.with(new SpanDecorator(tracer, req -> "distributor")),
        matching(req -> req.getUri().startsWith("/session/"))
          .to(() -> new HandleSession(tracer, clientFactory, sessions)));
  }

  @Override
  public boolean isReady() {
    try {
      return ImmutableSet.of(distributor, sessions).parallelStream()
        .map(HasReadyState::isReady)
        .reduce(true, Boolean::logicalAnd);
    } catch (RuntimeException e) {
      return false;
    }
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
