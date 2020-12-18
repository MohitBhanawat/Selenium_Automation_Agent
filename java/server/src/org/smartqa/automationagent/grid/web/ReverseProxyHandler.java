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

package org.smartqa.automationagent.grid.web;

import com.google.common.collect.ImmutableSet;

import static org.smartqa.automationagent.remote.tracing.HttpTracing.newSpanAsChildOf;
import static org.smartqa.automationagent.remote.tracing.Tags.HTTP_REQUEST;

import java.io.UncheckedIOException;
import java.util.logging.Logger;

import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.Span;
import org.smartqa.automationagent.remote.tracing.Tracer;

public class ReverseProxyHandler implements HttpHandler {

  private static final Logger LOG = Logger.getLogger(ReverseProxyHandler.class.getName());

  private static final ImmutableSet<String> IGNORED_REQ_HEADERS = ImmutableSet.<String>builder()
      .add("connection")
      .add("keep-alive")
      .add("proxy-authorization")
      .add("proxy-authenticate")
      .add("proxy-connection")
      .add("te")
      .add("trailer")
      .add("transfer-encoding")
      .add("upgrade")
      .build();

  private final Tracer tracer;
  private final HttpClient upstream;

  public ReverseProxyHandler(Tracer tracer, HttpClient httpClient) {
    this.tracer = Require.nonNull("Tracer", tracer);
    this.upstream = Require.nonNull("HTTP client", httpClient);
  }

  @Override
  public HttpResponse execute(HttpRequest req) throws UncheckedIOException {
    try (Span span = newSpanAsChildOf(tracer, req, "reverse_proxy")) {
      HTTP_REQUEST.accept(span, req);

      HttpRequest toUpstream = new HttpRequest(req.getMethod(), req.getUri());

      for (String name : req.getQueryParameterNames()) {
        for (String value : req.getQueryParameters(name)) {
          toUpstream.addQueryParameter(name, value);
        }
      }

      for (String name : req.getHeaderNames()) {
        if (IGNORED_REQ_HEADERS.contains(name.toLowerCase())) {
          continue;
        }

        for (String value : req.getHeaders(name)) {
          toUpstream.addHeader(name, value);
        }
      }
      // None of this "keep alive" nonsense.
      toUpstream.setHeader("Connection", "keep-alive");

      toUpstream.setContent(req.getContent());
      HttpResponse resp = upstream.execute(toUpstream);

      span.setAttribute("http.status", resp.getStatus());

      // clear response defaults.
      resp.removeHeader("Date");
      resp.removeHeader("Server");

      IGNORED_REQ_HEADERS.forEach(resp::removeHeader);

      return resp;
    }
  }
}
