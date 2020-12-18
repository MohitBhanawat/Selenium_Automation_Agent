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

public class TestOpenInTargetFrame extends InternalSelenseTestBase {
  @Test
  public void testOpenInTargetFrame() throws Exception {
    automationAgent.open("test_open_in_target_frame.html");
    automationAgent.selectFrame("rightFrame");
    automationAgent.click("link=Show new frame in leftFrame");
    // we are forced to do a pause instead of clickandwait here,
    // for currently we can not detect target frame loading in ie yet
    Thread.sleep(1500);
    verifyTrue(automationAgent.isTextPresent("Show new frame in leftFrame"));
    automationAgent.selectFrame("relative=top");
    automationAgent.selectFrame("leftFrame");
    verifyTrue(automationAgent.isTextPresent("content loaded"));
    verifyFalse(automationAgent.isTextPresent("This is frame LEFT"));
  }
}
