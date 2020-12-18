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

package com.smartqa.automationagent;

import org.testng.ITestContext;
import org.testng.TestRunner;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.internal.IResultListener;

import java.io.File;
import java.lang.reflect.Method;

public class SeleneseTestNgHelper extends SeleneseTestBase {

  private static AutomationAgent staticAutomationAgent;

  @BeforeTest
  @Override
  @Parameters({"automationagent.url", "automationagent.browser"})
  public void setUp(@Optional String url, @Optional String browserString) throws Exception {
    if (browserString == null) {
      browserString = runtimeBrowserString();
    }
    super.setUp(url, browserString);
    staticAutomationAgent = automationAgent;
  }

  @BeforeClass
  @Parameters({"automationagent.restartSession"})
  public void getSelenium(@Optional("false") boolean restartSession) {
    automationAgent = staticAutomationAgent;
    if (restartSession) {
      automationAgent.stop();
      automationAgent.start();
    }
  }

  @BeforeMethod
  public void setTestContext(Method method) {
    automationAgent.setContext(
        method.getDeclaringClass().getSimpleName() + "." + method.getName());

  }

  @BeforeSuite
  @Parameters({"automationagent.host", "automationagent.port"})
  public void attachScreenshotListener(@Optional("localhost") String host,
      @Optional("4444") String port, ITestContext context) {
    if (!"localhost".equals(host)) {
      return;
    }
    AutomationAgent screenshotTaker = new DefaultAutomationAgent(host, Integer.parseInt(port),
                                                                 "", "");
    TestRunner tr = (TestRunner) context;
    File outputDirectory = new File(context.getOutputDirectory());
    tr.addListener((IResultListener) new ScreenshotListener(outputDirectory,
        screenshotTaker));
  }

  @AfterMethod
  @Override
  public void checkForVerificationErrors() {
    super.checkForVerificationErrors();
  }

  @AfterMethod(alwaysRun = true)
  public void selectDefaultWindow() {
    if (automationAgent != null) {
      automationAgent.selectWindow("null");
    }
  }

  @AfterTest(alwaysRun = true)
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }

  // @Override static method of super class (which assumes JUnit conventions)
  public static void assertEquals(Object actual, Object expected) {
    SeleneseTestBase.assertEquals(expected, actual);
  }

  // @Override static method of super class (which assumes JUnit conventions)
  public static void assertEquals(String actual, String expected) {
    SeleneseTestBase.assertEquals(expected, actual);
  }

  // @Override static method of super class (which assumes JUnit conventions)
  public static void assertEquals(String actual, String[] expected) {
    SeleneseTestBase.assertEquals(expected, actual);
  }

  // @Override static method of super class (which assumes JUnit conventions)
  public static void assertEquals(String[] actual, String[] expected) {
    SeleneseTestBase.assertEquals(expected, actual);
  }

  // @Override static method of super class (which assumes JUnit conventions)
  public static boolean seleniumEquals(Object actual, Object expected) {
    return SeleneseTestBase.seleniumEquals(expected, actual);
  }

  // @Override static method of super class (which assumes JUnit conventions)
  public static boolean seleniumEquals(String actual, String expected) {
    return SeleneseTestBase.seleniumEquals(expected, actual);
  }

  @Override
  public void verifyEquals(Object actual, Object expected) {
    super.verifyEquals(expected, actual);
  }

  @Override
  public void verifyEquals(String[] actual, String[] expected) {
    super.verifyEquals(expected, actual);
  }
}
