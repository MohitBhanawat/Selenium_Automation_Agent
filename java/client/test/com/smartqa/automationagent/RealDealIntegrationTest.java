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

package com.smartqa.automationagent;

import org.junit.Test;

import com.smartqa.automationagent.SeleniumException;
import com.smartqa.automationagent.SeleniumLogLevels;

public class RealDealIntegrationTest extends InternalSelenseTestBase {

  @Test
  public void testWithJavaScript() {
    automationAgent
        .setContext("A real test, using the real AutomationAgent on the browser side served by Jetty, driven from Java");
    automationAgent.setBrowserLogLevel(SeleniumLogLevels.DEBUG);
    automationAgent.open("test_click_page1.html");
    assertTrue("link 'link' doesn't contain expected text",
               automationAgent.getText("link").indexOf("Click here for next page") != -1);
    String[] links = automationAgent.getAllLinks();
    assertTrue(links.length > 3);
    assertEquals(links[3], "linkToAnchorOnThisPage");
    automationAgent.click("link");
    automationAgent.waitForPageToLoad("10000");
    assertTrue(automationAgent.getLocation().endsWith("test_click_page2.html"));
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("10000");
    assertTrue(automationAgent.getLocation().endsWith("test_click_page1.html"));
  }

  @Test
  public void testAgain() {
    testWithJavaScript();
  }

  @Test
  public void testFailure() {
    automationAgent
        .setContext("A real negative test, using the real AutomationAgent on the browser side served by Jetty, driven from Java");
    automationAgent.open("test_click_page1.html");
    String badElementName = "This element doesn't exist, so AutomationAgent should throw an exception";
    try {
      automationAgent.getText(badElementName);
      fail("No exception was thrown!");
    } catch (SeleniumException se) {
      assertTrue("Exception message isn't as expected: " + se.getMessage(), se.getMessage()
          .indexOf(badElementName + " not found") != -1);
    }

    assertFalse("Negative test", automationAgent
        .isTextPresent("Negative test: verify non-existent text"));
  }

}
