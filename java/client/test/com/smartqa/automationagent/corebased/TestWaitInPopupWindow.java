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

public class TestWaitInPopupWindow extends InternalSelenseTestBase {
  @Test
  public void testWaitInPopupWindow() {
    automationAgent.open("test_select_window.html");
    automationAgent.click("popupPage");
    automationAgent.waitForPopUp("myPopupWindow", "5000");
    automationAgent.selectWindow("myPopupWindow");
    verifyEquals(automationAgent.getTitle(), "Select Window Popup");
    automationAgent.setTimeout("5000");
    automationAgent.click("link=Click to load new page");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Reload Page");
    automationAgent.setTimeout("30000");
    automationAgent.click("link=Click here");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
    automationAgent.close();
    automationAgent.selectWindow("null");
  }
}
