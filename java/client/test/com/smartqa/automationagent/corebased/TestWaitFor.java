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

import org.junit.After;
import org.junit.Test;

import com.smartqa.automationagent.InternalSelenseTestBase;

import java.util.regex.Pattern;

public class TestWaitFor extends InternalSelenseTestBase {
  @Test
  public void testWaitFor() throws Exception {
    automationAgent.open("test_async_event.html");
    assertEquals(automationAgent.getValue("theField"), "oldValue");
    automationAgent.click("theButton");
    assertEquals(automationAgent.getValue("theField"), "oldValue");
    for (int second = 0;; second++) {
      if (second >= 60) fail("timeout");
      try {
        if (Pattern.compile("n[aeiou]wValue").matcher(automationAgent.getValue("theField")).find()) break;
      } catch (Exception e) {
      }
      Thread.sleep(1000);
    }

    verifyEquals(automationAgent.getValue("theField"), "newValue");
    assertEquals(automationAgent.getText("theSpan"), "Some text");
    automationAgent.click("theSpanButton");
    assertEquals(automationAgent.getText("theSpan"), "Some text");
    for (int second = 0;; second++) {
      if (second >= 60) fail("timeout");
      try {
        if (Pattern.compile("Some n[aeiou]w text").matcher(automationAgent.getText("theSpan")).find())
          break;
      } catch (Exception e) {
      }
      Thread.sleep(1000);
    }

    verifyEquals(automationAgent.getText("theSpan"), "Some new text");
    automationAgent.click("theAlertButton");
    for (int second = 0;; second++) {
      if (second >= 60) fail("timeout");
      try {
        if (Pattern.compile("An [aeiou]lert").matcher(automationAgent.getAlert()).find()) break;
      } catch (Exception e) {
      }
      Thread.sleep(1000);
    }

    automationAgent.open("test_reload_onchange_page.html");
    automationAgent.click("theLink");
    for (int second = 0;; second++) {
      if (second >= 60) fail("timeout");
      try {
        if ("Slow Loading Page".equals(automationAgent.getTitle())) break;
      } catch (Exception e) {
      }
      Thread.sleep(1000);
    }

    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
    automationAgent.setTimeout("500");
    try {
      for (int second = 0;; second++) {
        if (second >= 60) fail("timeout");
        try {
          if (automationAgent.isTextPresent("thisTextIsNotPresent")) break;
        } catch (Exception e) {
        }
        Thread.sleep(1000);
      }
      fail("expected failure");
    } catch (Throwable e) {
    }
  }

  @After
  public void resetTimeout() {
    automationAgent.setTimeout("30000");
  }
}
