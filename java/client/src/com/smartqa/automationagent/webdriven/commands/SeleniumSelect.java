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

package com.smartqa.automationagent.webdriven.commands;

import java.util.ArrayList;
import java.util.List;

import org.smartqa.automationagent.By;
import org.smartqa.automationagent.JavascriptExecutor;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebElement;

import com.smartqa.automationagent.SeleniumException;
import com.smartqa.automationagent.webdriven.ElementFinder;
import com.smartqa.automationagent.webdriven.JavascriptLibrary;

public class SeleniumSelect {

  private final String findOption;
  private final WebDriver driver;
  private final WebElement select;

  public SeleniumSelect(
    JavascriptLibrary library,
    ElementFinder finder,
    WebDriver driver,
    String locator) {
    this.driver = driver;

    findOption =
      "return (" + library.getSeleniumScript("findOption.js") + ").apply(null, arguments)";

    select = finder.findElement(driver, locator);
    if (!"select".equals(select.getTagName().toLowerCase())) {
      throw new SeleniumException("Element is not a select element: " + locator);
    }
  }

  public void setSelected(String optionLocator) {
    if (isMultiple()) {
      for (WebElement opt : select.findElements(By.tagName("option"))) {
        if (opt.isSelected()) {
          opt.click();
        }
      }
    }

    WebElement option = findOption(optionLocator);
    if (!option.isSelected()) {
      option.click();
    }
  }

  public void addSelection(String optionLocator) {
    assertSupportsMultipleSelections();

    WebElement option = findOption(optionLocator);
    if (!option.isSelected()) {
      option.click();
    }
  }

  public void removeSelection(String optionLocator) {
    assertSupportsMultipleSelections();

    WebElement option = findOption(optionLocator);
    if (option.isSelected()) {
      option.click();
    }
  }

  public List<WebElement> getSelectedOptions() {
    List<WebElement> toReturn = new ArrayList<>();

    for (WebElement option : select.findElements(By.tagName("option"))) {
      if (option.isSelected()) {
        toReturn.add(option);
      }
    }

    return toReturn;
  }

  public WebElement findOption(String optionLocator) {
    return (WebElement) ((JavascriptExecutor) driver)
        .executeScript(findOption, select, optionLocator);
  }

  private void assertSupportsMultipleSelections() {
    if (!isMultiple()) {
      throw new SeleniumException(
          "You may only add a selection to a select that supports multiple selections");
    }
  }

  private boolean isMultiple() {
    String multipleValue = select.getAttribute("multiple");
    return "true".equals(multipleValue) || "multiple".equals(multipleValue);
  }

  public List<WebElement> getAllOptions() {
    return select.findElements(By.tagName("option"));
  }
}
