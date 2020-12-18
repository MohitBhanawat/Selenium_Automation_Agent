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

public class TestOpen extends InternalSelenseTestBase {
  @Test
  public void testOpen() {
    automationAgent.open("test_open.html");
    verifyTrue(automationAgent.getLocation().matches("^.*/test_open\\.html$"));
  }

  @Test
  public void testIsTextPresentCanDoExactAndRegexChecks() {
    automationAgent.open("test_open.html");
    verifyTrue(automationAgent.isTextPresent("This is a test of the open command."));
    verifyTrue(automationAgent.isTextPresent("glob:This is a test of the open command."));
    verifyTrue(automationAgent.isTextPresent("exact:This is a test of"));
    verifyTrue(automationAgent.isTextPresent("regexp:This is a test of"));
    verifyTrue(automationAgent.isTextPresent("regexp:T*his is a test of"));
    verifyFalse(automationAgent.isTextPresent("exact:XXXXThis is a test of"));
    verifyFalse(automationAgent.isTextPresent("regexp:ThXXXXXXXXXis is a test of"));
  }

@Test
  public void testCanOpenSlowLoadingPage() {
    automationAgent.open("test_page.slow.html");
    verifyTrue(automationAgent.getLocation().matches("^.*/test_page\\.slow\\.html$"));
    verifyEquals(automationAgent.getTitle(), "Slow Loading Page");
    automationAgent.setTimeout("5000");
    automationAgent.open("test_open.html");
    automationAgent.open("test_open.html");
    automationAgent.open("test_open.html");
  }

}
