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

public class TestMultiSelect extends InternalSelenseTestBase {
  @Test
  public void testMultiSelect() {
    automationAgent.open("test_multiselect.html");
    assertEquals(join(automationAgent.getSelectedLabels("theSelect"), ','), "Second Option");
    automationAgent.select("theSelect", "index=4");
    verifyEquals(join(automationAgent.getSelectedLabels("theSelect"), ','), "Fifth Option");
    automationAgent.addSelection("theSelect", "Third Option");
    automationAgent.addSelection("theSelect", "value=");
    verifyEquals(join(automationAgent.getSelectedLabels("theSelect"), ','),
        "Third Option,Fifth Option,Empty Value Option");
    automationAgent.removeSelection("theSelect", "id=o7");
    verifyEquals(join(automationAgent.getSelectedLabels("theSelect"), ','), "Third Option,Fifth Option");
    automationAgent.removeSelection("theSelect", "label=Fifth Option");
    verifyEquals(automationAgent.getSelectedLabel("theSelect"), "Third Option");
    automationAgent.addSelection("theSelect", "");
    verifyEquals(join(automationAgent.getSelectedLabels("theSelect"), ','), "Third Option,");
    automationAgent.removeSelection("theSelect", "");
    automationAgent.removeSelection("theSelect", "Third Option");
    try {
      assertEquals(automationAgent.getSelectedLabel("theSelect"), "");
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertEquals(join(automationAgent.getSelectedLabels("theSelect"), ','), "");
      fail("expected failure");
    } catch (Throwable e) {
    }
    verifyEquals(automationAgent.getValue("theSelect"), "");
    verifyFalse(automationAgent.isSomethingSelected("theSelect"));
    automationAgent.addSelection("theSelect", "Third Option");
    automationAgent.addSelection("theSelect", "value=");
    automationAgent.removeAllSelections("theSelect");
    verifyFalse(automationAgent.isSomethingSelected("theSelect"));
  }
}
