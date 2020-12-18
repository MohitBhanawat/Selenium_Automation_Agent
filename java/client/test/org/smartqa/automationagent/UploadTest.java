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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assume.assumeFalse;
import static org.smartqa.automationagent.Platform.ANDROID;
import static org.smartqa.automationagent.WaitingConditions.elementTextToEqual;
import static org.smartqa.automationagent.support.ui.ExpectedConditions.not;
import static org.smartqa.automationagent.support.ui.ExpectedConditions.visibilityOf;
import static org.smartqa.automationagent.testing.drivers.Browser.CHROME;
import static org.smartqa.automationagent.testing.drivers.Browser.CHROMIUMEDGE;
import static org.smartqa.automationagent.testing.drivers.Browser.EDGE;
import static org.smartqa.automationagent.testing.drivers.Browser.FIREFOX;
import static org.smartqa.automationagent.testing.drivers.Browser.HTMLUNIT;
import static org.smartqa.automationagent.testing.drivers.Browser.SAFARI;

import org.junit.Before;
import org.junit.Test;
import org.smartqa.automationagent.By;
import org.smartqa.automationagent.ElementNotInteractableException;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.InvalidArgumentException;
import org.smartqa.automationagent.WebElement;
import org.smartqa.automationagent.remote.CapabilityType;
import org.smartqa.automationagent.testing.Ignore;
import org.smartqa.automationagent.testing.JUnit4TestBase;
import org.smartqa.automationagent.testing.NeedsFreshDriver;
import org.smartqa.automationagent.testing.NoDriverAfterTest;
import org.smartqa.automationagent.testing.NoDriverBeforeTest;
import org.smartqa.automationagent.testing.NotYetImplemented;
import org.smartqa.automationagent.testing.SwitchToTopAfterTest;
import org.smartqa.automationagent.testing.TestUtilities;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Demonstrates how to use WebDriver with a file input element.
 */
public class UploadTest extends JUnit4TestBase {

  private static final String LOREM_IPSUM_TEXT = "lorem ipsum dolor sit amet";
  private static final String FILE_HTML = "<div>" + LOREM_IPSUM_TEXT + "</div>";

  private File testFile;

  @Before
  public void setUp() {
    testFile = createTmpFile(FILE_HTML);
  }

  @SwitchToTopAfterTest
  @Test
  @NotYetImplemented(value = SAFARI, reason = "Returns wrong text of the frame body")
  public void testFileUploading() {
    assumeFalse(
        "This test as written assumes a file on local disk is accessible to the browser. "
        + "That is not true for browsers on mobile platforms.",
        TestUtilities.getEffectivePlatform(driver).is(ANDROID));
    driver.get(pages.uploadPage);
    driver.findElement(By.id("upload")).sendKeys(testFile.getAbsolutePath());
    driver.findElement(By.id("go")).click();

    // Uploading files across a network may take a while, even if they're really small
    WebElement label = driver.findElement(By.id("upload_label"));
    wait.until(not(visibilityOf(label)));

    driver.switchTo().frame("upload_target");

    WebElement body = driver.findElement(By.xpath("//body"));
    wait.until(elementTextToEqual(body, LOREM_IPSUM_TEXT));
  }

  @SwitchToTopAfterTest
  @Test
  @NotYetImplemented(value = SAFARI, reason = "Returns wrong text of the frame body")
  public void testMultipleFileUploading() {
    List<String> multiContent = Arrays.asList(LOREM_IPSUM_TEXT, LOREM_IPSUM_TEXT, LOREM_IPSUM_TEXT);
    String fileNames = multiContent.stream()
        .map(text -> "<div>" + text + "</div>")
        .map(this::createTmpFile)
        .map(File::getAbsolutePath)
        .collect(Collectors.joining("\n"));
    assumeFalse(
        "This test as written assumes a file on local disk is accessible to the browser. "
        + "That is not true for browsers on mobile platforms.",
        TestUtilities.getEffectivePlatform(driver).is(ANDROID));
    driver.get(pages.uploadPage);
    driver.findElement(By.id("upload")).sendKeys(fileNames);
    driver.findElement(By.id("go")).click();

    // Uploading files across a network may take a while, even if they're really small
    WebElement label = driver.findElement(By.id("upload_label"));
    wait.until(not(visibilityOf(label)));

    driver.switchTo().frame("upload_target");

    WebElement body = driver.findElement(By.xpath("//body"));
    wait.until(elementTextToEqual(body, String.join("\n", multiContent)));
  }

  @Test
  public void testCleanFileInput() {
    driver.get(pages.uploadPage);
    WebElement element = driver.findElement(By.id("upload"));
    element.sendKeys(testFile.getAbsolutePath());
    element.clear();
    assertThat(element.getAttribute("value")).isEqualTo("");
  }

  @Test
  @Ignore(CHROME)
  @Ignore(CHROMIUMEDGE)
  @Ignore(HTMLUNIT)
  public void testClickFileInput() {
    driver.get(pages.uploadPage);
    WebElement element = driver.findElement(By.id("upload"));
    assertThatExceptionOfType(InvalidArgumentException.class).isThrownBy(element::click);
  }

  @Test
  @Ignore(value = SAFARI, reason = "Hangs forever in sendKeys")
  public void testUploadingWithHiddenFileInput() {
    driver.get(appServer.whereIs("upload_hidden.html"));
    driver.findElement(By.id("upload")).sendKeys(testFile.getAbsolutePath());
    driver.findElement(By.id("go")).click();

    // Uploading files across a network may take a while, even if they're really small
    WebElement label = driver.findElement(By.id("upload_label"));
    wait.until(not(visibilityOf(label)));

    driver.switchTo().frame("upload_target");

    WebElement body = driver.findElement(By.xpath("//body"));
    wait.until(elementTextToEqual(body, LOREM_IPSUM_TEXT));
  }

  @Test
  @Ignore(value = SAFARI, reason = "Hangs forever in sendKeys")
  @Ignore(HTMLUNIT)
  @NotYetImplemented(EDGE)
  @NeedsFreshDriver
  public void testUploadingWithInvisibleFileInput() {
    driver.get(appServer.whereIs("upload_invisible.html"));
    driver.findElement(By.id("upload")).sendKeys(testFile.getAbsolutePath());
    driver.findElement(By.id("go")).click();

    // Uploading files across a network may take a while, even if they're really small
    WebElement label = driver.findElement(By.id("upload_label"));
    wait.until(not(visibilityOf(label)));

    driver.switchTo().frame("upload_target");

    WebElement body = driver.findElement(By.xpath("//body"));
    wait.until(elementTextToEqual(body, LOREM_IPSUM_TEXT));
  }

  @Test
  @Ignore(FIREFOX)
  @Ignore(HTMLUNIT)
  @NotYetImplemented(EDGE)
  @NoDriverBeforeTest
  @NoDriverAfterTest
  public void testUploadingWithInvisibleFileInputWhenStrictFileInteractabilityIsOn() {
    createNewDriver(new ImmutableCapabilities(CapabilityType.STRICT_FILE_INTERACTABILITY, true));

    driver.get(appServer.whereIs("upload_invisible.html"));
    WebElement input = driver.findElement(By.id("upload"));
    System.out.println(input.isDisplayed());

    assertThatExceptionOfType(ElementNotInteractableException.class).isThrownBy(
        () -> input.sendKeys(testFile.getAbsolutePath()));
  }

  private File createTmpFile(String content) {
    try {
      File f = File.createTempFile("webdriver", "tmp");
      f.deleteOnExit();
      Files.write(f.toPath(), content.getBytes(StandardCharsets.UTF_8));
      return f;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
