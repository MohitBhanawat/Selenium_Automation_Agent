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

package org.smartqa.automationagent.support.pagefactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.smartqa.automationagent.JavascriptExecutor;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebElement;
import org.smartqa.automationagent.remote.RemoteWebElement;
import org.smartqa.automationagent.remote.internal.WebElementToJsonConverter;
import org.smartqa.automationagent.support.ByIdOrName;
import org.smartqa.automationagent.support.CacheLookup;
import org.smartqa.automationagent.support.FindBy;
import org.smartqa.automationagent.support.PageFactory;
import org.smartqa.automationagent.testing.JUnit4TestBase;

import java.util.List;

public class UsingPageFactoryTest extends JUnit4TestBase {

  @Test
  public void canExecuteJsUsingDecoratedElements() {
    driver.get(pages.xhtmlTestPage);

    Page page = new Page();
    PageFactory.initElements(driver, page);

    String tagName = (String) ((JavascriptExecutor) driver).executeScript(
        "return arguments[0].tagName", page.formElement);

    assertThat(tagName).isEqualToIgnoringCase("form");
  }

  @Test
  public void canListDecoratedElements() {
    driver.get(pages.xhtmlTestPage);

    Page page = new Page();
    PageFactory.initElements(driver, page);

    assertThat(page.divs).hasSize(13);
    for (WebElement link : page.divs) {
      assertThat(link.getTagName()).isEqualToIgnoringCase("div");
    }
  }

  @Test
  public void testDecoratedElementsShouldBeUnwrapped() {
    final RemoteWebElement element = new RemoteWebElement();
    element.setId("foo");

    WebDriver driver = mock(WebDriver.class);
    when(driver.findElement(new ByIdOrName("element"))).thenReturn(element);

    PublicPage page = new PublicPage();
    PageFactory.initElements(driver, page);

    Object seen = new WebElementToJsonConverter().apply(page.element);
    Object expected = new WebElementToJsonConverter().apply(element);

    assertThat(seen).isEqualTo(expected);
  }


  public class PublicPage {
    public WebElement element;
  }

  public static class Page {
    @FindBy(name = "someForm")
    WebElement formElement;

    @FindBy(tagName = "div")
    @CacheLookup
    List<WebElement> divs;
  }
}
