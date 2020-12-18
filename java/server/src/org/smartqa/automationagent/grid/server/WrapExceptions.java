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

package org.smartqa.automationagent.grid.server;

import static org.smartqa.automationagent.remote.http.Contents.asJson;

import org.smartqa.automationagent.grid.web.ErrorCodec;
import org.smartqa.automationagent.remote.http.Filter;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpResponse;

import static com.google.common.net.MediaType.JSON_UTF_8;

public class WrapExceptions implements Filter {

  private final ErrorCodec errors = ErrorCodec.createDefault();

  @Override
  public HttpHandler apply(HttpHandler next) {
    return req -> {
      try {
        return next.execute(req);
      } catch (Throwable cause) {
        HttpResponse res = new HttpResponse();
        res.setStatus(errors.getHttpStatusCode(cause));

        res.addHeader("Content-Type", JSON_UTF_8.toString());
        res.addHeader("Cache-Control", "none");

        res.setContent(asJson(errors.encode(cause)));

        return res;
      }
    };
  }
}
