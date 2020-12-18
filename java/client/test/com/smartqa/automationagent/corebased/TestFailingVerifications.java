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

package com.smartqa.automationagent.corebased;

import org.junit.Test;

import com.smartqa.automationagent.InternalSelenseTestBase;

public class TestFailingVerifications extends InternalSelenseTestBase {
  @Test
  public void testFailingVerifications() {
    automationAgent.open("/test_verifications.html");
    try {
      assertTrue(automationAgent.getLocation().matches(
          "^[\\s\\S]*/common/legacy/not_test_verifications\\.html$"));
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(automationAgent.getValue("theText"), "not the text value");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertNotEquals("the text value", automationAgent.getValue("theText"));
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(automationAgent.getValue("theHidden"), "not the hidden value");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(automationAgent.getText("theSpan"), "this is not the span");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertTrue(automationAgent.isTextPresent("this is not the span"));
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertFalse(automationAgent.isTextPresent("this is the span"));
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertTrue(automationAgent.isElementPresent("notTheSpan"));
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertFalse(automationAgent.isElementPresent("theSpan"));
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(automationAgent.getTable("theTable.2.0"), "a");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(automationAgent.getSelectedIndex("theSelect"), "2");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertTrue(automationAgent.getSelectedValue("theSelect").matches("^opt[\\s\\S]*3$"));
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(automationAgent.getSelectedLabel("theSelect"), "third option");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(join(automationAgent.getSelectOptions("theSelect"), ','),
          "first\\,option,second option");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(automationAgent.getAttribute("theText@class"), "bar");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertNotEquals("foo", automationAgent.getAttribute("theText@class"));
      fail("expected failure");
    } catch (Throwable e) {
    }
  }
}
