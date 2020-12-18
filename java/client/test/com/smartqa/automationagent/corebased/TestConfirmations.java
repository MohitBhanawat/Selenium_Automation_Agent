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

public class TestConfirmations extends InternalSelenseTestBase {
  @Test
  public void testConfirmations() throws Exception {
    automationAgent.open("test_confirm.html");
    automationAgent.chooseCancelOnNextConfirmation();
    automationAgent.click("confirmAndLeave");
    verifyTrue(automationAgent.isConfirmationPresent());
    for (int second = 0;; second++) {
      if (second >= 60) fail("timeout");
      try {
        if (automationAgent.isConfirmationPresent()) break;
      } catch (Exception e) {
      }
      Thread.sleep(1000);
    }

    assertTrue(automationAgent.isConfirmationPresent());
    verifyEquals(automationAgent.getConfirmation(), "You are about to go to a dummy page.");
    verifyEquals(automationAgent.getTitle(), "Test Confirm");
    automationAgent.click("confirmAndLeave");
    automationAgent.waitForPageToLoad("30000");
    verifyTrue(automationAgent.getConfirmation().matches("^[\\s\\S]*dummy page[\\s\\S]*$"));
    verifyEquals(automationAgent.getTitle(), "Dummy Page");
    automationAgent.open("test_confirm.html");
    verifyEquals(automationAgent.getTitle(), "Test Confirm");
    automationAgent.chooseCancelOnNextConfirmation();
    automationAgent.chooseOkOnNextConfirmation();
    automationAgent.click("confirmAndLeave");
    automationAgent.waitForPageToLoad("30000");
    verifyTrue(automationAgent.getConfirmation().matches("^[\\s\\S]*dummy page[\\s\\S]*$"));
    verifyEquals(automationAgent.getTitle(), "Dummy Page");
    automationAgent.open("test_confirm.html");
    try {
      assertEquals(automationAgent.getConfirmation(), "This should fail - there are no confirmations");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.click("confirmAndLeave");
    automationAgent.waitForPageToLoad("30000");
    try {
      assertEquals(automationAgent.getConfirmation(), "this should fail - wrong confirmation");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.open("test_confirm.html");
    automationAgent.click("confirmAndLeave");
    automationAgent.waitForPageToLoad("30000");
    try {
      automationAgent.open("test_confirm.html");
      fail("expected failure");
    } catch (Throwable e) {
    }
  }
}
