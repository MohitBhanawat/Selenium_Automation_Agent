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

// Generated source.
package org.smartqa.automationagent.lift;

import org.hamcrest.Description;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebElement;
import org.smartqa.automationagent.lift.find.BaseFinder;
import org.smartqa.automationagent.lift.find.Finder;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;


public class Finders {

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder div() {
    return org.smartqa.automationagent.lift.find.DivFinder.div();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder div(String id) {
    return org.smartqa.automationagent.lift.find.DivFinder.div(id);
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder link() {
    return org.smartqa.automationagent.lift.find.LinkFinder.link();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder link(java.lang.String anchorText) {
    return org.smartqa.automationagent.lift.find.LinkFinder.link(anchorText);
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder links() {
    return org.smartqa.automationagent.lift.find.LinkFinder.links();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder titles() {
    return org.smartqa.automationagent.lift.find.PageTitleFinder.titles();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder title() {
    return org.smartqa.automationagent.lift.find.PageTitleFinder.title();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder title(String title) {
    return org.smartqa.automationagent.lift.find.PageTitleFinder.title(title);
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder images() {
    return org.smartqa.automationagent.lift.find.ImageFinder.images();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder image() {
    return org.smartqa.automationagent.lift.find.ImageFinder.image();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder table() {
    return org.smartqa.automationagent.lift.find.TableFinder.table();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder tables() {
    return org.smartqa.automationagent.lift.find.TableFinder.tables();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder cell() {
    return org.smartqa.automationagent.lift.find.TableCellFinder.cell();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder cells() {
    return org.smartqa.automationagent.lift.find.TableCellFinder.cells();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder imageButton() {
    return org.smartqa.automationagent.lift.find.InputFinder.imageButton();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder imageButton(String label) {
    return org.smartqa.automationagent.lift.find.InputFinder.imageButton(label);
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder radioButton() {
    return org.smartqa.automationagent.lift.find.InputFinder.radioButton();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder radioButton(String id) {
    return org.smartqa.automationagent.lift.find.InputFinder.radioButton(id);
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder textbox() {
    return org.smartqa.automationagent.lift.find.InputFinder.textbox();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder button() {
    return org.smartqa.automationagent.lift.find.InputFinder.submitButton();
  }

  public static org.smartqa.automationagent.lift.find.HtmlTagFinder button(String label) {
    return org.smartqa.automationagent.lift.find.InputFinder.submitButton(label);
  }

  /**
   * A finder which returns the first element matched - such as if you have multiple elements which
   * match the finder (such as multiple links with the same text on a page etc)
   *
   * @param finder finder from which context to search
   * @return finder that will return the first match
   */
  public static Finder<WebElement, WebDriver> first(final Finder<WebElement, WebDriver> finder) {
    return new BaseFinder<WebElement, WebDriver>() {

      @Override
      public Collection<WebElement> findFrom(WebDriver context) {
        Collection<WebElement> collection = super.findFrom(context);
        if (!collection.isEmpty()) {
          Iterator<WebElement> iter = collection.iterator();
          iter.hasNext();
          return Collections.singletonList(iter.next());
        }
        return collection;
      }

      @Override
      protected Collection<WebElement> extractFrom(WebDriver context) {
        return finder.findFrom(context);
      }

      @Override
      protected void describeTargetTo(Description description) {
        description.appendText("first ");
        finder.describeTo(description);
      }
    };
  }
}
