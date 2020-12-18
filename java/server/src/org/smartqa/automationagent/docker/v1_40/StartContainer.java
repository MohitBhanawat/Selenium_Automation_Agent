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

package org.smartqa.automationagent.docker.v1_40;

import static org.smartqa.automationagent.docker.v1_40.DockerMessages.throwIfNecessary;
import static org.smartqa.automationagent.remote.http.HttpMethod.POST;

import org.smartqa.automationagent.docker.ContainerId;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;

class StartContainer {
  private final HttpHandler client;

  public StartContainer(HttpHandler client) {
    this.client = Require.nonNull("HTTP client", client);
  }

  public void apply(ContainerId id) {
    Require.nonNull("Container id", id);

    throwIfNecessary(
      client.execute(
        new HttpRequest(POST, String.format("/v1.40/containers/%s/start", id))
          .addHeader("Content-Length", "0")
          .addHeader("Content-Type", "text/plain")),
      "Unable to start container: %s",
      id);
  }
}
