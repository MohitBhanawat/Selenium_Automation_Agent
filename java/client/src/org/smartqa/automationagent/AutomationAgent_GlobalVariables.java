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

public class AutomationAgent_GlobalVariables {
  private String page_title;
  private String locator_string;
  private String smartidentifier_locator_string;
  private String string_from_extension;
  private boolean in_smart_identifier;
  private String combination;
  private String valid_xpath;
  private String locator_as_is;

  public String getLocator_as_is() {
    return locator_as_is;
  }

  public void setLocator_as_is(String locator_as_is) {
    this.locator_as_is = locator_as_is;
  }

  public String getPage_title() {
    return this.page_title;
  }

  public void setPage_title(String page_title) {
    this.page_title = page_title.replace(" ", "").trim();
  }

  public String getLocator_string() {
    return this.locator_string;
  }

  public void setLocator_string(String locator_string) {
    this.locator_string = locator_string.replace(" ", "").trim();
  }

  public String getString_from_extension() {
    return this.string_from_extension;
  }

  public void setString_from_extension(String string_from_extension) {
    this.string_from_extension = string_from_extension;
  }

  public boolean isIn_smart_identifier() {
    return in_smart_identifier;
  }

  public void setIn_smart_identifier(boolean in_smart_identifier) {
    this.in_smart_identifier = in_smart_identifier;
  }

  public String getSmartidentifier_locator_string() {
    return smartidentifier_locator_string;
  }

  public void setSmartidentifier_locator_string(String smartidentifier_locator_string) {
    this.smartidentifier_locator_string = smartidentifier_locator_string;
  }

  public String getCombination() {
    return combination;
  }

  public void setCombination(String combination) {
    this.combination = combination;
  }

  public String getValid_xpath() {
    return valid_xpath;
  }

  public void setValid_xpath(String valid_xpath) {
    this.valid_xpath = valid_xpath;
  }
}
