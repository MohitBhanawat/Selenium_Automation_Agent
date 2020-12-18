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

public class TestClick extends InternalSelenseTestBase {
  @Test
  public void testClick() throws Exception {
    automationAgent.open("test_click_page1.html");
    verifyEquals(automationAgent.getText("link"), "Click here for next page");
    automationAgent.click("link");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    automationAgent.click("linkWithEnclosedImage");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.click("enclosedImage");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.click("extraEnclosedImage");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.click("linkToAnchorOnThisPage");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    try {
      automationAgent.waitForPageToLoad("500");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.setTimeout("30000");
    automationAgent.click("linkWithOnclickReturnsFalse");
    Thread.sleep(300);
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    automationAgent.setTimeout("5000");
    automationAgent.open("test_click_page1.html");
    automationAgent.doubleClick("doubleClickable");
    assertEquals(automationAgent.getAlert(), "double clicked!");
  }
}
