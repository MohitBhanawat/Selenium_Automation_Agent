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
import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.HasCapabilities;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WrapsDriver;
import org.smartqa.automationagent.remote.CapabilityType;

import com.smartqa.automationagent.InternalSelenseTestBase;

@Ignore()
public class TestClickAt extends InternalSelenseTestBase {
  @Test(timeout = 60000)
  public void testClickAt() throws Exception {
    automationAgent.open("test_click_page1.html");
    verifyEquals(automationAgent.getText("link"), "Click here for next page");
    automationAgent.clickAt("link", "0,0");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.clickAt("link", "10,5");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    automationAgent.clickAt("linkWithEnclosedImage", "0,0");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.clickAt("linkWithEnclosedImage", "600,5");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.clickAt("enclosedImage", "0,0");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    // Pixel count is 0-based, not 1-based. In addition, current implementation
    // of Utils.getLocation adds 3 pixels to the x offset. Until that's fixed,
    // do not attempt to click at the edge of the image.
    automationAgent.clickAt("enclosedImage", "640,40");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.clickAt("extraEnclosedImage", "0,0");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.clickAt("extraEnclosedImage", "643,40");
    automationAgent.waitForPageToLoad("30000");
    verifyEquals(automationAgent.getTitle(), "Click Page Target");
    automationAgent.click("previousPage");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.clickAt("linkToAnchorOnThisPage", "0,0");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    automationAgent.clickAt("linkToAnchorOnThisPage", "10,5");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    try {
      automationAgent.waitForPageToLoad("500");
      fail("expected failure");
    } catch (Throwable e) {
    }
    automationAgent.setTimeout("30000");
    automationAgent.clickAt("linkWithOnclickReturnsFalse", "0,0");
    Thread.sleep(300);
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    automationAgent.clickAt("linkWithOnclickReturnsFalse", "10,5");
    Thread.sleep(300);
    verifyEquals(automationAgent.getTitle(), "Click Page 1");

    if (isUsingNativeEvents()) {
      // Click outside the element and make sure we don't pass to the next page.
      automationAgent.clickAt("linkWithEnclosedImage", "650,0");
      automationAgent.waitForPageToLoad("30000");
      verifyEquals(automationAgent.getTitle(), "Click Page 1");
      automationAgent.clickAt("linkWithEnclosedImage", "660,20");
      automationAgent.waitForPageToLoad("30000");
      verifyEquals(automationAgent.getTitle(), "Click Page 1");
      automationAgent.setTimeout("5000");
    }
  }

  private boolean isUsingNativeEvents() {
    if (!(automationAgent instanceof WrapsDriver)) {
      return false;
    }

    WebDriver driver = ((WrapsDriver) automationAgent).getWrappedDriver();
    if (!(driver instanceof HasCapabilities)) {
      return false;
    }

    Capabilities capabilities = ((HasCapabilities) driver).getCapabilities();
    return capabilities.is(CapabilityType.HAS_NATIVE_EVENTS);
  }
}
