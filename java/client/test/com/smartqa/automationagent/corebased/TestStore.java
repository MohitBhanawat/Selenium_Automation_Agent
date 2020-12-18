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

public class TestStore extends InternalSelenseTestBase {
  @Test
  public void testStore() {
    automationAgent.open("test_verifications.html");
    String storedHiddenValue = automationAgent.getValue("theHidden");
    String storedSpanText = automationAgent.getText("theSpan");
    String storedTextClass = automationAgent.getAttribute("theText@class");
    String storedTitle = automationAgent.getTitle();
    String textVariable = "PLAIN TEXT";
    String javascriptVariable = automationAgent
        .getEval("'Pi ~= ' +\n (Math.round(Math.PI * 100) / 100)");
    automationAgent.open("test_store_value.html");
    automationAgent.type("theText", storedHiddenValue);
    verifyEquals(automationAgent.getValue("theText"), "the hidden value");
    automationAgent.type("theText", storedSpanText);
    verifyEquals(automationAgent.getValue("theText"), "this is the span");
    automationAgent.type("theText", storedTextClass);
    verifyEquals(automationAgent.getValue("theText"), "foo");
    automationAgent.type("theText", textVariable);
    verifyEquals(automationAgent.getValue("theText"), "PLAIN TEXT");
    automationAgent.type("theText", javascriptVariable);
    verifyEquals(automationAgent.getValue("theText"), "Pi ~= 3.14");
    automationAgent.type("theText", storedTitle);
    verifyEquals(automationAgent.getValue("theText"), "theTitle");
    // Test multiple output variables in a single expression
    automationAgent.type("theText", "'" + storedHiddenValue + "'_'" + storedSpanText + "'");
    verifyEquals(automationAgent.getValue("theText"), "'the hidden value'_'this is the span'");
    // backward compatibility
    automationAgent.open("test_just_text.html");
    String storedBodyText = automationAgent.getBodyText();
    automationAgent.open("test_store_value.html");
    verifyEquals(automationAgent.getValue("theText"), "");
    automationAgent.type("theText", storedBodyText);
    verifyEquals(automationAgent.getValue("theText"), "This is the entire text of the page.");
    verifyEquals(automationAgent.getExpression(storedBodyText), "This is the entire text of the page.");
  }
}
