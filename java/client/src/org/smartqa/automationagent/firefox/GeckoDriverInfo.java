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

package org.smartqa.automationagent.firefox;

import com.google.auto.service.AutoService;

import static org.smartqa.automationagent.firefox.FirefoxDriver.Capability.MARIONETTE;
import static org.smartqa.automationagent.remote.CapabilityType.BROWSER_NAME;

import java.util.Optional;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.SessionNotCreatedException;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebDriverException;
import org.smartqa.automationagent.WebDriverInfo;
import org.smartqa.automationagent.remote.BrowserType;

@AutoService(WebDriverInfo.class)
public class GeckoDriverInfo implements WebDriverInfo {

  @Override
  public String getDisplayName() {
    return "Firefox";
  }

  @Override
  public Capabilities getCanonicalCapabilities() {
    return new ImmutableCapabilities(BROWSER_NAME, BrowserType.FIREFOX);
  }

  @Override
  public boolean isSupporting(Capabilities capabilities) {
    if (capabilities.is(MARIONETTE)) {
      return false;
    }

    if (BrowserType.FIREFOX.equals(capabilities.getBrowserName())) {
      return true;
    }

    return capabilities.asMap().keySet().stream()
        .map(key -> key.startsWith("moz:"))
        .reduce(Boolean::logicalOr)
        .orElse(false);
  }

  @Override
  public boolean isAvailable() {
    try {
      GeckoDriverService.createDefaultService();
      return true;
    } catch (IllegalStateException | WebDriverException e) {
      return false;
    }
  }

  @Override
  public int getMaximumSimultaneousSessions() {
    return Runtime.getRuntime().availableProcessors() + 1;
  }

  @Override
  public Optional<WebDriver> createDriver(Capabilities capabilities)
      throws SessionNotCreatedException {
    if (!isAvailable()) {
      return Optional.empty();
    }

    if (capabilities.is(MARIONETTE)) {
      return Optional.empty();
    }

    return Optional.of(new FirefoxDriver(capabilities));
  }
}
