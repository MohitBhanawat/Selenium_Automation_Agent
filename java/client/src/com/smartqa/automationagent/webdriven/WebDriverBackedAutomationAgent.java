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

package com.smartqa.automationagent.webdriven;

import java.util.function.Supplier;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.HasCapabilities;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WrapsDriver;

import com.smartqa.automationagent.DefaultAutomationAgent;

public class WebDriverBackedAutomationAgent extends DefaultAutomationAgent
    implements HasCapabilities, WrapsDriver {
  public WebDriverBackedAutomationAgent(Supplier<WebDriver> maker, String baseUrl) {
    super(new WebDriverCommandProcessor(baseUrl, maker));
  }

  public WebDriverBackedAutomationAgent(WebDriver baseDriver, String baseUrl) {
    super(new WebDriverCommandProcessor(baseUrl, baseDriver));
  }

  @Override
  public WebDriver getWrappedDriver() {
    return ((WrapsDriver) commandProcessor).getWrappedDriver();
  }

  @Override
  public Capabilities getCapabilities() {
    WebDriver driver = getWrappedDriver();
    if (driver instanceof HasCapabilities) {
      return ((HasCapabilities) driver).getCapabilities();
    }

    return null;
  }
}
