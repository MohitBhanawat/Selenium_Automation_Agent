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

public class TestCheckUncheck extends InternalSelenseTestBase {
  @Test
  public void testCheckUncheck() {
    automationAgent.open("test_check_uncheck.html");
    verifyTrue(automationAgent.isChecked("base-spud"));
    verifyFalse(automationAgent.isChecked("base-rice"));
    verifyTrue(automationAgent.isChecked("option-cheese"));
    verifyFalse(automationAgent.isChecked("option-onions"));
    automationAgent.check("base-rice");
    verifyFalse(automationAgent.isChecked("base-spud"));
    verifyTrue(automationAgent.isChecked("base-rice"));
    automationAgent.uncheck("option-cheese");
    verifyFalse(automationAgent.isChecked("option-cheese"));
    automationAgent.check("option-onions");
    verifyTrue(automationAgent.isChecked("option-onions"));
    verifyFalse(automationAgent.isChecked("option-chilli"));
    automationAgent.check("option chilli");
    verifyTrue(automationAgent.isChecked("option-chilli"));
    automationAgent.uncheck("option index=3");
    verifyFalse(automationAgent.isChecked("option-chilli"));
  }
}
