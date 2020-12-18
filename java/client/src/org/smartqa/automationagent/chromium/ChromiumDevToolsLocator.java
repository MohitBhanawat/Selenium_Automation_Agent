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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.devtools.Connection;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.remote.http.ClientConfig;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.smartqa.automationagent.json.Json.MAP_TYPE;
import static org.smartqa.automationagent.remote.http.Contents.string;
import static org.smartqa.automationagent.remote.http.HttpMethod.GET;

public class ChromiumDevToolsLocator {

  private static final Json JSON = new Json();
  private static final Logger LOG = Logger.getLogger(ChromiumDevToolsLocator.class.getName());

  public static Optional<URI> getReportedUri(String capabilityKey, Capabilities caps) {
    Object raw = caps.getCapability(capabilityKey);
    if (!(raw instanceof Map)) {
      LOG.fine("No capabilities for " + capabilityKey);
      return Optional.empty();
    }

    raw = ((Map<?, ?>) raw).get("debuggerAddress");
    if (!(raw instanceof String)) {
      LOG.fine("No debugger address");
      return Optional.empty();
    }

    int index = ((String) raw).lastIndexOf(':');
    if (index == -1 || index == ((String) raw).length() - 1) {
      LOG.fine("No index in " + raw);
      return Optional.empty();
    }

    try {
      URI uri = new URI(String.format("http://%s", raw));
      LOG.fine("URI found: " + uri);
      return Optional.of(uri);
    } catch (URISyntaxException e) {
      LOG.warning("Unable to creeate URI from: " + raw);
      return Optional.empty();
    }
  }

  public static Optional<URI> getCdpEndPoint(
    HttpClient.Factory clientFactory,
    URI reportedUri) {
    Require.nonNull("HTTP client factory", clientFactory);
    Require.nonNull("DevTools URI", reportedUri);

    ClientConfig config = ClientConfig.defaultConfig().baseUri(reportedUri);
    HttpClient client = clientFactory.createClient(config);

    HttpResponse res = client.execute(new HttpRequest(GET, "/json/version"));
    if (res.getStatus() != HTTP_OK) {
      return Optional.empty();
    }

    Map<String, Object> versionData = JSON.toType(string(res), MAP_TYPE);
    Object raw = versionData.get("webSocketDebuggerUrl");

    if (!(raw instanceof String)) {
      return Optional.empty();
    }

    String debuggerUrl = (String) raw;
    try {
      return Optional.of(new URI(debuggerUrl));
    } catch (URISyntaxException e) {
      LOG.warning("Invalid URI for endpoint " + raw);
      return Optional.empty();
    }
  }

  public static Optional<Connection> getChromeConnector(
      HttpClient.Factory clientFactory,
      Capabilities caps,
      String capabilityKey) {

    try {
      return getReportedUri(capabilityKey, caps)
        .flatMap(uri -> getCdpEndPoint(clientFactory, uri))
        .map(uri -> new Connection(
          clientFactory.createClient(ClientConfig.defaultConfig().baseUri(uri)),
          uri.toString()));
    } catch (Exception e) {
      LOG.log(Level.WARNING, "Unable to create CDP connection", e);
      return Optional.empty();
    }
  }
}
