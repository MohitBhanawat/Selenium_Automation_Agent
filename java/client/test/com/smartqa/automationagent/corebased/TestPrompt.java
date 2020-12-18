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

@Ignore("getPrompt not implemented")
public class TestPrompt extends InternalSelenseTestBase {
  @Test
  public void testPrompt() throws Exception {
    automationAgent.open("test_prompt.html");
    verifyFalse(automationAgent.isPromptPresent());
    assertFalse(automationAgent.isPromptPresent());
    automationAgent.answerOnNextPrompt("no");
    automationAgent.click("promptAndLeave");
    verifyTrue(automationAgent.isPromptPresent());
    for (int second = 0;; second++) {
      if (second >= 60) fail("timeout");
      try {
        if (automationAgent.isPromptPresent()) break;
      } catch (Exception e) {
      }
      Thread.sleep(1000);
    }

    assertTrue(automationAgent.isPromptPresent());
    verifyEquals(automationAgent.getPrompt(), "Type 'yes' and click OK");
    verifyEquals(automationAgent.getTitle(), "Test Prompt");
    automationAgent.answerOnNextPrompt("yes");
    automationAgent.click("promptAndLeave");
    automationAgent.waitForPageToLoad("30000");
    verifyTrue(automationAgent.getPrompt().matches("^[\\s\\S]*'yes'[\\s\\S]*$"));
    verifyEquals(automationAgent.getTitle(), "Dummy Page");
  }
}
