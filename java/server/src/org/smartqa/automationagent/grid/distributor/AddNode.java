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

package org.smartqa.automationagent.grid.distributor;

import static org.smartqa.automationagent.remote.http.Contents.string;

import org.smartqa.automationagent.grid.data.NodeStatus;
import org.smartqa.automationagent.grid.node.Node;
import org.smartqa.automationagent.grid.node.remote.RemoteNode;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.tracing.Tracer;

class AddNode implements HttpHandler {

  private final Tracer tracer;
  private final Distributor distributor;
  private final Json json;
  private final HttpClient.Factory httpFactory;

  AddNode(
      Tracer tracer,
      Distributor distributor,
      Json json,
      HttpClient.Factory httpFactory) {
    this.tracer = Require.nonNull("Tracer", tracer);
    this.distributor = Require.nonNull("Distributor", distributor);
    this.json = Require.nonNull("Json converter", json);
    this.httpFactory = Require.nonNull("HTTP Factory", httpFactory);
  }

  @Override
  public HttpResponse execute(HttpRequest req) {
    NodeStatus status = json.toType(string(req), NodeStatus.class);

    Node node = new RemoteNode(
        tracer,
        httpFactory,
        status.getNodeId(),
        status.getUri(),
        status.getStereotypes().keySet());

    distributor.add(node);

    return new HttpResponse();
  }
}
