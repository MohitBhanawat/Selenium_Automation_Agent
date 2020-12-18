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

package org.smartqa.automationagent.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smartqa.automationagent.testing.drivers.Browser.EDGE;
import static org.smartqa.automationagent.testing.drivers.Browser.FIREFOX;
import static org.smartqa.automationagent.testing.drivers.Browser.HTMLUNIT;
import static org.smartqa.automationagent.testing.drivers.Browser.IE;
import static org.smartqa.automationagent.testing.drivers.Browser.MARIONETTE;
import static org.smartqa.automationagent.testing.drivers.Browser.SAFARI;

import org.junit.After;
import org.junit.Test;
import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.logging.LogEntries;
import org.smartqa.automationagent.logging.LogType;
import org.smartqa.automationagent.logging.LoggingPreferences;
import org.smartqa.automationagent.remote.CapabilityType;
import org.smartqa.automationagent.testing.Ignore;
import org.smartqa.automationagent.testing.JUnit4TestBase;
import org.smartqa.automationagent.testing.drivers.WebDriverBuilder;

import java.util.Set;
import java.util.logging.Level;

@Ignore(HTMLUNIT)
@Ignore(IE)
@Ignore(EDGE)
@Ignore(MARIONETTE)
@Ignore(SAFARI)
@Ignore(FIREFOX)
public class PerformanceLogTypeTest extends JUnit4TestBase {

  private WebDriver localDriver;

  @After
  public void quitDriver() {
    if (localDriver != null) {
      localDriver.quit();
      localDriver = null;
    }
  }

  @Test
  public void performanceLogShouldBeDisabledByDefault() {
    Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
    assertThat(logTypes.contains(LogType.PERFORMANCE))
        .describedAs("Performance log should not be enabled by default").isFalse();
  }

  void createLocalDriverWithPerformanceLogType() {
    LoggingPreferences logPrefs = new LoggingPreferences();
    logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
    Capabilities caps = new ImmutableCapabilities(CapabilityType.LOGGING_PREFS, logPrefs);
    localDriver = new WebDriverBuilder().get(caps);
  }

  @Test
  public void shouldBeAbleToEnablePerformanceLog() {
    createLocalDriverWithPerformanceLogType();
    Set<String> logTypes = localDriver.manage().logs().getAvailableLogTypes();
    assertThat(logTypes.contains(LogType.PERFORMANCE))
        .describedAs("Profiler log should be enabled").isTrue();
  }

  @Test
  public void pageLoadShouldProducePerformanceLogEntries() {
    createLocalDriverWithPerformanceLogType();
    localDriver.get(pages.simpleTestPage);
    LogEntries entries = localDriver.manage().logs().get(LogType.PERFORMANCE);
    assertThat(entries).isNotEmpty();
  }
}
