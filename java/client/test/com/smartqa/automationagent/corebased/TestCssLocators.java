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

import com.smartqa.automationagent.AutomationAgent;
import com.smartqa.automationagent.InternalSelenseTestBase;

import org.junit.Test;

public class TestCssLocators extends InternalSelenseTestBase {
  @Test
  public void testCssLocators() {

    // Unimplemented features:
    // namespace
    // pseudo element
    // ::first-line
    // ::first-letter
    // ::selection
    // ::before
    // ::after
    // pseudo class including:
    // :nth-of-type
    // :nth-last-of-type
    // :first-of-type
    // :last-of-type
    // :only-of-type
    // :visited
    // :hover
    // :active
    // :focus
    // :indeterminate
    //

    automationAgent.open("test_locators.html");

    boolean isIe = "true".equals(automationAgent.getEval("browserVersion.isIE;"));

    // css2 selector test

    // universal selector

    verifyTrue(automationAgent.isElementPresent("css=*"));

    // only element type

    verifyEquals(automationAgent.getText("css=p"), "this is the first element in the document");

    verifyEquals(automationAgent.getText("css=a"), "this is the first element");

    // id selector

    verifyEquals(automationAgent.getText("css=a#id3"), "this is the third element");

    // attribute selector

    verifyTrue(automationAgent.isElementPresent("css=input[name]"));

    verifyEquals(automationAgent.getText("css=a[href=\"#id3\"]"), "this is the third element");

    verifyFalse(automationAgent.isElementPresent("css=span[automationagent:foo]"));

    verifyEquals(automationAgent.getText("css=a[class~=\"class2\"]"), "this is the fifth element");

    verifyEquals(automationAgent.getText("css=a[lang|=\"en\"]"), "this is the sixth element");

    // class selector

    verifyTrue(automationAgent.isElementPresent("css=a.a1"));

    // pseudo class selector

    verifyEquals(automationAgent.getText("css=th:first-child"), "theHeaderText");

    // descendant combinator

    verifyEquals(automationAgent.getText("css=div#combinatorTest a"), "and grandson");

    // child combinator

    verifyEquals(automationAgent.getText("css=div#combinatorTest > span"), "this is a child and grandson");

    // preceding combinator

    verifyEquals(automationAgent.getText("css=span#firstChild + span"), "another child");

    // css3 selector test

    // attribuite test

    verifyEquals(automationAgent.getText("css=a[name^=\"foo\"]"), "foobar");

    verifyEquals(automationAgent.getText("css=a[name$=\"foo\"]"), "barfoo");

    verifyEquals(automationAgent.getText("css=a[name*=\"zoo\"]"), "foozoobar");

    verifyEquals(automationAgent.getText("css=a[name*=\"name\"][alt]"), "this is the second element");

    // pseudo class test

    verifyEquals(automationAgent.getText("css=div#structuralPseudo :nth-child(2n)"), "span2");

    verifyEquals(automationAgent.getText("css=div#structuralPseudo :nth-child(2)"), "span2");

    verifyEquals(automationAgent.getText("css=div#structuralPseudo :nth-child(-n+6)"), "span1");

    verifyEquals(automationAgent.getText("css=div#onlyChild span:only-child"), "only child");

    verifyTrue(automationAgent.isElementPresent("css=span:empty"));

    // TODO(simon): Re-enable this.
    // verifyEquals(automationagent.getText("css=div#targetTest span:target"), "target");

    verifyTrue(automationAgent.isElementPresent("css=input[type=\"text\"]:enabled"));

    verifyTrue(automationAgent.isElementPresent("css=input[type=\"text\"]:disabled"));

    verifyTrue(automationAgent.isElementPresent("css=input[type=\"checkbox\"]:checked"));

    verifyEquals(automationAgent.getText("css=a:contains(\"zoo\")"), "foozoobar");

    verifyEquals(automationAgent.getText("css=div#structuralPseudo span:not(:first-child)"), "span2");

    // combinator test

    verifyEquals(automationAgent.getText("css=div#combinatorTest span#firstChild ~ span"), "another child");

    if (!isIe) {
      verifyEquals(automationAgent.getText("css=div#structuralPseudo :first-child"), "span1");

      verifyEquals(automationAgent.getText("css=div#structuralPseudo :last-child"), "div4");

      verifyEquals(automationAgent.getText("css=div#structuralPseudo :not(span):not(:last-child)"), "div1");
    }

    if (isCapableOfAdvancedSelectors(automationAgent, isIe)) {
      // Versions of firefox prior to 3.5 don't propogate the lang property.
      verifyEquals(automationAgent.getText("css=a:lang(en)"), "this is the first element");

      verifyEquals(automationAgent.getText("css=#linkPseudoTest :link"), "link pseudo test");

      verifyTrue(automationAgent.isElementPresent("css=html:root"));

      verifyEquals(automationAgent.getText("css=div#structuralPseudo :nth-last-child(4n+1)"), "span4");

      verifyEquals(automationAgent.getText("css=div#structuralPseudo :nth-last-child(2)"), "div3");

      verifyEquals(automationAgent.getText("css=div#structuralPseudo :nth-last-child(-n+6)"), "span3");
    }
  }

  private boolean isCapableOfAdvancedSelectors(AutomationAgent automationAgent, boolean isIe) {
    String isFirefox = automationAgent.getEval("browserVersion.isFirefox;");

    String version = automationAgent.getEval("browserVersion.firefoxVersion");

    if (isIe) {
      return false;
    }

    if (Boolean.valueOf(isFirefox)) {
      return version == null || !version.startsWith("3.0");
    }

    return true;
  }
}
