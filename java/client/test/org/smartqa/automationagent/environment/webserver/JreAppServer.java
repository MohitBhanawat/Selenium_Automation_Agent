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

package org.smartqa.automationagent.environment.webserver;

import com.google.common.collect.ImmutableMap;

import org.smartqa.automationagent.grid.config.MapConfig;
import org.smartqa.automationagent.grid.server.BaseServerOptions;
import org.smartqa.automationagent.grid.server.Server;
import org.smartqa.automationagent.grid.web.PathResource;
import org.smartqa.automationagent.grid.web.ResourceHandler;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.jre.server.JreServer;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.net.PortProber;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpHandler;
import org.smartqa.automationagent.remote.http.HttpMethod;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.http.Route;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.MediaType.JSON_UTF_8;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;
import static org.smartqa.automationagent.chrome.InProjectLocate.locate;
import static org.smartqa.automationagent.remote.http.Contents.bytes;
import static org.smartqa.automationagent.remote.http.Contents.string;
import static org.smartqa.automationagent.remote.http.Route.get;
import static org.smartqa.automationagent.remote.http.Route.matching;
import static org.smartqa.automationagent.remote.http.Route.post;

public class JreAppServer implements AppServer {

  private final Server<?> server;

  public JreAppServer() {
    this(emulateJettyAppServer());
  }

  public JreAppServer(HttpHandler handler) {
    Require.nonNull("Handler", handler);

    int port = PortProber.findFreePort();
    server = new JreServer(
      new BaseServerOptions(new MapConfig(singletonMap("server", singletonMap("port", port)))),
      handler);
  }

  private static Route emulateJettyAppServer() {
    Path common = locate("common/src/web").toAbsolutePath();

    return Route.combine(
      new ResourceHandler(new PathResource(common)),
      get("/encoding").to(EncodingHandler::new),
      matching(req -> req.getUri().startsWith("/page/")).to(PageHandler::new),
      get("/redirect").to(() -> new RedirectHandler()),
      get("/sleep").to(SleepingHandler::new),
      post("/upload").to(UploadHandler::new));
  }

  @Override
  public void start() {
    server.start();
  }

  @Override
  public void stop() {
    server.stop();
  }

  @Override
  public String whereIs(String relativeUrl) {
    return createUrl("http", getHostName(), relativeUrl);
  }

  @Override
  public String whereElseIs(String relativeUrl) {
    return createUrl("http", getAlternateHostName(), relativeUrl);
  }

  @Override
  public String whereIsSecure(String relativeUrl) {
    return createUrl("https", getHostName(), relativeUrl);
  }

  @Override
  public String whereIsWithCredentials(String relativeUrl, String user, String password) {
    return String.format
        ("http://%s:%s@%s:%d/%s",
         user,
         password,
         getHostName(),
         server.getUrl().getPort(),
         relativeUrl);
  }

  private String createUrl(String protocol, String hostName, String relativeUrl) {
    if (!relativeUrl.startsWith("/")) {
      relativeUrl = "/" + relativeUrl;
    }

    try {
      return new URL(
          protocol,
          hostName,
          server.getUrl().getPort(),
          relativeUrl)
          .toString();
    } catch (MalformedURLException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public String create(Page page) {
    try {
      byte[] data = new Json()
          .toJson(ImmutableMap.of("content", page.toString()))
          .getBytes(UTF_8);

      HttpClient client = HttpClient.Factory.createDefault().createClient(new URL(whereIs("/")));
      HttpRequest request = new HttpRequest(HttpMethod.POST, "/common/createPage");
      request.setHeader(CONTENT_TYPE, JSON_UTF_8.toString());
      request.setContent(bytes(data));
      HttpResponse response = client.execute(request);
      return string(response);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String getHostName() {
    return "localhost";
  }

  @Override
  public String getAlternateHostName() {
    throw new UnsupportedOperationException("getAlternateHostName");
  }

  public static void main(String[] args) {
    JreAppServer server = new JreAppServer();
    server.start();

    System.out.println(server.whereIs("/"));
  }
}
