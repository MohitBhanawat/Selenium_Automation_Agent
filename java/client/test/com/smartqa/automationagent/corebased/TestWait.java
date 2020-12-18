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

public class TestWait extends InternalSelenseTestBase {
  @Test
  public void testWait() {
    // Link click
    automationAgent.open("test_reload_onchange_page.html");
    automationAgent.click("theLink");
    automationAgent.waitForPageToLoad("30000");
    // Page should reload
    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
    automationAgent.open("test_reload_onchange_page.html");
    automationAgent.select("theSelect", "Second Option");
    automationAgent.waitForPageToLoad("30000");
    // Page should reload
    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
    // Textbox with onblur
    automationAgent.open("test_reload_onchange_page.html");
    automationAgent.type("theTextbox", "new value");
    automationAgent.fireEvent("theTextbox", "blur");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
    // Submit button
    automationAgent.open("test_reload_onchange_page.html");
    automationAgent.click("theSubmit");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
    automationAgent.click("slowPage_reload");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
  }
}
