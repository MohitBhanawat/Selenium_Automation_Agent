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

import com.smartqa.automationagent.InternalSelenseTestBase;
import com.smartqa.automationagent.SeleniumException;

import org.junit.Test;

public class TestCursorPosition extends InternalSelenseTestBase {
  @Test
  public void testCursorPosition() {
    automationAgent.open("test_type_page1.html");
    try {
      assertEquals(automationAgent.getCursorPosition("username"), "8");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.windowFocus();
    verifyEquals(automationAgent.getValue("username"), "");
    automationAgent.type("username", "TestUser");
    automationAgent.setCursorPosition("username", "0");

    Number position = 0;
    try {
      position = automationAgent.getCursorPosition("username");
    } catch (SeleniumException e) {
      if (!isWindowInFocus(e)) {
        return;
      }
    }
    verifyEquals(position.toString(), "0");
    automationAgent.setCursorPosition("username", "-1");
    verifyEquals(automationAgent.getCursorPosition("username"), "8");
    automationAgent.refresh();
    automationAgent.waitForPageToLoad("30000");
    try {
      assertEquals(automationAgent.getCursorPosition("username"), "8");
      fail("expected failure");
    } catch (Throwable e) {
    }
  }

  private boolean isWindowInFocus(SeleniumException e) {
    if (e.getMessage().contains("There is no cursor on this page")) {
      System.out.println("Test failed because window does not have focus");
      return false;
    }
    return true;
  }
}
