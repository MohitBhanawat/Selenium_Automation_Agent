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

@Ignore
public class TestFramesNested extends InternalSelenseTestBase {
  @Test
  public void testFramesNested() {
    automationAgent.open("NestedFrames.html");
    verifyEquals(automationAgent.getTitle(), "NestedFrames");
    verifyFalse(automationAgent.isTextPresent("This is a test"));
    automationAgent.selectFrame("mainFrame");
    verifyEquals(automationAgent.getTitle(), "NestedFrames2");
    automationAgent.selectFrame("mainFrame");
    verifyEquals(automationAgent.getTitle(), "AUT");
    automationAgent.selectFrame("mainFrame");
    verifyTrue(automationAgent.getLocation().matches("^[\\s\\S]*/common/legacy/test_open\\.html$"));
    verifyTrue(automationAgent.isTextPresent("This is a test"));
    automationAgent.selectFrame("relative=up");
    verifyEquals(automationAgent.getTitle(), "AUT");
    verifyFalse(automationAgent.isTextPresent("This is a test"));
    automationAgent.selectFrame("relative=top");
    verifyEquals(automationAgent.getTitle(), "NestedFrames");
    automationAgent.selectFrame("dom=window.frames[1]");
    verifyEquals(automationAgent.getTitle(), "NestedFrames2");
    automationAgent.selectFrame("relative=top");
    verifyEquals(automationAgent.getTitle(), "NestedFrames");
    automationAgent.selectFrame("index=1");
    verifyEquals(automationAgent.getTitle(), "NestedFrames2");
    automationAgent.selectFrame("relative=top");
    verifyEquals(automationAgent.getTitle(), "NestedFrames");
    automationAgent.selectFrame("foo");
    verifyEquals(automationAgent.getTitle(), "NestedFrames2");
    automationAgent.selectFrame("relative=top");
    verifyEquals(automationAgent.getTitle(), "NestedFrames");
    automationAgent.selectFrame("dom=window.frames[\"mainFrame\"].frames[\"mainFrame\"]");
    verifyEquals(automationAgent.getTitle(), "AUT");
  }
}
