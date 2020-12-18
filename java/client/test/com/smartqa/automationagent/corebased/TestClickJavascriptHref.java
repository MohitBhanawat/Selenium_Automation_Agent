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

public class TestClickJavascriptHref extends InternalSelenseTestBase {
  @Test
  public void testClickJavascriptHref() {
    automationAgent.open("test_click_javascript_page.html");
    automationAgent.click("link");
    verifyEquals(automationAgent.getAlert(), "link clicked: foo");
    automationAgent.click("linkWithMultipleJavascriptStatements");
    verifyEquals(automationAgent.getAlert(), "alert1");
    verifyEquals(automationAgent.getAlert(), "alert2");
    verifyEquals(automationAgent.getAlert(), "alert3");
    automationAgent.click("linkWithJavascriptVoidHref");
    verifyEquals(automationAgent.getAlert(), "onclick");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    automationAgent.click("linkWithOnclickReturnsFalse");
    verifyEquals(automationAgent.getTitle(), "Click Page 1");
    automationAgent.click("enclosedImage");
    verifyEquals(automationAgent.getAlert(), "enclosedImage clicked");
  }
}
