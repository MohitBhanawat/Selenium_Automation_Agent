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

public class TestClickJavascriptHrefChrome extends InternalSelenseTestBase {
  @Test
  public void testClickJavascriptHrefChrome() {
    automationAgent.open("test_click_javascript_chrome_page.html");
    automationAgent.click("id=a");
    verifyEquals(automationAgent.getAlert(), "a");
    automationAgent.click("id=b");
    verifyEquals(automationAgent.getAlert(), "b");
    automationAgent.click("id=c");
    verifyEquals(automationAgent.getAlert(), "c");
    automationAgent.click("id=d");
    verifyFalse(automationAgent.isElementPresent("id=d"));
    automationAgent.click("id=e");
    verifyEquals(automationAgent.getAlert(), "e");
    verifyFalse(automationAgent.isElementPresent("id=e"));
    automationAgent.click("id=f");
    automationAgent.waitForPopUp("f-window", "10000");
    automationAgent.selectWindow("name=f-window");
    verifyTrue(automationAgent.isElementPresent("id=visibleParagraph"));
    automationAgent.close();
    automationAgent.selectWindow("");

    // TODO(simon): re-enable this part of the test
    // automationagent.click("id=g");
    // verifyEquals(automationagent.getAlert(), "g");
    // automationagent.waitForPopUp("g-window", "10000");
    // automationagent.selectWindow("name=g-window");
    // verifyTrue(automationagent.isElementPresent("id=visibleParagraph"));
    // automationagent.close();
    // automationagent.selectWindow("");
    automationAgent.click("id=h");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getAlert(), "h");
    verifyTrue(automationAgent.isElementPresent("id=visibleParagraph"));
  }
}
