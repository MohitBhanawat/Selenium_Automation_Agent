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

import static org.smartqa.automationagent.remote.RemoteTags.SESSION_ID;
import static org.smartqa.automationagent.remote.tracing.HttpTracing.newSpanAsChildOf;
import static org.smartqa.automationagent.remote.tracing.Tags.HTTP_REQUEST;

import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.Span;
import org.smartqa.automationagent.remote.tracing.Tracer;

class RemoveFromSession implements HttpHandler {

  private final Tracer tracer;
  private final SessionMap sessions;
  private final SessionId id;

  RemoveFromSession(Tracer tracer, SessionMap sessions, SessionId id) {
    this.tracer = Require.nonNull("Tracer", tracer);
    this.sessions = Require.nonNull("Session map", sessions);
    this.id = Require.nonNull("Session id", id);
  }

  @Override
  public HttpResponse execute(HttpRequest req) {
    try (Span span = newSpanAsChildOf(tracer, req, "sessions.remove_session")) {
      HTTP_REQUEST.accept(span, req);
      SESSION_ID.accept(span, id);

      sessions.remove(id);
      return new HttpResponse();
    }
  }
}
