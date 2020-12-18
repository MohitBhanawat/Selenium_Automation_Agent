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
import org.smartqa.automationagent.grid.web.ProtocolConverter;
import org.smartqa.automationagent.grid.web.ReverseProxyHandler;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.Dialect;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.Tracer;

import static org.smartqa.automationagent.remote.http.HttpMethod.DELETE;

import java.net.URL;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class ProtocolConvertingSession extends BaseActiveSession {

  private final HttpHandler handler;
  private final String killUrl;

  protected ProtocolConvertingSession(
      Tracer tracer,
      HttpClient client,
      SessionId id,
      URL url,
      Dialect downstream,
      Dialect upstream,
      Capabilities capabilities) {
    super(id, url, downstream, upstream, capabilities);

    Require.nonNull("HTTP client", client);

    if (downstream.equals(upstream)) {
      this.handler = new ReverseProxyHandler(tracer, client);
    } else {
      this.handler = new ProtocolConverter(tracer, client, downstream, upstream);
    }

    this.killUrl = "/session/" + id;
  }

  @Override
  public HttpResponse execute(HttpRequest req) {
    String host = "host";
    StreamSupport.stream(req.getHeaderNames().spliterator(), true)
      .filter(host::equalsIgnoreCase)
      .collect(Collectors.toList())
      .forEach(req::removeHeader);
    req.addHeader(host, String.format("%s:%s", getUri().getHost(), getUri().getPort()));
    HttpResponse res = handler.execute(req);
    if (req.getMethod() == DELETE && killUrl.equals(req.getUri())) {
      stop();
    }
    return res;
  }
}
