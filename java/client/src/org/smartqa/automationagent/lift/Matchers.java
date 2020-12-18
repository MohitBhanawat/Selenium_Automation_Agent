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

public class Matchers {

  public static org.hamcrest.Matcher<org.smartqa.automationagent.WebElement> attribute(
      java.lang.String attributeName, org.hamcrest.Matcher<java.lang.String> valueMatcher) {
    return org.smartqa.automationagent.lift.match.AttributeMatcher.attribute(attributeName, valueMatcher);
  }

  public static org.hamcrest.Matcher<java.lang.Integer> atLeast(int count) {
    return org.smartqa.automationagent.lift.match.NumericalMatchers.atLeast(count);
  }

  public static org.hamcrest.Matcher<java.lang.Integer> exactly(int count) {
    return org.smartqa.automationagent.lift.match.NumericalMatchers.exactly(count);
  }

  public static org.hamcrest.Matcher<org.smartqa.automationagent.WebElement> text(
      org.hamcrest.Matcher<java.lang.String> textMatcher) {
    return org.smartqa.automationagent.lift.match.TextMatcher.text(textMatcher);
  }

  public static org.hamcrest.Matcher<org.smartqa.automationagent.WebElement> selection() {
    return org.smartqa.automationagent.lift.match.SelectionMatcher.selection();
  }

  public static org.hamcrest.Matcher<org.smartqa.automationagent.WebElement> value(Object value) {
    return org.smartqa.automationagent.lift.match.ValueMatcher.value(value);
  }

  public static org.hamcrest.Matcher<org.smartqa.automationagent.WebElement> displayed() {
    return org.smartqa.automationagent.lift.match.DisplayedMatcher.displayed();
  }
}
