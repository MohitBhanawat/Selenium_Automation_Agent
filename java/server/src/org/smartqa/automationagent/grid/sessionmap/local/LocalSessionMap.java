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

package org.smartqa.automationagent.grid.sessionmap.local;

import org.smartqa.automationagent.NoSuchSessionException;
import org.smartqa.automationagent.events.EventBus;
import org.smartqa.automationagent.grid.config.Config;
import org.smartqa.automationagent.grid.data.Session;
import org.smartqa.automationagent.grid.log.LoggingOptions;
import org.smartqa.automationagent.grid.server.EventBusOptions;
import org.smartqa.automationagent.grid.sessionmap.SessionMap;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.tracing.Tracer;

import static org.smartqa.automationagent.grid.data.SessionClosedEvent.SESSION_CLOSED;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalSessionMap extends SessionMap {

  private final EventBus bus;
  private final Map<SessionId, Session> knownSessions = new ConcurrentHashMap<>();
  private final ReadWriteLock lock = new ReentrantReadWriteLock(/* be fair */ true);

  public LocalSessionMap(Tracer tracer, EventBus bus) {
    super(tracer);

    this.bus = Require.nonNull("Event bus", bus);

    bus.addListener(SESSION_CLOSED, event -> {
      SessionId id = event.getData(SessionId.class);
      knownSessions.remove(id);
    });
  }

  public static SessionMap create(Config config) {
    Tracer tracer = new LoggingOptions(config).getTracer();
    EventBus bus = new EventBusOptions(config).getEventBus();

    return new LocalSessionMap(tracer, bus);
  }

  @Override
  public boolean isReady() {
    return bus.isReady();
  }

  @Override
  public boolean add(Session session) {
    Require.nonNull("Session", session);

    Lock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      knownSessions.put(session.getId(), session);
    } finally {
      writeLock.unlock();
    }

    return true;
  }

  @Override
  public Session get(SessionId id) {
    Require.nonNull("Session ID", id);

    Lock readLock = lock.readLock();
    readLock.lock();
    try {
      Session session = knownSessions.get(id);
      if (session == null) {
        throw new NoSuchSessionException("Unable to find session with ID: " + id);
      }

      return session;
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public void remove(SessionId id) {
    Require.nonNull("Session ID", id);

    Lock writeLock = lock.writeLock();
    writeLock.lock();
    try {
      knownSessions.remove(id);
    } finally {
      writeLock.unlock();
    }
  }
}
