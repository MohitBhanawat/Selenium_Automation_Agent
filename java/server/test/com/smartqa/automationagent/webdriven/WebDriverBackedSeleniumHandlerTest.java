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

package com.smartqa.automationagent.webdriven;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.smartqa.automationagent.environment.GlobalTestEnvironment;
import org.smartqa.automationagent.environment.InProcessTestEnvironment;
import org.smartqa.automationagent.environment.TestEnvironment;
import org.smartqa.automationagent.environment.webserver.AppServer;
import org.smartqa.automationagent.grid.config.MapConfig;
import org.smartqa.automationagent.grid.server.BaseServerOptions;
import org.smartqa.automationagent.grid.server.Server;
import org.smartqa.automationagent.jre.server.JreServer;
import org.smartqa.automationagent.remote.server.ActiveSessions;
import org.smartqa.automationagent.remote.tracing.DefaultTestTracer;
import org.smartqa.automationagent.remote.tracing.Tracer;
import org.smartqa.automationagent.testing.Pages;

import com.smartqa.automationagent.AutomationAgent;
import com.smartqa.automationagent.DefaultAutomationAgent;
import com.smartqa.automationagent.webdriven.WebDriverBackedSeleniumHandler;

import static java.util.Collections.emptyMap;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.junit.Assert.assertTrue;
import static org.smartqa.automationagent.testing.Safely.safelyCall;

public class WebDriverBackedSeleniumHandlerTest {

  private Server<?> server;
  private int port;
  private AppServer appServer;
  private Pages pages;

  @Before
  public void setUpServer() {
    Tracer tracer = DefaultTestTracer.createTracer();

    // Register the emulator
    ActiveSessions sessions = new ActiveSessions(3, MINUTES);

    server = new JreServer(
      new BaseServerOptions(new MapConfig(emptyMap())),
      new WebDriverBackedSeleniumHandler(tracer, sessions));

    // Wait until the server is actually started.
    server.start();

    port = server.getUrl().getPort();
  }

  @Before
  public void prepTheEnvironment() {
    TestEnvironment environment = GlobalTestEnvironment.getOrCreate(InProcessTestEnvironment::new);
    appServer = environment.getAppServer();

    pages = new Pages(appServer);
  }

  @After
  public void stopServer() {
    safelyCall(() -> server.stop(), () -> appServer.stop());
  }

  @Test
  public void searchGoogle() {
    AutomationAgent
        automationAgent = new DefaultAutomationAgent("localhost", port, "*chrome", appServer.whereIs("/"));
    automationAgent.start();

    automationAgent.open(pages.simpleTestPage);
    String text = automationAgent.getBodyText();

    automationAgent.stop();
    assertTrue(text.contains("More than one line of text"));
  }
}
