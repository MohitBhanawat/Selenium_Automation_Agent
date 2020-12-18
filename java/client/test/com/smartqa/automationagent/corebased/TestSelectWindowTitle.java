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

public class TestSelectWindowTitle extends InternalSelenseTestBase {
  @Test
  public void testSelectWindowTitle() {
    automationAgent.open("test_select_window.html");
    automationAgent.click("popupPage");
    automationAgent.waitForPopUp("myPopupWindow", "5000");
    automationAgent.selectWindow("Select Window Popup");
    verifyTrue(automationAgent.getLocation().matches(
        "^.*/test_select_window_popup\\.html$"));
    verifyEquals(automationAgent.getTitle(), "Select Window Popup");
    verifyTrue(join(automationAgent.getAllWindowNames(), ',').matches("^[\\s\\S]*,[\\s\\S]*$"));
    verifyTrue(Pattern.compile("myPopupWindow").matcher(join(automationAgent.getAllWindowNames(), ','))
        .find());
    automationAgent.close();
    automationAgent.selectWindow("null");
    verifyTrue(automationAgent.getLocation().matches("^.*/test_select_window\\.html$"));
    automationAgent.click("popupPage");
    automationAgent.waitForPopUp("myPopupWindow", "5000");
    automationAgent.selectWindow("myPopupWindow");
    verifyTrue(automationAgent.getLocation().matches(
        "^.*/test_select_window_popup\\.html$"));
    automationAgent.close();
    automationAgent.selectWindow("null");
    automationAgent.click("popupAnonymous");
    automationAgent.waitForPopUp("anonymouspopup", "5000");
    automationAgent.selectWindow("anonymouspopup");
    verifyTrue(automationAgent.getLocation().matches(
        "^.*/test_select_window_popup\\.html$"));
    automationAgent.click("closePage");
    automationAgent.selectWindow("null");
    automationAgent.click("popupAnonymous");
    automationAgent.waitForPopUp("anonymouspopup", "5000");
    automationAgent.selectWindow("anonymouspopup");
    verifyTrue(automationAgent.getLocation().matches(
        "^.*/test_select_window_popup\\.html$"));
    automationAgent.click("closePage2");
  }
}
