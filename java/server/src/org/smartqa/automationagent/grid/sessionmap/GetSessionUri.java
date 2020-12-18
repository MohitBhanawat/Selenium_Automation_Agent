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

package org.smartqa.automationagent.grid.sessionmap;

import static org.smartqa.automationagent.remote.http.Contents.asJson;

import java.io.UncheckedIOException;

import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;

class GetSessionUri implements HttpHandler {
  private final SessionMap sessionMap;
  private final SessionId sessionId;

  GetSessionUri(SessionMap sessionMap, SessionId sessionId) {
    this.sessionMap = Require.nonNull("Session map", sessionMap);
    this.sessionId = Require.nonNull("Session id", sessionId);
  }

  @Override
  public HttpResponse execute(HttpRequest req) throws UncheckedIOException {
    return new HttpResponse()
      .setContent(asJson(sessionMap.getUri(sessionId)));
  }
}
