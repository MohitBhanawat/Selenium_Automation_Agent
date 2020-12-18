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

package org.smartqa.automationagent.testing.drivers;

import static org.smartqa.automationagent.remote.http.Contents.string;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.firefox.FirefoxOptions;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.LocalFileDetector;
import org.smartqa.automationagent.remote.RemoteWebDriver;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpMethod;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.support.ui.FluentWait;
import org.smartqa.automationagent.support.ui.Wait;

public class GridSupplier implements Supplier<WebDriver> {

  private static OutOfProcessSeleniumServer hub;
  private static OutOfProcessSeleniumServer node;
  private static volatile boolean started;

  private final Capabilities desired;

  public GridSupplier(Capabilities desired) {
    this.desired = desired;
  }

  @Override
  public WebDriver get() {
    if (desired == null || !Boolean.getBoolean("automationagent.browser.grid")) {
      return null;
    }

    if (!started) {
      startServers();
    }

    RemoteWebDriver driver = new RemoteWebDriver(hub.getWebDriverUrl(), desired);
    driver.setFileDetector(new LocalFileDetector());
    return driver;
  }

  private synchronized void startServers() {
    if (started) {
      return;
    }

    try {
      hub = new OutOfProcessSeleniumServer().start("-role", "hub");

      URL hubUrl = hub.getWebDriverUrl();
      hubUrl = new URL(hubUrl.getProtocol(), hubUrl.getHost(), hubUrl.getPort(), hubUrl.getFile());

      node = new OutOfProcessSeleniumServer().start(
          "-role", "node",
          "-hub", String.valueOf(hubUrl));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Keep polling the status page of the hub until it claims to be ready
    HttpClient client = HttpClient.Factory.createDefault().createClient(hub.getWebDriverUrl());
    Json json = new Json();
    Wait<HttpClient> wait = new FluentWait<>(client)
        .ignoring(RuntimeException.class)
        .withTimeout(Duration.ofSeconds(30));
    wait.until(c -> {
      HttpRequest req = new HttpRequest(HttpMethod.GET, "/status");
      HttpResponse response = c.execute(req);
      Map<?, ?> value = json.toType(string(response), Map.class);

      return ((Map<?, ?>) value.get("value")).get("ready") == Boolean.TRUE;
    });

    started = true;
  }

  public static void main(String[] args) {
    System.setProperty("automationagent.browser.grid", "true");
    WebDriver driver = new GridSupplier(new FirefoxOptions()).get();
    driver.get("http://www.google.com");
  }
}
