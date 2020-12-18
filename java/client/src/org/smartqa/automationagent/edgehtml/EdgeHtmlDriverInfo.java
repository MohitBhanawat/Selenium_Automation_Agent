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
package org.smartqa.automationagent.edgehtml;

import com.google.auto.service.AutoService;

import static org.smartqa.automationagent.remote.CapabilityType.BROWSER_NAME;

import java.util.Objects;
import java.util.Optional;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.SessionNotCreatedException;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebDriverException;
import org.smartqa.automationagent.WebDriverInfo;
import org.smartqa.automationagent.remote.BrowserType;

@AutoService(WebDriverInfo.class)
public class EdgeHtmlDriverInfo implements WebDriverInfo {

  @Override
  public String getDisplayName() {
    return "EdgeHTML";
  }

  @Override
  public Capabilities getCanonicalCapabilities() {
    return new ImmutableCapabilities(BROWSER_NAME, BrowserType.EDGE, EdgeHtmlOptions.USE_CHROMIUM, false);
  }

  @Override
  public boolean isSupporting(Capabilities capabilities) {
    return (BrowserType.EDGE.equals(capabilities.getBrowserName())
            || capabilities.getCapability("ms:edgeOptions") != null
            || capabilities.getCapability("edgeOptions") != null)
           &&
           Objects.equals(capabilities.getCapability(EdgeHtmlOptions.USE_CHROMIUM), false);
  }

  @Override
  public boolean isAvailable() {
    try {
      EdgeHtmlDriverService.createDefaultService();
      return true;
    } catch (IllegalStateException | WebDriverException e) {
      return false;
    }
  }

  @Override
  public int getMaximumSimultaneousSessions() {
    return 1;
  }

  @Override
  public Optional<WebDriver> createDriver(Capabilities capabilities)
      throws SessionNotCreatedException {
    if (!isAvailable()) {
      return Optional.empty();
    }

    return Optional.of(new EdgeHtmlDriver(new EdgeHtmlOptions().merge(capabilities)));
  }
}
