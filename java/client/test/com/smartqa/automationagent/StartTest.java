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

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.environment.GlobalTestEnvironment;
import org.smartqa.automationagent.environment.InProcessTestEnvironment;
import org.smartqa.automationagent.environment.TestEnvironment;
import org.smartqa.automationagent.firefox.FirefoxDriver;

import com.smartqa.automationagent.AutomationAgent;
import com.smartqa.automationagent.webdriven.WebDriverBackedAutomationAgent;

public class StartTest {

  private static TestEnvironment env;
  private static String root;

  @BeforeClass
  public static void startSelenium() {
    env = GlobalTestEnvironment.getOrCreate(InProcessTestEnvironment::new);
    root = env.getAppServer().whereIs("/");
  }

  @AfterClass
  public static void killSeleniumServer() {
    env.stop();
  }

  @Test
  public void shouldBeAbleToCreateAWebDriverBackedSeleniumInstance() {
    WebDriver driver = new FirefoxDriver();
    AutomationAgent automationAgent = new WebDriverBackedAutomationAgent(driver, root);

    try {
      automationAgent.open(env.getAppServer().whereIs("/"));

      String seleniumTitle = automationAgent.getTitle();
      String title = driver.getTitle();

      assertEquals(title, seleniumTitle);
    } finally {
      automationAgent.stop();
    }
  }
}
