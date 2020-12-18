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

package com.smartqa.automationagent.webdriven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.smartqa.automationagent.environment.GlobalTestEnvironment;
import org.smartqa.automationagent.testing.JUnit4TestBase;
import org.smartqa.automationagent.testing.NoDriverAfterTest;

import com.smartqa.automationagent.AutomationAgent;
import com.smartqa.automationagent.Wait;
import com.smartqa.automationagent.webdriven.WebDriverBackedAutomationAgent;

public class WebDriverBackedSeleniumLargeTest extends JUnit4TestBase {

  private AutomationAgent automationAgent;

  @Before
  public void setUpEnvironment() {
    String base = GlobalTestEnvironment.get().getAppServer().whereIs("");
    automationAgent = new WebDriverBackedAutomationAgent(driver, base);
  }

  @Test
  public void canUseTheOriginalWaitClassWithAWebDriverBackedInstance() {
    automationAgent.open(pages.dynamicPage);

    Wait waiter = new Wait() {

      @Override
      public boolean until() {
        return automationAgent.isElementPresent("id=box0");
      }
    };

    try {
      waiter.wait("Can't find the box", 2000, 200);
      fail("Should not have found the box");
    } catch (Wait.WaitTimedOutException e) {
      // this is expected
    }

    automationAgent.click("adder");

    waiter.wait("Can't find the box", 2000, 200);
  }

  @NoDriverAfterTest
  @Test
  public void testCallingStopThenSleepDoesNotCauseAnExceptionToBeThrown() {
    // Stop automationagent
    automationAgent.stop();

    try {
      // Now schedule a command that caues "interrupt" to be thrown internally.
      automationAgent.isElementPresent("name=q");
      fail("This test should have failed");
    } catch (NullPointerException expected) {
      // This is the exception thrown by automationagent 1. We should throw the same
      // one
    }

    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      fail("This was not expected");
    }
  }

  @Test
  public void testShouldBeAbleToInvokeSeleniumCoreElementLocatorsWithGetEval() {
    automationAgent.open(pages.simpleTestPage);
    String tagName = automationAgent.getEval(
        "var el = automationagent.browserbot.findElement('id=oneline');" +
        "el.tagName.toUpperCase();");
    assertEquals("P", tagName);
  }
}
