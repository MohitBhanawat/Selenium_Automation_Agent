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

import com.google.common.collect.ImmutableMap;

import static org.smartqa.automationagent.remote.http.Contents.asJson;

import org.smartqa.automationagent.grid.data.DistributorStatus;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;

class StatusHandler implements HttpHandler {

  private final Distributor distributor;

  StatusHandler(Distributor distributor) {
    this.distributor = Require.nonNull("Distributor", distributor);
  }

  @Override
  public HttpResponse execute(HttpRequest req) {
    DistributorStatus status = distributor.getStatus();

    ImmutableMap<String, Object> report = ImmutableMap.of(
        "value", ImmutableMap.of(
            "ready", status.hasCapacity(),
            "message", status.hasCapacity() ? "Ready" : "No free slots available",
            "grid", status));

    return new HttpResponse().setContent(asJson(report));
  }
}
