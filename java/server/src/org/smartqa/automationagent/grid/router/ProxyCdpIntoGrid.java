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

package org.smartqa.automationagent.grid.router;

import org.smartqa.automationagent.NoSuchSessionException;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.grid.sessionmap.SessionMap;
import org.smartqa.automationagent.remote.HttpSessionId;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.BinaryMessage;
import org.smartqa.automationagent.remote.http.ClientConfig;
import org.smartqa.automationagent.remote.http.CloseMessage;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.http.HttpRequest;
import org.smartqa.automationagent.remote.http.Message;
import org.smartqa.automationagent.remote.http.TextMessage;
import org.smartqa.automationagent.remote.http.WebSocket;

import static org.smartqa.automationagent.remote.http.HttpMethod.GET;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyCdpIntoGrid implements BiFunction<String, Consumer<Message>, Optional<Consumer<Message>>> {

  private static final Logger LOG = Logger.getLogger(ProxyCdpIntoGrid.class.getName());
  private final HttpClient.Factory clientFactory;
  private final SessionMap sessions;

  public ProxyCdpIntoGrid(HttpClient.Factory clientFactory, SessionMap sessions) {
    this.clientFactory = Objects.requireNonNull(clientFactory);
    this.sessions = Objects.requireNonNull(sessions);
  }

  @Override
  public Optional<Consumer<Message>> apply(String uri, Consumer<Message> downstream) {
    Objects.requireNonNull(uri);
    Objects.requireNonNull(downstream);

    Optional<SessionId> sessionId = HttpSessionId.getSessionId(uri).map(SessionId::new);
    if (!sessionId.isPresent()) {
      return Optional.empty();
    }

    try {
      Session session = sessions.get(sessionId.get());

      HttpClient client = clientFactory.createClient(ClientConfig.defaultConfig().baseUri(session.getUri()));
      WebSocket upstream = client.openSocket(new HttpRequest(GET, uri), new ForwardingListener(downstream));

      return Optional.of(upstream::send);

    } catch (NoSuchSessionException e) {
      LOG.info("Attempt to connect to non-existant session: " + uri);
      return Optional.empty();
    }
  }

  private static class ForwardingListener implements WebSocket.Listener {
    private final Consumer<Message> downstream;

    public ForwardingListener(Consumer<Message> downstream) {
      this.downstream = Objects.requireNonNull(downstream);
    }

    @Override
    public void onBinary(byte[] data) {
      downstream.accept(new BinaryMessage(data));
    }

    @Override
    public void onClose(int code, String reason) {
      downstream.accept(new CloseMessage(code, reason));
    }

    @Override
    public void onText(CharSequence data) {
      downstream.accept(new TextMessage(data));
    }

    @Override
    public void onError(Throwable cause) {
      LOG.log(Level.WARNING, "Error proxying CDP command", cause);
    }
  }
}
