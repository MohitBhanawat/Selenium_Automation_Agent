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

public class TestLocators extends InternalSelenseTestBase {
  @Test
  public void testLocators() {
    automationAgent.open("test_locators.html");
    // Id location
    verifyEquals(automationAgent.getText("id=id1"), "this is the first element");
    verifyFalse(automationAgent.isElementPresent("id=name1"));
    verifyFalse(automationAgent.isElementPresent("id=id4"));
    verifyEquals(automationAgent.getAttribute("id=id1@class"), "a1");
    // name location
    verifyEquals(automationAgent.getText("name=name1"), "this is the second element");
    verifyFalse(automationAgent.isElementPresent("name=id1"));
    verifyFalse(automationAgent.isElementPresent("name=notAName"));
    verifyEquals(automationAgent.getAttribute("name=name1@class"), "a2");
    // class location
    verifyEquals(automationAgent.getText("class=a3"), "this is the third element");
    // alt location
    verifyTrue(automationAgent.isElementPresent("alt=banner"));
    // identifier location
    verifyEquals(automationAgent.getText("identifier=id1"), "this is the first element");
    verifyFalse(automationAgent.isElementPresent("identifier=id4"));
    verifyEquals(automationAgent.getAttribute("identifier=id1@class"), "a1");
    verifyEquals(automationAgent.getText("identifier=name1"), "this is the second element");
    verifyEquals(automationAgent.getAttribute("identifier=name1@class"), "a2");
    // DOM Traversal location
    verifyEquals(automationAgent.getText("dom=document.links[1]"), "this is the second element");
    verifyEquals(automationAgent.getText("dom=function foo() {return document.links[1];}; foo();"),
                 "this is the second element");
    verifyEquals(automationAgent.getText("dom=function foo() {\nreturn document.links[1];};\nfoo();"),
                 "this is the second element");
    verifyEquals(automationAgent.getAttribute("dom=document.links[1]@class"), "a2");
    verifyFalse(automationAgent.isElementPresent("dom=document.links[9]"));
    verifyFalse(automationAgent.isElementPresent("dom=foo"));
    // Link location
    verifyTrue(automationAgent.isElementPresent("link=this is the second element"));
    assertTrue(automationAgent.isTextPresent("this is the second element"));
    verifyTrue(automationAgent.isElementPresent("link=this * second element"));
    verifyTrue(automationAgent.isElementPresent("link=regexp:this [aeiou]s the second element"));
    verifyEquals(automationAgent.getAttribute("link=this is the second element@class"), "a2");
    verifyFalse(automationAgent.isElementPresent("link=this is not an element"));
    // SEL-484: IE: Can't select element by ID when there's another earlier element whose "name"
    // matches the ID
    verifyTrue(automationAgent.isElementPresent("name=foobar"));
    verifyTrue(automationAgent.isElementPresent("id=foobar"));
    // SEL-608:
    // "ID selector does not work when an element on the page has a name parameter equal to id"
    verifyTrue(automationAgent.isElementPresent("id=myForm"));
  }
}
