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

package org.smartqa.automationagent.grid.sessionmap;

import org.junit.Before;
import org.junit.Test;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.NoSuchSessionException;
import org.smartqa.automationagent.events.EventBus;
import org.smartqa.automationagent.events.local.GuavaEventBus;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.grid.data.SessionClosedEvent;
import org.smartqa.automationagent.grid.sessionmap.SessionMap;
import org.smartqa.automationagent.grid.sessionmap.local.LocalSessionMap;
import org.smartqa.automationagent.grid.sessionmap.remote.RemoteSessionMap;
import org.smartqa.automationagent.grid.testing.PassthroughHttpClient;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.http.HttpClient;
import org.smartqa.automationagent.remote.tracing.DefaultTestTracer;
import org.smartqa.automationagent.remote.tracing.Tracer;
import org.smartqa.automationagent.support.ui.FluentWait;
import org.smartqa.automationagent.support.ui.Wait;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * We test the session map by ensuring that the HTTP protocol is properly adhered to. If this is
 * true, then any implementations are interoperable, and we can breathe a sigh of relief.
 */
public class SessionMapTest {

  private SessionId id;
  private Session expected;
  private SessionMap local;
  private HttpClient client;
  private SessionMap remote;
  private EventBus bus;

  @Before
  public void setUp() throws URISyntaxException {
    id = new SessionId(UUID.randomUUID());
    expected = new Session(
        id,
        new URI("http://localhost:1234"),
        new ImmutableCapabilities());

    Tracer tracer = DefaultTestTracer.createTracer();
    bus = new GuavaEventBus();

    local = new LocalSessionMap(tracer, bus);
    client = new PassthroughHttpClient(local);
    remote = new RemoteSessionMap(tracer, client);
  }

  @Test
  public void shouldBeAbleToAddASession() {
    assertTrue(remote.add(expected));

    assertEquals(expected, local.get(id));
  }

  @Test
  public void shouldBeAbleToRetrieveASessionUri() {
    local.add(expected);

    assertEquals(expected, remote.get(id));
  }

  @Test
  public void shouldThrowANoSuchSessionExceptionIfSessionCannotBeFound() {
    assertThatExceptionOfType(NoSuchSessionException.class).isThrownBy(() -> local.get(id));
    assertThatExceptionOfType(NoSuchSessionException.class).isThrownBy(() -> remote.get(id));
  }

  @Test
  public void shouldAllowSessionsToBeRemoved() {
    local.add(expected);

    assertEquals(expected, remote.get(id));

    remote.remove(id);

    assertThatExceptionOfType(NoSuchSessionException.class).isThrownBy(() -> local.get(id));
    assertThatExceptionOfType(NoSuchSessionException.class).isThrownBy(() -> remote.get(id));
  }

  /**
   * This is because multiple areas within the grid may all try and remove a session.
   */
  @Test
  public void removingASessionThatDoesNotExistIsNotAnError() {
    remote.remove(id);
  }

  @Test(expected = NoSuchSessionException.class)
  public void shouldThrowAnExceptionIfGettingASessionThatDoesNotExist() {
    remote.get(id);
  }

  @Test
  public void shouldAllowEntriesToBeRemovedByAMessage() {
    local.add(expected);

    bus.fire(new SessionClosedEvent(expected.getId()));

    Wait<SessionMap> wait = new FluentWait<>(local).withTimeout(ofSeconds(2));
    wait.until(sessions -> {
      try {
        sessions.get(expected.getId());
        return false;
      } catch (NoSuchSessionException e) {
        return true;
      }
    });
  }

}
