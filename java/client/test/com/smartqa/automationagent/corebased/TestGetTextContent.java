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

public class TestGetTextContent extends InternalSelenseTestBase {
  @Test
  public void testGetTextContent() {
    automationAgent.open("test_gettextcontent.html");
    verifyTrue(automationAgent.isTextPresent("Text1"));

    // TODO(simon): Fix this lameness.
    if (Boolean.getBoolean("automationagent.browser.automationagent") &&
        System.getProperty("automationagent.browser").startsWith("*ie")) {
      verifyFalse(automationAgent.isTextPresent("Text2"));
      verifyFalse(automationAgent.isTextPresent("Text3"));
      verifyFalse(automationAgent.isTextPresent("Text4"));
      verifyFalse(automationAgent.isTextPresent("Text5"));
      verifyFalse(automationAgent.isTextPresent("Text6"));
      verifyFalse(automationAgent.isTextPresent("Text7"));
      verifyFalse(automationAgent.isTextPresent("Text8"));
    }
  }
}
