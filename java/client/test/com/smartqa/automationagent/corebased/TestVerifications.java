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

public class TestVerifications extends InternalSelenseTestBase {
  @Test
  public void testVerifications() {
    automationAgent.open("test_verifications.html?foo=bar");
    verifyTrue(automationAgent.getLocation().matches(
        "^.*/test_verifications\\.html[\\s\\S]*$"));
    verifyTrue(automationAgent.getLocation().matches(
        "^.*/test_verifications\\.html[\\s\\S]foo=bar$"));
    verifyEquals(automationAgent.getValue("theText"), "the text value");
    verifyNotEquals("not the text value", automationAgent.getValue("theText"));
    verifyEquals(automationAgent.getValue("theHidden"), "the hidden value");
    verifyEquals(automationAgent.getText("theSpan"), "this is the span");
    verifyNotEquals("blah blah", automationAgent.getText("theSpan"));
    verifyTrue(automationAgent.isTextPresent("this is the span"));
    verifyFalse(automationAgent.isTextPresent("this is not the span"));
    verifyTrue(automationAgent.isElementPresent("theSpan"));
    verifyTrue(automationAgent.isElementPresent("theText"));
    verifyFalse(automationAgent.isElementPresent("unknown"));
    verifyEquals(automationAgent.getTable("theTable.0.0"), "th1");
    verifyEquals(automationAgent.getTable("theTable.1.0"), "a");
    verifyEquals(automationAgent.getTable("theTable.2.1"), "d");
    verifyEquals(automationAgent.getTable("theTable.3.1"), "f2");
    verifyEquals(automationAgent.getSelectedIndex("theSelect"), "1");
    verifyEquals(automationAgent.getSelectedValue("theSelect"), "option2");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "second option");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "second option");
    verifyEquals(automationAgent.getSelectedId("theSelect"), "o2");
    verifyEquals(join(automationAgent.getSelectOptions("theSelect"), ','),
        "first option,second option,third,,option");
    verifyEquals(automationAgent.getAttribute("theText@class"), "foo");
    verifyNotEquals("fox", automationAgent.getAttribute("theText@class"));
    verifyEquals(automationAgent.getTitle(), "theTitle");
    verifyNotEquals("Blah Blah", automationAgent.getTitle());
  }
}
