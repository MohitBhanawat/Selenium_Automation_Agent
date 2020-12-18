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

import org.junit.Ignore;
import org.junit.Test;

import com.smartqa.automationagent.InternalSelenseTestBase;

public class TestRefresh extends InternalSelenseTestBase {
  @Ignore("The click on slowRefresh doesn't make the rc implementation wait")
  @Test
  public void testRefresh() {
    automationAgent.open("test_page.slow.html");
    System.out.println(automationAgent.getLocation());
    verifyTrue(automationAgent
                   .getLocation().matches("^[\\s\\S]*/common/rc/tests/html/test_page\\.slow\\.html$"));
    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
    automationAgent.click("changeSpan");
    assertTrue(automationAgent.isTextPresent("Changed the text"));
    automationAgent.refresh();
    automationAgent.waitForPageToLoad("30000");
    assertFalse(automationAgent.isTextPresent("Changed the text"));
    automationAgent.click("changeSpan");
    assertTrue(automationAgent.isTextPresent("Changed the text"));
    automationAgent.click("slowRefresh");
    automationAgent.waitForPageToLoad("30000");
    assertFalse(automationAgent.isTextPresent("Changed the text"));
    automationAgent.click("changeSpan");
    assertTrue(automationAgent.isTextPresent("Changed the text"));
    automationAgent.click("id=slowRefreshJavascriptHref");
    automationAgent.waitForPageToLoad("30000");
    assertFalse(automationAgent.isTextPresent("Changed the text"));
    automationAgent.click("anchor");
    automationAgent.click("changeSpan");
    assertTrue(automationAgent.isTextPresent("Changed the text"));
    automationAgent.refresh();
    automationAgent.waitForPageToLoad("30000");
    assertFalse(automationAgent.isTextPresent("Changed the text"));
  }
}
