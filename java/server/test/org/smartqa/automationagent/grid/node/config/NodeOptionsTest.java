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

package org.smartqa.automationagent.grid.node.config;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.smartqa.automationagent.Platform;
import org.smartqa.automationagent.WebDriverInfo;
import org.smartqa.automationagent.chrome.ChromeDriverInfo;
import org.smartqa.automationagent.events.EventBus;
import org.smartqa.automationagent.events.local.GuavaEventBus;
import org.smartqa.automationagent.grid.config.Config;
import org.smartqa.automationagent.grid.config.MapConfig;
import org.smartqa.automationagent.grid.node.config.NodeOptions;
import org.smartqa.automationagent.grid.node.local.LocalNode;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.tracing.DefaultTestTracer;
import org.smartqa.automationagent.remote.tracing.Tracer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.spy;

public class NodeOptionsTest {

  private Tracer tracer;
  private HttpClient.Factory clientFactory;
  private LocalNode.Builder builder;
  private LocalNode.Builder builderSpy;

  @Before
  public void setUp() throws URISyntaxException {
    tracer = DefaultTestTracer.createTracer();
    EventBus bus = new GuavaEventBus();
    clientFactory = HttpClient.Factory.createDefault();
    URI uri = new URI("http://localhost:1234");
    builder = LocalNode.builder(tracer, bus, uri, uri, null);
    builderSpy = spy(builder);
  }

  @Test
  public void canConfigureNodeWithDriverDetection() {
    assumeFalse("We don't have driver servers in PATH when we run unit tests",
                Boolean.parseBoolean(System.getenv("TRAVIS")));
    assumeTrue("ChromeDriver needs to be available", new ChromeDriverInfo().isAvailable());

    Config config = new MapConfig(singletonMap(
        "node", singletonMap("detect-drivers", "true")));

    List<WebDriverInfo> reported = new ArrayList<>();
    new NodeOptions(config).getSessionFactories(info -> {
      reported.add(info);
      return Collections.emptySet();
    });

    String expected = new ChromeDriverInfo().getDisplayName();

    reported.stream()
      .filter(info -> expected.equals(info.getDisplayName()))
      .findFirst()
      .orElseThrow(() -> new AssertionError("Unable to find Chrome info"));
  }

  @Test
  public void shouldDetectCorrectDriversOnWindows() {
    assumeTrue(Platform.getCurrent().is(Platform.WINDOWS));
    assumeFalse("We don't have driver servers in PATH when we run unit tests",
                Boolean.getBoolean("TRAVIS"));

    Config config = new MapConfig(singletonMap(
        "node", singletonMap("detect-drivers", "true")));
    List<WebDriverInfo> reported = new ArrayList<>();
    new NodeOptions(config).getSessionFactories(info -> {
      reported.add(info);
      return Collections.emptySet();
    });

    assertThat(reported).is(supporting("chrome"));
    assertThat(reported).is(supporting("firefox"));
    assertThat(reported).is(supporting("internet explorer"));
    assertThat(reported).is(supporting("MicrosoftEdge"));
    assertThat(reported).isNot(supporting("safari"));
  }


  @Test
  public void shouldDetectCorrectDriversOnMac() {
    assumeTrue(Platform.getCurrent().is(Platform.MAC));
    assumeFalse("We don't have driver servers in PATH when we run unit tests",
                Boolean.getBoolean("TRAVIS"));

    Config config = new MapConfig(singletonMap(
        "node", singletonMap("detect-drivers", "true")));
    List<WebDriverInfo> reported = new ArrayList<>();
    new NodeOptions(config).getSessionFactories(info -> {
      reported.add(info);
      return Collections.emptySet();
    });

    LocalNode node = builder.build();
    assertThat(reported).is(supporting("chrome"));
    assertThat(reported).is(supporting("firefox"));
    assertThat(reported).isNot(supporting("internet explorer"));
    assertThat(reported).is(supporting("MicrosoftEdge"));
    assertThat(reported).is(supporting("safari"));
  }

  @Test
  public void canConfigureNodeWithoutDriverDetection() {
    Config config = new MapConfig(singletonMap(
        "node", singletonMap("detect-drivers", "false")));
    List<WebDriverInfo> reported = new ArrayList<>();
    new NodeOptions(config).getSessionFactories(info -> {
      reported.add(info);
      return Collections.emptySet();
    });

    assertThat(reported).isEmpty();
  }

  @Test
  public void doNotDetectDriversByDefault() {
    Config config = new MapConfig(emptyMap());

    List<WebDriverInfo> reported = new ArrayList<>();
    new NodeOptions(config).getSessionFactories(info -> {
      reported.add(info);
      return Collections.emptySet();
    });

    assertThat(reported).isEmpty();
  }

  private Condition<? super List<? extends WebDriverInfo>> supporting(String name) {
    return new Condition<>(
      infos -> infos.stream().anyMatch(info -> name.equals(info.getCanonicalCapabilities().getBrowserName())),
      "supporting %s",
      name);
  }
}
