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

import com.smartqa.automationagent.AutomationAgent;

/**
 * This class throws an {@link java.lang.AssertionError} when the condition is not met - the same as
 * JUnit4 and TestNG do.
 */
public class JUnit4AndTestNgConditionRunner extends DefaultConditionRunner {

  public JUnit4AndTestNgConditionRunner(Monitor monitor, AutomationAgent automationAgent, int initialDelay,
                                        int interval, int timeout) {
    super(monitor, automationAgent, initialDelay, interval, timeout);
  }

  public JUnit4AndTestNgConditionRunner(Monitor monitor, AutomationAgent automationAgent, int interval,
                                        int timeout) {
    super(monitor, automationAgent, interval, timeout);
  }

  public JUnit4AndTestNgConditionRunner(AutomationAgent automationAgent, int initialDelay, int interval,
                                        int timeout) {
    super(automationAgent, initialDelay, interval, timeout);
  }

  public JUnit4AndTestNgConditionRunner(AutomationAgent automationAgent, int interval, int timeout) {
    super(automationAgent, interval, timeout);
  }

  public JUnit4AndTestNgConditionRunner(AutomationAgent automationAgent) {
    super(automationAgent);
  }

  @Override
  public void throwAssertionException(String message) {
    // same as Junit4's and TestNG's fail(..) methods.
    throw new AssertionError(message);
  }

  @Override
  public void throwAssertionException(String message, Throwable cause) {
    // same as Junit4's and TestNG's fail(..) methods.
    throw new AssertionError(message + "; cause: " + cause.getMessage());
  }

}
