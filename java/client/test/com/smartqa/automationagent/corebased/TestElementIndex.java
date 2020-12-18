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

public class TestElementIndex extends InternalSelenseTestBase {
  @Test
  public void testElementIndex() {
    automationAgent.open("test_element_order.html");
    assertEquals(automationAgent.getElementIndex("d2"), "1");
    assertEquals(automationAgent.getElementIndex("d1.1.1"), "0");
    verifyEquals(automationAgent.getElementIndex("d2"), "1");
    verifyEquals(automationAgent.getElementIndex("d1.2"), "5");
    assertNotEquals("2", automationAgent.getElementIndex("d2"));
    verifyNotEquals("2", automationAgent.getElementIndex("d2"));
  }
}
