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

import com.google.common.io.Files;
import com.smartqa.automationagent.InternalSelenseTestBase;

import org.junit.Test;
import org.smartqa.automationagent.WrapsDriver;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class TestType extends InternalSelenseTestBase {
  @Test
  public void testType() throws Exception {
    automationAgent.open("test_type_page1.html");
    verifyEquals(automationAgent.getValue("username"), "");
    automationAgent.shiftKeyDown();
    automationAgent.type("username", "x");
    verifyEquals(automationAgent.getValue("username"), "X");
    automationAgent.shiftKeyUp();
    automationAgent.type("username", "TestUserWithLongName");
    verifyEquals(automationAgent.getValue("username"), "TestUserWi");
    automationAgent.type("username", "TestUser");
    verifyEquals(automationAgent.getValue("username"), "TestUser");
    verifyEquals(automationAgent.getValue("password"), "");
    automationAgent.type("password", "testUserPasswordIsVeryLong");
    verifyEquals(automationAgent.getValue("password"), "testUserPasswordIsVe");
    automationAgent.type("password", "testUserPassword");
    verifyEquals(automationAgent.getValue("password"), "testUserPassword");
    if (isAbleToUpdateFileElements()) {
      File tempFile = File.createTempFile("example", "upload");
      tempFile.deleteOnExit();
      Files.asCharSink(tempFile, StandardCharsets.UTF_8).write("I like cheese");
      automationAgent.type("file", tempFile.getAbsolutePath());
      automationAgent.click("submitButton");
      automationAgent.waitForPageToLoad("30000");
      verifyTrue(automationAgent.isTextPresent("Welcome, TestUser!"));
    }
  }

  private boolean isAbleToUpdateFileElements() {
    String browser = System.getProperty("automationagent.browser", runtimeBrowserString());

    return automationAgent instanceof WrapsDriver ||
           "*firefox".equals(browser) || "*firefoxchrome".equals(browser);
  }
}
