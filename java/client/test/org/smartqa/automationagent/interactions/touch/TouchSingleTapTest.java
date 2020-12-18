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

package org.smartqa.automationagent.interactions.touch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smartqa.automationagent.support.ui.ExpectedConditions.titleIs;

import org.junit.Test;
import org.smartqa.automationagent.By;
import org.smartqa.automationagent.JavascriptExecutor;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebElement;
import org.smartqa.automationagent.interactions.Action;
import org.smartqa.automationagent.interactions.touch.TouchActions;

/**
 * Tests single tap actions on touch enabled devices.
 */
public class TouchSingleTapTest extends TouchTestBase {

  private TouchActions getBuilder(WebDriver driver) {
    return new TouchActions(driver);
  }

  private void singleTapOnElement(String elementId) {
    WebElement toSingleTap = driver.findElement(By.id(elementId));
    Action singleTap = getBuilder(driver).singleTap(toSingleTap).build();
    singleTap.perform();
  }

  @Test
  public void testCanSingleTapOnALinkAndFollowIt() {
    driver.get(pages.clicksPage);
    singleTapOnElement("normal");
    wait.until(titleIs("XHTML Test Page"));
  }

  @Test
  public void testCanSingleTapOnAnAnchorAndNotReloadThePage() {
    driver.get(pages.clicksPage);
    ((JavascriptExecutor) driver).executeScript("document.latch = true");
    singleTapOnElement("anchor");
    Boolean samePage = (Boolean) ((JavascriptExecutor) driver)
        .executeScript("return document.latch");

    assertThat(samePage).isTrue();
  }
}
