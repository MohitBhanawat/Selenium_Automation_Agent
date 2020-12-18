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

import org.smartqa.automationagent.grid.data.CreateSessionRequest;
import org.smartqa.automationagent.grid.data.CreateSessionResponse;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;

import static org.smartqa.automationagent.remote.http.Contents.asJson;
import static org.smartqa.automationagent.remote.http.Contents.string;

import java.io.UncheckedIOException;
import java.util.HashMap;

class NewNodeSession implements HttpHandler {

  private final Node node;
  private final Json json;

  NewNodeSession(Node node, Json json) {
    this.node = Require.nonNull("Node", node);
    this.json = Require.nonNull("Json converter", json);
  }

  @Override
  public HttpResponse execute(HttpRequest req) throws UncheckedIOException {
    CreateSessionRequest incoming = json.toType(string(req), CreateSessionRequest.class);

    CreateSessionResponse sessionResponse = node.newSession(incoming).orElse(null);

    HashMap<String, Object> value = new HashMap<>();
    value.put("value", sessionResponse);

    return new HttpResponse().setContent(asJson(value));
  }
}
