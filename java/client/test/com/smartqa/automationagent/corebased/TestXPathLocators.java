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

public class TestXPathLocators extends InternalSelenseTestBase {
  @Test
  public void testXPathLocators() {
    automationAgent.open("test_locators.html");
    verifyEquals(automationAgent.getText("xpath=//a"), "this is the first element");
    verifyEquals(automationAgent.getText("xpath=//a[@class='a2']"), "this is the second element");
    verifyEquals(automationAgent.getText("xpath=//*[@class='a2']"), "this is the second element");
    verifyEquals(automationAgent.getText("xpath=//a[2]"), "this is the second element");
    verifyEquals(automationAgent.getText("xpath=//a[position()=2]"), "this is the second element");
    verifyFalse(automationAgent.isElementPresent("xpath=//a[@href='foo']"));
    verifyEquals(automationAgent.getAttribute("xpath=//a[contains(@href,'#id1')]/@class"), "a1");
    verifyTrue(automationAgent.isElementPresent("xpath=//a[text()=\"this is the" + "\u00a0"
                                                + "third element\"]"));
    verifyEquals(automationAgent.getText("//a"), "this is the first element");
    verifyEquals(automationAgent.getAttribute("//a[contains(@href,'#id1')]/@class"), "a1");
    verifyEquals(
        automationAgent.getText("xpath=(//table[@class='stylee'])//th[text()='theHeaderText']/../td"),
        "theCellText");
    automationAgent.click("//input[@name='name2' and @value='yes']");
    verifyTrue(automationAgent.isElementPresent("xpath=//*[text()=\"right\"]"));
    verifyEquals(automationAgent.getValue("xpath=//div[@id='nested1']/div[1]//input[2]"), "nested3b");
    verifyEquals(automationAgent.getValue("xpath=id('nested1')/div[1]//input[2]"), "nested3b");
    verifyEquals(
        automationAgent.getValue("xpath=id('anotherNested')//div[contains(@id, 'useful')]//input"),
        "winner");
    automationAgent.assignId("xpath=//*[text()=\"right\"]", "rightButton");
    verifyTrue(automationAgent.isElementPresent("rightButton"));
    verifyEquals(automationAgent.getXpathCount("id('nested1')/div[1]//input"), "2");
    verifyEquals(automationAgent.getXpathCount("//div[@id='nonexistent']"), "0");
    verifyTrue(automationAgent.isElementPresent("xpath=//a[@href=\"javascript:doFoo('a', 'b')\"]"));
    verifyFalse(automationAgent.isElementPresent("xpath=id('foo')//applet"));
    try {
      assertTrue(automationAgent.isElementPresent("xpath=id('foo')//applet2"));
      fail("expected failure");
    } catch (Throwable e) {
    }
    try {
      assertTrue(automationAgent.isElementPresent("xpath=//a[0}"));
      fail("expected failure");
    } catch (Throwable e) {
    }

    // These cases are now covered by the "in play attributes" optimization.

    // <p>Test toggling of ignoreAttributesWithoutValue. The test must be performed using the
    // non-native ajaxslt engine. After the test, native xpaths are re-enabled.</p>
    // <table cellpadding="1" cellspacing="1" border="1">
    // <tbody>

    // <tr>
    // <td>allowNativeXpath</td>
    // <td>false</td>
    // <td>&nbsp;</td>
    // </tr>
    // <tr>
    // <td>ignoreAttributesWithoutValue</td>
    // <td>false</td>
    // <td>&nbsp;</td>
    // </tr>
    // <tr>
    // <td>verifyXpathCount</td>
    // <td>//div[@id='ignore']/a[@class]</td>
    // <td>2</td>
    // </tr>
    // <tr>
    // <td>verifyText</td>
    // <td>//div[@id='ignore']/a[@class][1]</td>
    // <td>over the rainbow</td>
    // </tr>
    // <tr>
    // <td>verifyText</td>
    // <td>//div[@id='ignore']/a[@class][2]</td>
    // <td>skies are blue</td>
    // </tr>
    // <tr>
    // <td>verifyXpathCount</td>
    // <td>//div[@id='ignore']/a[@class='']</td>
    // <td>1</td>
    // </tr>
    // <tr>
    // <td>verifyText</td>
    // <td>//div[@id='ignore']/a[@class='']</td>
    // <td>skies are blue</td>
    // </tr>
    // <tr>
    // <td>ignoreAttributesWithoutValue</td>
    // <td>true</td>
    // <td>&nbsp;</td>
    // </tr>
    // <tr>
    // <td>verifyXpathCount</td>
    // <td>//div[@id='ignore']/a[@class]</td>
    // <td>1</td>
    // </tr>
    // <tr>
    // <td>verifyText</td>
    // <td>//div[@id='ignore']/a[@class]</td>
    // <td>over the rainbow</td>
    // </tr>
    // <tr>
    // <td>verifyXpathCount</td>
    // <td>//div[@id='ignore']/a[@class='']</td>
    // <td>0</td>
    // </tr>
    // <tr>
    // <td>allowNativeXpath</td>
    // <td>true</td>
    // <td>&nbsp;</td>
    // </tr>

  }
}
