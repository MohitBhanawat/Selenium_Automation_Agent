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

import java.net.URL;

import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.http.ClientConfig;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.http.WebSocket;

public class TracedHttpClient implements HttpClient {

  private final Tracer tracer;
  private final HttpClient delegate;

  private TracedHttpClient(Tracer tracer, HttpClient delegate) {
    this.tracer = Require.nonNull("Tracer", tracer);
    this.delegate = Require.nonNull("Actual handler", delegate);
  }

  @Override
  public WebSocket openSocket(HttpRequest request, WebSocket.Listener listener) {
    return delegate.openSocket(request, listener);
  }

  @Override
  public HttpResponse execute(HttpRequest req) {
    try (Span span = newSpanAsChildOf(tracer, req, "httpclient.execute")) {
      KIND.accept(span, Span.Kind.CLIENT);
      HTTP_REQUEST.accept(span, req);
      tracer.getPropagator().inject(span, req, (r, key, value) -> r.setHeader(key, value));
      HttpResponse response = delegate.execute(req);
      HTTP_RESPONSE.accept(span, response);
      return response;
    }
  }

  public static class Factory implements HttpClient.Factory {

    private final Tracer tracer;
    private final HttpClient.Factory delegate;

    public Factory(Tracer tracer, HttpClient.Factory delegate) {
      this.tracer = Require.nonNull("Tracer", tracer);
      this.delegate = Require.nonNull("Actual handler", delegate);
    }

    public HttpClient createClient(ClientConfig config) {
      HttpClient client = delegate.createClient(config);
      return new TracedHttpClient(tracer, client);
    }

    @Override
    public HttpClient createClient(URL url) {
      HttpClient client = delegate.createClient(url);
      return new TracedHttpClient(tracer, client);
    }
  }
}
