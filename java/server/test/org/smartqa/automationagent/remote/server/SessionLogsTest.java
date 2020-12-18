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

package org.smartqa.automationagent.remote.server;

import static org.junit.Assert.assertTrue;
import static org.smartqa.automationagent.json.Json.MAP_TYPE;
import static org.smartqa.automationagent.remote.http.Contents.string;
import static org.smartqa.automationagent.testing.drivers.Browser.CHROME;
import static org.smartqa.automationagent.testing.drivers.Browser.CHROMIUMEDGE;
import static org.smartqa.automationagent.testing.drivers.Browser.HTMLUNIT;
import static org.smartqa.automationagent.testing.drivers.Browser.IE;
import static org.smartqa.automationagent.testing.drivers.Browser.SAFARI;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.logging.SessionLogHandler;
import org.smartqa.automationagent.logging.SessionLogs;
import org.smartqa.automationagent.remote.LocalFileDetector;
import org.smartqa.automationagent.remote.RemoteWebDriver;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpMethod;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.testing.Ignore;
import org.smartqa.automationagent.testing.JUnit4TestBase;
import org.smartqa.automationagent.testing.drivers.Browser;
import org.smartqa.automationagent.testing.drivers.OutOfProcessSeleniumServer;
import org.smartqa.automationagent.testing.drivers.WebDriverBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

@Ignore(HTMLUNIT)
@Ignore(IE)
@Ignore(CHROME)
@Ignore(CHROMIUMEDGE)
@Ignore(SAFARI)
public class SessionLogsTest extends JUnit4TestBase {

  private static OutOfProcessSeleniumServer server;
  private RemoteWebDriver localDriver;

  @BeforeClass
  public static void startUpServer() throws IOException {
    server = new OutOfProcessSeleniumServer();
    server.enableLogCapture();
    server.start("standalone");
  }

  @AfterClass
  public static void stopServer() {
    server.stop();
  }

  @After
  public void stopDriver() {
    if (localDriver != null) {
      localDriver.quit();
      localDriver = null;
    }
  }

  private void startDriver() {
    Capabilities caps = WebDriverBuilder.getStandardCapabilitiesFor(Browser.detect());
    localDriver = new RemoteWebDriver(server.getWebDriverUrl(), caps);
    localDriver.setFileDetector(new LocalFileDetector());
  }

  @Test
  public void sessionLogsShouldContainAllAvailableLogTypes() throws Exception {
    startDriver();
    Set<String> logTypes = localDriver.manage().logs().getAvailableLogTypes();
    stopDriver();
    Map<String, SessionLogs> sessionMap =
        SessionLogHandler.getSessionLogs(getValueForPostRequest(server.getWebDriverUrl()));
    for (SessionLogs sessionLogs : sessionMap.values()) {
      for (String logType : logTypes) {
        assertTrue(String.format("Session logs should include available log type %s", logType),
                   sessionLogs.getLogTypes().contains(logType));
      }
    }
  }

  private static Map<String, Object> getValueForPostRequest(URL serverUrl) throws Exception {
    String url = serverUrl + "/logs";
    HttpClient.Factory factory = HttpClient.Factory.createDefault();
    HttpClient client = factory.createClient(new URL(url));
    HttpResponse response = client.execute(new HttpRequest(HttpMethod.POST, url));
    Map<String, Object> map = new Json().toType(string(response), MAP_TYPE);
    return (Map<String, Object>) map.get("value");
  }
}
