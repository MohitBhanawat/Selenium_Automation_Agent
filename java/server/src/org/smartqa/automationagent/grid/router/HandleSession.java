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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.smartqa.automationagent.NoSuchSessionException;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.grid.sessionmap.SessionMap;
import org.smartqa.automationagent.grid.web.ReverseProxyHandler;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.net.Urls;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.HttpTracing;
import org.smartqa.automationagent.remote.tracing.Span;
import org.smartqa.automationagent.remote.tracing.Tracer;

import static org.smartqa.automationagent.remote.HttpSessionId.getSessionId;
import static org.smartqa.automationagent.remote.RemoteTags.SESSION_ID;
import static org.smartqa.automationagent.remote.tracing.Tags.HTTP_REQUEST;
import static org.smartqa.automationagent.remote.tracing.Tags.HTTP_RESPONSE;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

class HandleSession implements HttpHandler {

  private final Tracer tracer;
  private final HttpClient.Factory httpClientFactory;
  private final SessionMap sessions;
  private final Cache<SessionId, HttpHandler> knownSessions;

  HandleSession(
    Tracer tracer,
    HttpClient.Factory httpClientFactory,
    SessionMap sessions) {
    this.tracer = Require.nonNull("Tracer", tracer);
    this.httpClientFactory = Require.nonNull("HTTP client factory", httpClientFactory);
    this.sessions = Require.nonNull("Sessions", sessions);

    this.knownSessions = CacheBuilder.newBuilder()
      .expireAfterAccess(Duration.ofMinutes(1))
      .build();
  }

  @Override
  public HttpResponse execute(HttpRequest req) {
    try (Span span = HttpTracing.newSpanAsChildOf(tracer, req, "router.handle_session")) {
      HTTP_REQUEST.accept(span, req);

      SessionId id = getSessionId(req.getUri()).map(SessionId::new)
        .orElseThrow(() -> new NoSuchSessionException("Cannot find session: " + req));

      SESSION_ID.accept(span, id);

      try {
        HttpTracing.inject(tracer, span, req);
        HttpResponse res = knownSessions.get(id, loadSessionId(tracer, span, id)).execute(req);

        HTTP_RESPONSE.accept(span, res);

        return res;
      } catch (ExecutionException e) {
        span.setAttribute("error", true);
        span.setAttribute("error.message", e.getMessage());

        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) {
          throw (RuntimeException) cause;
        }
        throw new RuntimeException(cause);
      }
    }
  }

  private Callable<HttpHandler> loadSessionId(Tracer tracer, Span span, SessionId id) {
    return span.wrap(
      () -> {
        Session session = sessions.get(id);
          if (session instanceof HttpHandler) {
            return (HttpHandler) session;
          }
          HttpClient client = httpClientFactory.createClient(Urls.fromUri(session.getUri()));
          return new ReverseProxyHandler(tracer, client);
      }
    );
  }
}
