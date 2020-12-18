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

public class TestImplicitLocators extends InternalSelenseTestBase {
  @Test
  public void testImplicitLocators() {
    automationAgent.open("test_locators.html");
    verifyEquals(automationAgent.getText("id1"), "this is the first element");
    verifyEquals(automationAgent.getAttribute("id1@class"), "a1");
    verifyEquals(automationAgent.getText("name1"), "this is the second element");
    verifyEquals(automationAgent.getAttribute("name1@class"), "a2");
    verifyEquals(automationAgent.getText("document.links[1]"), "this is the second element");
    verifyEquals(automationAgent.getAttribute("document.links[1]@class"), "a2");
    verifyEquals(automationAgent.getAttribute("//img[contains(@src, 'banner.gif')]/@alt"), "banner");
    verifyEquals(automationAgent.getText("//body/a[2]"), "this is the second element");
  }
}
