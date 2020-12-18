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

package org.smartqa.automationagent.chromium;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebDriverException;
import org.smartqa.automationagent.devtools.Connection;
import org.smartqa.automationagent.devtools.DevTools;
import org.smartqa.automationagent.devtools.HasDevTools;
import org.smartqa.automationagent.html5.LocalStorage;
import org.smartqa.automationagent.html5.Location;
import org.smartqa.automationagent.html5.LocationContext;
import org.smartqa.automationagent.html5.SessionStorage;
import org.smartqa.automationagent.html5.WebStorage;
import org.smartqa.automationagent.interactions.HasTouchScreen;
import org.smartqa.automationagent.interactions.TouchScreen;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.mobile.NetworkConnection;
import org.smartqa.automationagent.remote.CommandExecutor;
import org.smartqa.automationagent.remote.FileDetector;
import org.smartqa.automationagent.remote.RemoteTouchScreen;
import org.smartqa.automationagent.remote.RemoteWebDriver;
import org.smartqa.automationagent.remote.html5.RemoteLocationContext;
import org.smartqa.automationagent.remote.html5.RemoteWebStorage;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.mobile.RemoteNetworkConnection;

/**
 * A {@link WebDriver} implementation that controls a Chromium browser running on the local machine.
 * This class is provided as a convenience for easily testing the Chromium browser. The control server
 * which each instance communicates with will live and die with the instance.
 *
 * To avoid unnecessarily restarting the ChromiumDriver server with each instance, use a
 * {@link RemoteWebDriver} coupled with the desired WebDriverService, which is managed
 * separately.
 *
 * Note that unlike ChromiumDriver, RemoteWebDriver doesn't directly implement
 * role interfaces such as {@link LocationContext} and {@link WebStorage}.
 * Therefore, to access that functionality, it needs to be
 * {@link org.smartqa.automationagent.remote.Augmenter augmented} and then cast
 * to the appropriate interface.
 */
public class ChromiumDriver extends RemoteWebDriver
    implements HasDevTools, HasTouchScreen, LocationContext, NetworkConnection, WebStorage {

  private final RemoteLocationContext locationContext;
  private final RemoteWebStorage webStorage;
  private final TouchScreen touchScreen;
  private final RemoteNetworkConnection networkConnection;
  private final Optional<Connection> connection;
  private final Optional<DevTools> devTools;

  protected ChromiumDriver(CommandExecutor commandExecutor, Capabilities capabilities, String capabilityKey) {
    super(commandExecutor, capabilities);
    locationContext = new RemoteLocationContext(getExecuteMethod());
    webStorage = new RemoteWebStorage(getExecuteMethod());
    touchScreen = new RemoteTouchScreen(getExecuteMethod());
    networkConnection = new RemoteNetworkConnection(getExecuteMethod());

    HttpClient.Factory factory = HttpClient.Factory.createDefault();
    connection = ChromiumDevToolsLocator.getChromeConnector(
        factory,
        getCapabilities(),
        capabilityKey);
    devTools = connection.map(DevTools::new);
  }

  @Override
  public void setFileDetector(FileDetector detector) {
    throw new WebDriverException(
        "Setting the file detector only works on remote webdriver instances obtained " +
        "via RemoteWebDriver");
  }

  @Override
  public LocalStorage getLocalStorage() {
    return webStorage.getLocalStorage();
  }

  @Override
  public SessionStorage getSessionStorage() {
    return webStorage.getSessionStorage();
  }

  @Override
  public Location location() {
    return locationContext.location();
  }

  @Override
  public void setLocation(Location location) {
    locationContext.setLocation(location);
  }

  @Override
  public TouchScreen getTouch() {
    return touchScreen;
  }

  @Override
  public ConnectionType getNetworkConnection() {
    return networkConnection.getNetworkConnection();
  }

  @Override
  public ConnectionType setNetworkConnection(ConnectionType type) {
    return networkConnection.setNetworkConnection(type);
  }

  /**
   * Launches Chrome app specified by id.
   *
   * @param id Chrome app id.
   */
  public void launchApp(String id) {
    execute(ChromiumDriverCommand.LAUNCH_APP, ImmutableMap.of("id", id));
  }

  /**
   * Execute a Chrome Devtools Protocol command and get returned result. The
   * command and command args should follow
   * <a href="https://chromedevtools.github.io/devtools-protocol/">chrome
   * devtools protocol domains/commands</a>.
   */
  public Map<String, Object> executeCdpCommand(String commandName, Map<String, Object> parameters) {
    Require.nonNull("Command name", commandName);
    Require.nonNull("Parameters", parameters);

    @SuppressWarnings("unchecked")
    Map<String, Object> toReturn = (Map<String, Object>) getExecuteMethod().execute(
        ChromiumDriverCommand.EXECUTE_CDP_COMMAND,
        ImmutableMap.of("cmd", commandName, "params", parameters));

    return ImmutableMap.copyOf(toReturn);
  }

  @Override
  public DevTools getDevTools() {
    return devTools.orElseThrow(() -> new WebDriverException("Unable to create DevTools connection"));
  }

  public String getCastSinks() {
    Object response =  getExecuteMethod().execute(ChromiumDriverCommand.GET_CAST_SINKS, null);
    return response.toString();
  }

  public String getCastIssueMessage() {
    Object response = getExecuteMethod().execute(ChromiumDriverCommand.GET_CAST_ISSUE_MESSAGE, null);
    return response.toString();
  }

  public void selectCastSink(String deviceName) {
    Object response =  getExecuteMethod().execute(ChromiumDriverCommand.SET_CAST_SINK_TO_USE, ImmutableMap.of("sinkName", deviceName));
  }

  public void startTabMirroring(String deviceName) {
    Object response =  getExecuteMethod().execute(ChromiumDriverCommand.START_CAST_TAB_MIRRORING, ImmutableMap.of("sinkName", deviceName));
  }

  public void stopCasting(String deviceName) {
    Object response = getExecuteMethod().execute(ChromiumDriverCommand.STOP_CASTING, ImmutableMap.of("sinkName", deviceName));
  }

  public void setPermission(String name, String value) {
    Object response = getExecuteMethod().execute(ChromiumDriverCommand.SET_PERMISSION,
      ImmutableMap.of("descriptor", ImmutableMap.of("name", name), "state", value));
  }

  @Override
  public void quit() {
    connection.ifPresent(Connection::close);
    super.quit();
  }
}
