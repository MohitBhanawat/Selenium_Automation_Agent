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

import com.google.common.collect.ImmutableMap;

import org.smartqa.automationagent.SessionNotCreatedException;
import org.smartqa.automationagent.grid.session.ActiveSession;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.logging.LogType;
import org.smartqa.automationagent.logging.LoggingPreferences;
import org.smartqa.automationagent.remote.CapabilityType;
import org.smartqa.automationagent.remote.NewSessionPayload;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.server.ActiveSessions;
import org.smartqa.automationagent.remote.server.NewSessionPipeline;
import org.smartqa.automationagent.remote.server.log.LoggingManager;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.logging.Level;

import static com.google.common.net.MediaType.JSON_UTF_8;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.smartqa.automationagent.remote.http.Contents.bytes;
import static org.smartqa.automationagent.remote.http.Contents.reader;

public class BeginSession implements HttpHandler {

  private final NewSessionPipeline pipeline;
  private final ActiveSessions allSessions;
  private final Json json;

  public BeginSession(NewSessionPipeline pipeline, ActiveSessions allSessions, Json json) {
    this.pipeline = pipeline;
    this.allSessions = allSessions;
    this.json = json;
  }

  @Override
  public HttpResponse execute(HttpRequest req) throws UncheckedIOException {
    ActiveSession session;
    try (Reader reader = reader(req);
         NewSessionPayload payload = NewSessionPayload.create(reader)) {
      session = pipeline.createNewSession(payload);
      allSessions.put(session);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    // Force capture of server-side logs since we don't have easy access to the request from the
    // local end.
    LoggingPreferences loggingPrefs = new LoggingPreferences();
    loggingPrefs.enable(LogType.SERVER, Level.INFO);
    Object raw = session.getCapabilities().get(CapabilityType.LOGGING_PREFS);
    if (raw instanceof LoggingPreferences) {
      loggingPrefs.addPreferences((LoggingPreferences) raw);
    }

    LoggingManager.perSessionLogHandler().configureLogging(loggingPrefs);
    LoggingManager.perSessionLogHandler().attachToCurrentThread(session.getId());

    // Only servers implementing the server-side webdriver-backed automationagent need to return this
    // particular value.
    Map<String, Object> caps;
    if (session.getCapabilities().containsKey("webdriver.remote.sessionid")) {
      caps = session.getCapabilities();
    } else {
      caps = ImmutableMap.<String, Object>builder()
          .putAll(session.getCapabilities())
          .put("webdriver.remote.sessionid", session.getId().toString())
          .build();
    }

    Object toConvert;
      switch (session.getDownstreamDialect()) {
        case OSS:
          toConvert = ImmutableMap.of(
              "status", 0,
              "sessionId", session.getId().toString(),
              "value", caps);
          break;

        case W3C:
          toConvert = ImmutableMap.of(
              "value", ImmutableMap.of(
                  "sessionId", session.getId().toString(),
                  "capabilities", caps));
          break;

        default:
          throw new SessionNotCreatedException(
              "Unrecognized downstream dialect: " + session.getDownstreamDialect());
      }

      byte[] payload = json.toJson(toConvert).getBytes(UTF_8);

    return new HttpResponse().setStatus(HTTP_OK)
      .setHeader("Cache-Control", "no-cache")
      .setHeader("Content-Type", JSON_UTF_8.toString())
      .setHeader("Content-Length", String.valueOf(payload.length))
      .setContent(bytes(payload));
  }
}
