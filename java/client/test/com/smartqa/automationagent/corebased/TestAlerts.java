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

package com.smartqa.automationagent.corebased;

import org.junit.Test;

import com.smartqa.automationagent.InternalSelenseTestBase;

import java.util.regex.Pattern;

public class TestAlerts extends InternalSelenseTestBase {
  @Test
  public void testAlerts() throws Exception {
    automationAgent.open("test_verify_alert.html");
    verifyFalse(automationAgent.isAlertPresent());
    assertFalse(automationAgent.isAlertPresent());
    automationAgent.click("oneAlert");
    verifyTrue(automationAgent.isAlertPresent());
    for (int second = 0;; second++) {
      if (second >= 60) fail("timeout");
      try {
        if (automationAgent.isAlertPresent()) break;
      } catch (Exception e) {
      }
      Thread.sleep(1000);
    }

    assertTrue(automationAgent.isAlertPresent());
    verifyEquals(automationAgent.getAlert(), "Store Below 494 degrees K!");
    automationAgent.click("multipleLineAlert");
    verifyEquals(automationAgent.getAlert(), "This alert spans multiple lines");
    automationAgent.click("oneAlert");
    String myVar = automationAgent.getAlert();
    verifyEquals(automationAgent.getExpression(myVar), "Store Below 494 degrees K!");
    automationAgent.click("twoAlerts");
    verifyTrue(automationAgent.getAlert().matches("^[\\s\\S]* 220 degrees C!$"));
    verifyTrue(Pattern.compile("^Store Below 429 degrees F!").matcher(automationAgent.getAlert()).find());
    automationAgent.click("alertAndLeave");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getAlert(), "I'm Melting! I'm Melting!");
    automationAgent.open("test_verify_alert.html");
    try {
      assertEquals(automationAgent.getAlert(), "noAlert");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.click("oneAlert");
    try {
      assertEquals(automationAgent.getAlert(), "wrongAlert");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.click("twoAlerts");
    try {
      assertEquals(automationAgent.getAlert(), "Store Below 429 degrees F!");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(automationAgent.getAlert(), "Store Below 220 degrees C!");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.click("oneAlert");
    try {
      automationAgent.open("../tests/html/test_verify_alert.html");
      fail("expected failure");
    } catch (Throwable e) {
    }
  }
}
