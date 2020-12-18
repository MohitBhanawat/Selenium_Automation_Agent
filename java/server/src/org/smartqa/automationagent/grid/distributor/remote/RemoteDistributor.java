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

package org.smartqa.automationagent.grid.distributor.remote;

import org.smartqa.automationagent.SessionNotCreatedException;
import org.smartqa.automationagent.grid.data.CreateSessionResponse;
import org.smartqa.automationagent.grid.data.DistributorStatus;
import org.smartqa.automationagent.grid.distributor.Distributor;
import org.smartqa.automationagent.grid.node.Node;
import org.smartqa.automationagent.grid.web.Values;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.HttpTracing;
import org.smartqa.automationagent.remote.tracing.Tracer;

import static org.smartqa.automationagent.remote.http.Contents.asJson;
import static org.smartqa.automationagent.remote.http.HttpMethod.DELETE;
import static org.smartqa.automationagent.remote.http.HttpMethod.GET;
import static org.smartqa.automationagent.remote.http.HttpMethod.POST;

import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

public class RemoteDistributor extends Distributor {

  private static final Logger LOG = Logger.getLogger("AutomationAgent Distributor (Remote)");
  private final HttpHandler client;

  public RemoteDistributor(Tracer tracer, HttpClient.Factory factory, URL url) {
    super(tracer, factory);
    this.client = factory.createClient(url);
  }

  @Override
  public boolean isReady() {
    try {
      return client.execute(new HttpRequest(GET, "/readyz")).isSuccessful();
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public CreateSessionResponse newSession(HttpRequest request)
      throws SessionNotCreatedException {
    HttpRequest upstream = new HttpRequest(POST, "/se/grid/distributor/session");
    HttpTracing.inject(tracer, tracer.getCurrentContext(), upstream);
    upstream.setContent(request.getContent());

    HttpResponse response = client.execute(upstream);

    return Values.get(response, CreateSessionResponse.class);
  }

  @Override
  public RemoteDistributor add(Node node) {
    HttpRequest request = new HttpRequest(POST, "/se/grid/distributor/node");
    HttpTracing.inject(tracer, tracer.getCurrentContext(), request);
    request.setContent(asJson(node.getStatus()));

    HttpResponse response = client.execute(request);

    Values.get(response, Void.class);

    LOG.info(String.format("Added node %s.", node.getId()));

    return this;
  }

  @Override
  public void remove(UUID nodeId) {
    Require.nonNull("Node ID", nodeId);
    HttpRequest request = new HttpRequest(DELETE, "/se/grid/distributor/node/" + nodeId);
    HttpTracing.inject(tracer, tracer.getCurrentContext(), request);

    HttpResponse response = client.execute(request);

    Values.get(response, Void.class);
  }

  @Override
  public DistributorStatus getStatus() {
    HttpRequest request = new HttpRequest(GET, "/se/grid/distributor/status");
    HttpTracing.inject(tracer, tracer.getCurrentContext(), request);

    HttpResponse response = client.execute(request);

    return Values.get(response, DistributorStatus.class);
  }
}
