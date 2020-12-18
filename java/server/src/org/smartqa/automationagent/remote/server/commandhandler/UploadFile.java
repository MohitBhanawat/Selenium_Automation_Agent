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

package org.smartqa.automationagent.remote.server.commandhandler;

import org.smartqa.automationagent.WebDriverException;
import org.smartqa.automationagent.grid.session.ActiveSession;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.io.Zip;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.ErrorCodes;
import org.smartqa.automationagent.remote.Response;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;

import static org.smartqa.automationagent.remote.http.Contents.string;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

public class UploadFile implements HttpHandler {

  private final Json json;
  private final ActiveSession session;

  public UploadFile(Json json, ActiveSession session) {
    this.json = Require.nonNull("Json converter", json);
    this.session = Require.nonNull("Current session", session);
  }

  @Override
  public HttpResponse execute(HttpRequest req) throws UncheckedIOException {
    Map<String, Object> args = json.toType(string(req), Json.MAP_TYPE);
    String file = (String) args.get("file");

    File tempDir = session.getFileSystem().createTempDir("upload", "file");

    try {
      Zip.unzip(file, tempDir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    // Select the first file
    File[] allFiles = tempDir.listFiles();

    Response response = new Response(session.getId());
    if (allFiles == null || allFiles.length != 1) {
      response.setStatus(ErrorCodes.UNHANDLED_ERROR);
      response.setValue(new WebDriverException(
          "Expected there to be only 1 file. There were: " +
          (allFiles == null ? 0 : allFiles.length)));
    } else {
      response.setStatus(ErrorCodes.SUCCESS);
      response.setValue(allFiles[0].getAbsolutePath());
    }

    HttpResponse resp = new HttpResponse();
    session.getDownstreamDialect().getResponseCodec().encode(() -> resp, response);
    return resp;
  }
}
