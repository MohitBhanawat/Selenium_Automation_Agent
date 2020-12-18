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

public class TestAddLocationStrategy extends InternalSelenseTestBase {
  @Test
  public void testAddLocationStrategy() {
    automationAgent.open("test_click_page1.html");
    automationAgent.addLocationStrategy("foo", "return inDocument.getElementById(locator);");
    assertTrue(automationAgent.isElementPresent("foo=link"));
    automationAgent.refresh();
    automationAgent.waitForPageToLoad("30000");
    assertTrue(automationAgent.isElementPresent("foo=link"));
    try {
      automationAgent.addLocationStrategy("bar", "[[[;");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.addLocationStrategy("bar", "thisVariableDoesNotExist;");
    try {
      assertTrue(automationAgent.isElementPresent("bar=link"));
      fail("expected failure");
    } catch (Throwable e) {
    }
  }
}
