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

public class TestFramesClick extends InternalSelenseTestBase {
  @Test
  public void testFramesClick() throws Exception {
    automationAgent.open("Frames.html");
    automationAgent.selectFrame("mainFrame");
    automationAgent.open("test_click_page1.html");
    // Click a regular link
    verifyEquals(automationAgent.getText("link"), "Click here for next page");
    automationAgent.click("link");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    // Click a link with an enclosed image
    automationAgent.click("linkWithEnclosedImage");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    // Click an image enclosed by a link
    automationAgent.click("enclosedImage");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    // Click a link with an href anchor target within this page
    automationAgent.click("linkToAnchorOnThisPage");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    // Click a link where onclick returns false
    automationAgent.click("linkWithOnclickReturnsFalse");
    // Need a pause to give the page a chance to reload (so this test can fail)
    Thread.sleep(300);
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    automationAgent.setTimeout("5000");
    automationAgent.open("test_click_page1.html");
    // TODO Click a link with a target attribute
  }
}
