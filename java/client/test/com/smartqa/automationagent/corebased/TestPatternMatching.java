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

import java.util.regex.Pattern;

public class TestPatternMatching extends InternalSelenseTestBase {
  @Test
  public void testPatternMatching() {
    automationAgent.open("test_verifications.html");
    verifyTrue(automationAgent.getValue("theText").matches("^[\\s\\S]*text[\\s\\S]*$"));
    verifyTrue(automationAgent.getValue("theHidden").matches("^[\\s\\S]* hidden value$"));
    verifyTrue(automationAgent.getText("theSpan").matches("^[\\s\\S]* span$"));
    verifyTrue(automationAgent.getSelectedLabel("theSelect").matches("^second [\\s\\S]*$"));
    verifyTrue(join(automationAgent.getSelectOptions("theSelect"), ',').matches(
        "^first[\\s\\S]*,second[\\s\\S]*,third[\\s\\S]*$"));
    verifyTrue(automationAgent.getAttribute("theText@class").matches("^[\\s\\S]oo$"));
    verifyTrue(automationAgent.getValue("theTextarea").matches("^Line 1[\\s\\S]*$"));
    verifyTrue(automationAgent.getValue("theText").matches("^[a-z ]+$"));
    verifyTrue(Pattern.compile("dd").matcher(automationAgent.getValue("theHidden")).find());
    verifyFalse(Pattern.compile("DD").matcher(automationAgent.getValue("theHidden")).find());
    verifyEquals(automationAgent.getValue("theHidden"), "regexpi:DD");
    verifyTrue(Pattern.compile("span$").matcher(automationAgent.getText("theSpan")).find());
    verifyTrue(Pattern.compile("second .*").matcher(automationAgent.getSelectedLabel("theSelect")).find());
    verifyTrue(Pattern.compile("^f").matcher(automationAgent.getAttribute("theText@class")).find());
    verifyTrue(automationAgent.getValue("theText").matches("^[a-z ]+$"));
    verifyTrue(Pattern.compile("dd").matcher(automationAgent.getValue("theHidden")).find());
    verifyTrue(Pattern.compile("span$").matcher(automationAgent.getText("theSpan")).find());
    verifyTrue(Pattern.compile("second .*").matcher(automationAgent.getSelectedLabel("theSelect")).find());
    verifyTrue(Pattern.compile("^f").matcher(automationAgent.getAttribute("theText@class")).find());
    verifyEquals(automationAgent.getValue("theText"), "the text value");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "second option");
    verifyTrue(Pattern.compile("^first.*?,second option,third*")
        .matcher(join(automationAgent.getSelectOptions("theSelect"), ',')).find());
  }
}
