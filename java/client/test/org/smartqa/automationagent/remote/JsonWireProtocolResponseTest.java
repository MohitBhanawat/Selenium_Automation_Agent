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

package org.smartqa.automationagent.remote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.SessionNotCreatedException;
import org.smartqa.automationagent.WebDriverException;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.Dialect;
import org.smartqa.automationagent.remote.ErrorCodes;
import org.smartqa.automationagent.remote.InitialHandshakeResponse;
import org.smartqa.automationagent.remote.JsonWireProtocolResponse;
import org.smartqa.automationagent.remote.ProtocolHandshake;
import org.smartqa.automationagent.remote.Response;

import java.util.Map;

public class JsonWireProtocolResponseTest {

  @Test
  public void successfulResponseGetsParsedProperly() {
    Capabilities caps = new ImmutableCapabilities("cheese", "peas");
    Map<String, ?> payload =
        ImmutableMap.of(
            "status", 0,
            "value", caps.asMap(),
            "sessionId", "cheese is opaque");
    InitialHandshakeResponse initialResponse = new InitialHandshakeResponse(
        0,
        200,
        payload);

    ProtocolHandshake.Result result =
        new JsonWireProtocolResponse().getResponseFunction().apply(initialResponse);

    assertThat(result).isNotNull();
    assertThat(result.getDialect()).isEqualTo(Dialect.OSS);
    Response response = result.createResponse();

    assertThat(response.getState()).isEqualTo("success");
    assertThat((int) response.getStatus()).isEqualTo(0);

    assertThat(response.getValue()).isEqualTo(caps.asMap());
  }

  @Test
  public void shouldIgnoreAw3CProtocolReply() {
    Capabilities caps = new ImmutableCapabilities("cheese", "peas");
    Map<String, Map<String, Object>> payload =
        ImmutableMap.of(
            "value", ImmutableMap.of(
                "capabilities", caps.asMap(),
                "sessionId", "cheese is opaque"));
    InitialHandshakeResponse initialResponse = new InitialHandshakeResponse(
        0,
        200,
        payload);

    ProtocolHandshake.Result result =
        new JsonWireProtocolResponse().getResponseFunction().apply(initialResponse);

    assertThat(result).isNull();
  }

  @Test
  public void shouldIgnoreAGeckodriver013Reply() {
    Capabilities caps = new ImmutableCapabilities("cheese", "peas");
    Map<String, ?> payload =
        ImmutableMap.of(
            "value", caps.asMap(),
            "sessionId", "cheese is opaque");
    InitialHandshakeResponse initialResponse = new InitialHandshakeResponse(
        0,
        200,
        payload);

    ProtocolHandshake.Result result =
        new JsonWireProtocolResponse().getResponseFunction().apply(initialResponse);

    assertThat(result).isNull();
  }

  @Test
  public void shouldProperlyPopulateAnError() {
    WebDriverException exception = new SessionNotCreatedException("me no likey");
    Json json = new Json();

    Map<String, Object> payload = ImmutableMap.of(
        "value", json.toType(json.toJson(exception), Json.MAP_TYPE),
        "status", ErrorCodes.SESSION_NOT_CREATED);

    InitialHandshakeResponse initialResponse = new InitialHandshakeResponse(
        0,
        500,
        payload);

    assertThatExceptionOfType(SessionNotCreatedException.class)
        .isThrownBy(() -> new JsonWireProtocolResponse().getResponseFunction().apply(initialResponse))
        .withMessageContaining("me no likey");
  }
}
