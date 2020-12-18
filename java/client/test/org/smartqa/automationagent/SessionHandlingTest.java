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

package org.smartqa.automationagent;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.smartqa.automationagent.testing.drivers.Browser.FIREFOX;
import static org.smartqa.automationagent.testing.drivers.Browser.MARIONETTE;
import static org.smartqa.automationagent.testing.drivers.Browser.SAFARI;

import org.junit.Test;
import org.smartqa.automationagent.NoSuchSessionException;
import org.smartqa.automationagent.testing.Ignore;
import org.smartqa.automationagent.testing.JUnit4TestBase;
import org.smartqa.automationagent.testing.NoDriverAfterTest;
import org.smartqa.automationagent.testing.NotYetImplemented;

public class SessionHandlingTest extends JUnit4TestBase {

  @NoDriverAfterTest
  @Test
  public void callingQuitMoreThanOnceOnASessionIsANoOp() {
    driver.quit();
    sleepTight(3000);
    driver.quit();
  }

  @NoDriverAfterTest
  @Test
  @Ignore(value = FIREFOX)
  @NotYetImplemented(value = MARIONETTE, reason = "https://github.com/mozilla/geckodriver/issues/689")
  @NotYetImplemented(SAFARI)
  public void callingQuitAfterClosingTheLastWindowIsANoOp() {
    driver.close();
    sleepTight(3000);
    driver.quit();
  }

  @NoDriverAfterTest
  @Test
  @Ignore(value = FIREFOX)
  public void callingAnyOperationAfterClosingTheLastWindowShouldThrowAnException() {
    driver.close();
    sleepTight(3000);
    assertThatExceptionOfType(NoSuchSessionException.class).isThrownBy(driver::getCurrentUrl);
  }

  @NoDriverAfterTest
  @Test
  public void callingAnyOperationAfterQuitShouldThrowAnException() {
    driver.quit();
    sleepTight(3000);
    assertThatExceptionOfType(NoSuchSessionException.class).isThrownBy(driver::getCurrentUrl);
  }

  @Test
  public void shouldContinueAfterSleep() {
    sleepTight(10000);
    driver.getWindowHandle(); // should not throw
  }

  private void sleepTight(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
