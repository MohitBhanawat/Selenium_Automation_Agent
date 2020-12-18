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

import org.smartqa.automationagent.docker.DockerException;
import org.smartqa.automationagent.docker.internal.Reference;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.http.Contents;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;

import static org.smartqa.automationagent.json.Json.JSON_UTF_8;
import static org.smartqa.automationagent.json.Json.MAP_TYPE;
import static org.smartqa.automationagent.remote.http.HttpMethod.POST;

import java.util.Map;
import java.util.logging.Logger;

class PullImage {
  private static final Json JSON = new Json();
  private static final Logger LOG = Logger.getLogger(PullImage.class.getName());
  private final HttpHandler client;

  public PullImage(HttpHandler client) {
    this.client = Require.nonNull("HTTP client", client);
  }

  public void apply(Reference ref) {
    Require.nonNull("Reference to pull", ref);

    LOG.info("Pulling " + ref);

    HttpRequest req = new HttpRequest(POST, "/v1.40/images/create")
      .addHeader("Content-Type", JSON_UTF_8)
      .addHeader("Content-Length", "0")
      .addQueryParameter("fromImage", String.format("%s/%s", ref.getRepository(), ref.getName()));

    if (ref.getDigest() != null) {
      req.addQueryParameter("tag", ref.getDigest());
    } else if (ref.getTag() != null) {
      req.addQueryParameter("tag", ref.getTag());
    }

    HttpResponse res = client.execute(req);

    LOG.info("Have response from server");

    if (!res.isSuccessful()) {
      String message = "Unable to pull image: " + ref.getFamiliarName();

      try {
        Map<String, Object> value = JSON.toType(Contents.string(res), MAP_TYPE);
        message = (String) value.get("message");
      } catch (Exception e) {
        // fall through
      }

      throw new DockerException(message);
    }
  }
}
