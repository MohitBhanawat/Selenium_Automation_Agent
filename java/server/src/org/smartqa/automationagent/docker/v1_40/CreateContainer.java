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

import org.smartqa.automationagent.docker.Container;
import org.smartqa.automationagent.docker.ContainerId;
import org.smartqa.automationagent.docker.ContainerInfo;
import org.smartqa.automationagent.docker.DockerException;
import org.smartqa.automationagent.docker.DockerProtocol;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.json.JsonException;
import org.smartqa.automationagent.remote.http.Contents;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;

import static org.smartqa.automationagent.json.Json.JSON_UTF_8;
import static org.smartqa.automationagent.json.Json.MAP_TYPE;
import static org.smartqa.automationagent.remote.http.Contents.asJson;
import static org.smartqa.automationagent.remote.http.HttpMethod.POST;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class CreateContainer {
  private static final Json JSON = new Json();
  private static final Logger LOG = Logger.getLogger(CreateContainer.class.getName());
  private final DockerProtocol protocol;
  private final HttpHandler client;

  public CreateContainer(DockerProtocol protocol, HttpHandler client) {
    this.protocol = Require.nonNull("Protocol", protocol);
    this.client = Require.nonNull("HTTP client", client);
  }

  public Container apply(ContainerInfo info) {
    HttpResponse res = DockerMessages.throwIfNecessary(
      client.execute(
        new HttpRequest(POST, "/v1.40/containers/create")
          .addHeader("Content-Type", JSON_UTF_8)
          .setContent(asJson(info))),
      "Unable to create container: ",
      info);

    try {
      Map<String, Object> rawContainer = JSON.toType(Contents.string(res), MAP_TYPE);

      if (!(rawContainer.get("Id") instanceof String)) {
        throw new DockerException("Unable to read container id: " + rawContainer);
      }
      ContainerId id = new ContainerId((String) rawContainer.get("Id"));

      if (rawContainer.get("Warnings") instanceof Collection) {
        String allWarnings = ((Collection<?>) rawContainer.get("Warnings")).stream()
          .map(String::valueOf)
          .collect(Collectors.joining("\n", " * ", ""));

        LOG.info(String.format("Warnings while creating %s from %s: %s", id, info, allWarnings));
      }

      return new Container(protocol, id);
    } catch (JsonException | NullPointerException e) {
      throw new DockerException("Unable to create container from " + info);
    }
  }
}
