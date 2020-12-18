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

public class TestSelect extends InternalSelenseTestBase {
  @Test
  public void testSelect() {
    automationAgent.open("test_select.html");
    assertTrue(automationAgent.isSomethingSelected("theSelect"));
    assertEquals(automationAgent.getSelectedLabel("theSelect"), "Second Option");
    automationAgent.select("theSelect", "index=4");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Fifth Option");
    verifyEquals(automationAgent.getSelectedIndex("theSelect"), "4");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Fifth Option");
    verifyEquals(join(automationAgent.getSelectedLabels("theSelect"), ','), "Fifth Option");
    automationAgent.select("theSelect", "Third Option");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Third Option");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Third Option");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Third Option");
    automationAgent.select("theSelect", "label=Fourth Option");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Fourth Option");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Fourth Option");
    automationAgent.select("theSelect", "value=option6");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Sixth Option");
    verifyEquals(automationAgent.getSelectedValue("theSelect"), "option6");
    verifyEquals(automationAgent.getSelectedValue("theSelect"), "option6");
    automationAgent.select("theSelect", "value=");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Empty Value Option");
    automationAgent.select("theSelect", "id=o4");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Fourth Option");
    verifyEquals(automationAgent.getSelectedId("theSelect"), "o4");
    automationAgent.select("theSelect", "");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "");
    verifyEquals(join(automationAgent.getSelectedLabels("theSelect"), ','), "");
    try {
      automationAgent.select("theSelect", "Not an option");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      automationAgent.addSelection("theSelect", "Fourth Option");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      automationAgent.removeSelection("theSelect", "Fourth Option");
      fail("expected failure");
    } catch (Throwable e) {
    }
    verifyEquals(
        join(automationAgent.getSelectOptions("theSelect"), ','),
        "First Option,Second Option,Third Option,Fourth Option,Fifth Option,Sixth Option,Empty Value Option,");
  }
}
