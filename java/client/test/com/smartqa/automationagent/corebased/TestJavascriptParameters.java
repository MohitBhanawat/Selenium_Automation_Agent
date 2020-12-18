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

import java.util.regex.Pattern;

@Ignore("automationagent.getValue is not a function. Needs to be added to automationagent JS emulation")
public class TestJavascriptParameters extends InternalSelenseTestBase {
  @Test
  public void testJavascriptParameters() {
    automationAgent.open("test_store_value.html");
    automationAgent.type("theText", automationAgent.getEval("[1,2,3,4,5].join(':')"));
    verifyEquals(automationAgent.getValue("theText"), "1:2:3:4:5");
    automationAgent.type(automationAgent.getEval("'the' + 'Text'"), automationAgent.getEval("10 * 5"));
    verifyEquals(automationAgent.getValue("theText"), "50");
    verifyEquals(automationAgent.getValue("theText"), automationAgent
        .getEval("10 + 10 + 10 + 10 + 10"));
    // Check a complex expression
    automationAgent.type("theText", automationAgent
        .getEval("\n function square(n) {\n return n * n;\n };\n '25 * 25 = ' + square(25);\n "));
    verifyTrue(automationAgent.getValue("theText").matches("^25 [\\s\\S]* 25 = 625$"));
    // Demonstrate interation between variable substitution and javascript
    String var1 = "the value";
    automationAgent.type("theText", automationAgent.getEval("'${var1}'.toUpperCase()"));
    verifyEquals(automationAgent.getValue("theText"), "${VAR1}");
    automationAgent.type("theText", automationAgent.getEval("'" + var1 + "'.toUpperCase()"));
    verifyEquals(automationAgent.getValue("theText"), "THE VALUE");
    verifyEquals(
        automationAgent.getExpression(automationAgent.getEval("'" + var1 + "'.toUpperCase()")),
        "THE VALUE");
    verifyTrue(Pattern.compile("TH[Ee] VALUE")
        .matcher(automationAgent
                     .getExpression(automationAgent.getEval("automationagent.getValue('theText')"))).find());
  }
}
