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

package org.smartqa.automationagent.grid.router.httpd;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.smartqa.automationagent.BuildInfo;
import org.smartqa.automationagent.cli.CliCommand;
import org.smartqa.automationagent.grid.TemplateGridCommand;
import org.smartqa.automationagent.grid.config.Config;
import org.smartqa.automationagent.grid.config.MapConfig;
import org.smartqa.automationagent.grid.config.Role;
import org.smartqa.automationagent.grid.distributor.Distributor;
import org.smartqa.automationagent.grid.distributor.config.DistributorOptions;
import org.smartqa.automationagent.grid.distributor.remote.RemoteDistributor;
import org.smartqa.automationagent.grid.graphql.GraphqlHandler;
import org.smartqa.automationagent.grid.log.LoggingOptions;
import org.smartqa.automationagent.grid.router.ProxyCdpIntoGrid;
import org.smartqa.automationagent.grid.router.Router;
import org.smartqa.automationagent.grid.server.BaseServerOptions;
import org.smartqa.automationagent.grid.server.NetworkOptions;
import org.smartqa.automationagent.grid.server.Server;
import org.smartqa.automationagent.grid.sessionmap.SessionMap;
import org.smartqa.automationagent.grid.sessionmap.config.SessionMapOptions;
import org.smartqa.automationagent.netty.server.NettyServer;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpResponse;
import org.smartqa.automationagent.remote.http.Route;
import org.smartqa.automationagent.remote.tracing.Tracer;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.smartqa.automationagent.grid.config.StandardGridRoles.DISTRIBUTOR_ROLE;
import static org.smartqa.automationagent.grid.config.StandardGridRoles.HTTPD_ROLE;
import static org.smartqa.automationagent.grid.config.StandardGridRoles.ROUTER_ROLE;
import static org.smartqa.automationagent.grid.config.StandardGridRoles.SESSION_MAP_ROLE;
import static org.smartqa.automationagent.net.Urls.fromUri;
import static org.smartqa.automationagent.remote.http.Route.get;

@AutoService(CliCommand.class)
public class RouterServer extends TemplateGridCommand {

  private static final Logger LOG = Logger.getLogger(RouterServer.class.getName());

  @Override
  public String getName() {
    return "router";
  }

  @Override
  public String getDescription() {
    return "Creates a router to front the automationagent grid.";
  }

  @Override
  public Set<Role> getConfigurableRoles() {
    return ImmutableSet.of(DISTRIBUTOR_ROLE, HTTPD_ROLE, ROUTER_ROLE, SESSION_MAP_ROLE);
  }

  @Override
  public Set<Object> getFlagObjects() {
    return Collections.emptySet();
  }

  @Override
  protected String getSystemPropertiesConfigPrefix() {
    return "router";
  }

  @Override
  protected Config getDefaultConfig() {
    return new MapConfig(ImmutableMap.of("server", ImmutableMap.of("port", 4444)));
  }

  @Override
  protected void execute(Config config) {
    LoggingOptions loggingOptions = new LoggingOptions(config);
    Tracer tracer = loggingOptions.getTracer();

    NetworkOptions networkOptions = new NetworkOptions(config);
    HttpClient.Factory clientFactory = networkOptions.getHttpClientFactory(tracer);

    SessionMapOptions sessionsOptions = new SessionMapOptions(config);
    SessionMap sessions = sessionsOptions.getSessionMap();

    BaseServerOptions serverOptions = new BaseServerOptions(config);

    DistributorOptions distributorOptions = new DistributorOptions(config);
    URL distributorUrl = fromUri(distributorOptions.getDistributorUri());
    Distributor distributor = new RemoteDistributor(
        tracer,
        clientFactory,
        distributorUrl
    );

    GraphqlHandler graphqlHandler = new GraphqlHandler(distributor, serverOptions.getExternalUri());

    Route handler = Route.combine(
      new Router(tracer, clientFactory, sessions, distributor).with(networkOptions.getSpecComplianceChecks()),
      Route.post("/graphql").to(() -> graphqlHandler),
      get("/readyz").to(() -> req -> new HttpResponse().setStatus(HTTP_NO_CONTENT)));

    Server<?> server = new NettyServer(serverOptions, handler, new ProxyCdpIntoGrid(clientFactory, sessions));
    server.start();

    BuildInfo info = new BuildInfo();
    LOG.info(String.format(
      "Started AutomationAgent router %s (revision %s): %s",
      info.getReleaseLabel(),
      info.getBuildRevision(),
      server.getUrl()));
  }
}
