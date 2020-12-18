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

package com.smartqa.automationagent.condition;

import static org.junit.Assert.fail;

import com.smartqa.automationagent.AutomationAgent;

/**
 * This class throws an {@link AssertionError} when the condition is not met.
 */
public class JUnitConditionRunner extends DefaultConditionRunner {

  public JUnitConditionRunner(Monitor monitor, AutomationAgent automationAgent, int initialDelay,
                              int interval, int timeout) {
    super(monitor, automationAgent, initialDelay, interval, timeout);
  }

  public JUnitConditionRunner(Monitor monitor, AutomationAgent automationAgent, int interval,
                              int timeout) {
    super(monitor, automationAgent, interval, timeout);
  }

  public JUnitConditionRunner(AutomationAgent automationAgent, int initialDelay, int interval, int timeout) {
    super(automationAgent, initialDelay, interval, timeout);
  }

  public JUnitConditionRunner(AutomationAgent automationAgent, int interval, int timeout) {
    super(automationAgent, interval, timeout);
  }

  public JUnitConditionRunner(AutomationAgent automationAgent) {
    super(automationAgent);
  }

  @Override
  public void throwAssertionException(String message) {
    fail(message);
  }

  @Override
  public void throwAssertionException(String message, Throwable cause) {
    String causeText = cause.getMessage();
    fail(message + (causeText == null ? "" : "; cause: " + causeText));
  }


}
