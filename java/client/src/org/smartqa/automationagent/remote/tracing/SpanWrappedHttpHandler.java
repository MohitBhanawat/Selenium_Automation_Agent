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

package org.smartqa.automationagent.remote.tracing;

import static org.smartqa.automationagent.remote.tracing.HttpTracing.newSpanAsChildOf;
import static org.smartqa.automationagent.remote.tracing.Tags.HTTP_REQUEST;
import static org.smartqa.automationagent.remote.tracing.Tags.HTTP_RESPONSE;
import static org.smartqa.automationagent.remote.tracing.Tags.KIND;

import java.io.UncheckedIOException;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;

public class SpanWrappedHttpHandler implements HttpHandler {

  private static final Logger LOG = Logger.getLogger(SpanWrappedHttpHandler.class.getName());
  private final Tracer tracer;
  private final Function<HttpRequest, String> namer;
  private final HttpHandler delegate;

  public SpanWrappedHttpHandler(Tracer tracer, Function<HttpRequest, String> namer, HttpHandler delegate) {
    this.tracer = Require.nonNull("Tracer", tracer);
    this.namer = Require.nonNull("Naming function", namer);
    this.delegate = Require.nonNull("Actual handler", delegate);
  }

  @Override
  public HttpResponse execute(HttpRequest req) throws UncheckedIOException {
    // If there is already a span attached to this request, then do nothing.
    Object possibleSpan = req.getAttribute("automationagent.tracing.span");
    if (possibleSpan instanceof Span) {
      return delegate.execute(req);
    }

    String name = Require.state("Operation name", namer.apply(req)).nonNull("must be set for %s", req);

    TraceContext before = tracer.getCurrentContext();
    Span span = newSpanAsChildOf(tracer, req, name);
    try {
      TraceContext after = tracer.getCurrentContext();
      span.setAttribute("random.key", UUID.randomUUID().toString());

      req.setAttribute("automationagent.tracing.span", span);

      if (!(after.getClass().getName().equals("org.smartqa.automationagent.remote.tracing.empty.NullContext"))) {
        LOG.fine(String.format("Wrapping request. Before %s and after %s", before, after));
      }

      KIND.accept(span, Span.Kind.SERVER);
      HTTP_REQUEST.accept(span, req);
      HttpTracing.inject(tracer, span, req);

      HttpResponse res = delegate.execute(req);

      HTTP_RESPONSE.accept(span, res);

      return res;
    } catch (Throwable t) {
      span.setAttribute("error", true);
      span.setStatus(Status.UNKNOWN.withDescription(t.getMessage()));
      LOG.log(Level.WARNING, "Unable to execute request: " + t.getMessage(), t);
      throw t;
    } finally {
      span.close();
    }
  }
}
