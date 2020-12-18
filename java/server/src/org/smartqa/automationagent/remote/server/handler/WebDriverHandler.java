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

package org.smartqa.automationagent.remote.server.handler;

import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WrapsDriver;
import org.smartqa.automationagent.internal.Require;
import org.smartqa.automationagent.remote.SessionId;
import org.smartqa.automationagent.remote.server.KnownElements;
import org.smartqa.automationagent.remote.server.Session;
import org.smartqa.automationagent.remote.server.rest.RestishHandler;

import java.util.concurrent.Callable;

public abstract class WebDriverHandler<T> implements RestishHandler<T>, Callable<T> {

  private final Session session;

  protected WebDriverHandler(Session session) {
    this.session = session;
  }

  @Override
  public final T handle() throws Exception {
    return call();
  }

  public SessionId getSessionId() {
    return session.getSessionId();
  }

  public String getScreenshot() {
    Session session = getSession();
    return session != null ? session.getAndClearScreenshot() : null;
  }

  protected WebDriver getDriver() {
    Session session = getSession();
    return session.getDriver();
  }

  protected Session getSession() {
    return session;
  }

  protected KnownElements getKnownElements() {
    return getSession().getKnownElements();
  }

  protected BySelector newBySelector() {
    return new BySelector();
  }

  protected WebDriver getUnwrappedDriver() {
    WebDriver toReturn = getDriver();
    while (toReturn instanceof WrapsDriver) {
      toReturn = ((WrapsDriver) toReturn).getWrappedDriver();
    }
    return Require.state("Unwrapped driver", toReturn).nonNull();
  }
}
