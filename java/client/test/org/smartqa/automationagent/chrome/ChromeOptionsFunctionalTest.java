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

package org.smartqa.automationagent.chrome;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smartqa.automationagent.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.smartqa.automationagent.support.ui.ExpectedConditions.titleIs;

import org.junit.After;
import org.junit.Test;
import org.smartqa.automationagent.By;
import org.smartqa.automationagent.Keys;
import org.smartqa.automationagent.chrome.ChromeDriver;
import org.smartqa.automationagent.chrome.ChromeOptions;
import org.smartqa.automationagent.support.ui.WebDriverWait;
import org.smartqa.automationagent.testing.JUnit4TestBase;
import org.smartqa.automationagent.testing.NeedsLocalEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;

public class ChromeOptionsFunctionalTest extends JUnit4TestBase {

  private static final String EXT_PATH = "third_party/chrome_ext/backspace.crx";

  private ChromeDriver driver = null;

  @After
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @NeedsLocalEnvironment
  @Test
  public void canStartChromeWithCustomOptions() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("user-agent=foo;bar");
    driver = new ChromeDriver(options);

    driver.get(pages.clickJacker);
    Object userAgent = driver.executeScript("return window.navigator.userAgent");
    assertThat(userAgent).isEqualTo("foo;bar");
  }

  @NeedsLocalEnvironment
  @Test
  public void optionsStayEqualAfterSerialization() {
    ChromeOptions options1 = new ChromeOptions();
    ChromeOptions options2 = new ChromeOptions();
    assertThat(options2).isEqualTo(options1);
    options1.asMap();
    assertThat(options2).isEqualTo(options1);
  }

  @NeedsLocalEnvironment
  @Test
  public void canSetAcceptInsecureCerts() {
    ChromeOptions options = new ChromeOptions();
    options.setAcceptInsecureCerts(true);
    driver = new ChromeDriver(options);

    assertThat(driver.getCapabilities().getCapability(ACCEPT_INSECURE_CERTS)).isEqualTo(true);
  }

  @NeedsLocalEnvironment
  @Test
  public void canAddExtensionFromFile() {
    ChromeOptions options = new ChromeOptions();
    options.addExtensions(InProjectLocate.locate(EXT_PATH).toFile());
    driver = new ChromeDriver(options);

    driver.get(pages.clicksPage);

    driver.findElement(By.id("normal")).click();
    new WebDriverWait(driver, Duration.ofSeconds(10)).until(titleIs("XHTML Test Page"));

    driver.findElement(By.tagName("body")).sendKeys(Keys.BACK_SPACE);
    new WebDriverWait(driver, Duration.ofSeconds(10)).until(titleIs("clicks"));
  }

  @NeedsLocalEnvironment
  @Test
  public void canAddExtensionFromStringEncodedInBase64() throws IOException {
    ChromeOptions options = new ChromeOptions();
    options.addEncodedExtensions(Base64.getEncoder().encodeToString(
        Files.readAllBytes(InProjectLocate.locate(EXT_PATH))));
    driver = new ChromeDriver(options);

    driver.get(pages.clicksPage);

    driver.findElement(By.id("normal")).click();
    new WebDriverWait(driver, Duration.ofSeconds(10)).until(titleIs("XHTML Test Page"));

    driver.findElement(By.tagName("body")).sendKeys(Keys.BACK_SPACE);
    new WebDriverWait(driver, Duration.ofSeconds(10)).until(titleIs("clicks"));
  }

}
