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

import com.smartqa.automationagent.InternalSelenseTestBase;
import com.smartqa.automationagent.SeleniumException;

import org.junit.Test;

public class TestAddSelection extends InternalSelenseTestBase {
  @Test
  public void addingToSelectionWhenSelectHasEmptyMultipleAttribute() {
    automationAgent.open("test_multiple_select.html");

    automationAgent.addSelection("sel", "select_2");
    automationAgent.addSelection("sel", "select_3");

    String[] found = automationAgent.getSelectedIds("name=sel");

    assertEquals(2, found.length);
    assertEquals("select_2", found[0]);
    assertEquals("select_3", found[1]);
  }

  @Test
  public void addingToSelectionShouldThrowExceptionForSingleSelectionList() {
    automationAgent.open("test_select.html");

    String[] before = automationAgent.getSelectedIds("theSelect");

    try {
      automationAgent.addSelection("theSelect", "Second Option");
      fail("Expected SeleniumException");
    } catch (SeleniumException ex) {
      // Expected exception. Message is different in DefaultAutomationAgent
      // and WebDriverBackedAutomationAgent
    }

    assertEquals(before, automationAgent.getSelectedIds("theSelect"));
  }
}
