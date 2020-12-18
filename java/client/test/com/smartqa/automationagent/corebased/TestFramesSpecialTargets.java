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

@Ignore("Unable to find changeSpan")
public class TestFramesSpecialTargets extends InternalSelenseTestBase {
  @Test
  public void testFramesSpecialTargets() {
    automationAgent.openWindow("Frames.html", "SpecialTargets");
    automationAgent.waitForPopUp("SpecialTargets", "10000");
    automationAgent.selectWindow("SpecialTargets");
    automationAgent.selectFrame("bottomFrame");
    automationAgent.click("changeTop");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.click("changeSpan");
    automationAgent.open("Frames.html");
    automationAgent.selectFrame("bottomFrame");
    automationAgent.click("changeParent");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.click("changeSpan");
    automationAgent.open("Frames.html");
    automationAgent.selectFrame("bottomFrame");
    automationAgent.click("changeSelf");
    automationAgent.waitForPageToLoad("30000");
    automationAgent.click("changeSpan");
    automationAgent.close();
  }
}
