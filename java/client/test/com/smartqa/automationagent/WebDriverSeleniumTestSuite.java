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

package com.smartqa.automationagent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.smartqa.automationagent.corebased.SeleniumMouseTest;
import com.smartqa.automationagent.corebased.TestAddLocationStrategy;
import com.smartqa.automationagent.corebased.TestAddSelection;
import com.smartqa.automationagent.corebased.TestAlerts;
import com.smartqa.automationagent.corebased.TestBrowserVersion;
import com.smartqa.automationagent.corebased.TestCheckUncheck;
import com.smartqa.automationagent.corebased.TestClick;
import com.smartqa.automationagent.corebased.TestClickAt;
import com.smartqa.automationagent.corebased.TestClickJavascriptHref;
import com.smartqa.automationagent.corebased.TestClickJavascriptHrefChrome;
import com.smartqa.automationagent.corebased.TestCommandError;
import com.smartqa.automationagent.corebased.TestComments;
import com.smartqa.automationagent.corebased.TestConfirmations;
import com.smartqa.automationagent.corebased.TestCssLocators;
import com.smartqa.automationagent.corebased.TestDojoDragAndDrop;
import com.smartqa.automationagent.corebased.TestEditable;
import com.smartqa.automationagent.corebased.TestElementIndex;
import com.smartqa.automationagent.corebased.TestElementOrder;
import com.smartqa.automationagent.corebased.TestElementPresent;
import com.smartqa.automationagent.corebased.TestErrorChecking;
import com.smartqa.automationagent.corebased.TestEval;
import com.smartqa.automationagent.corebased.TestEvilClosingWindow;
import com.smartqa.automationagent.corebased.TestFailingAssert;
import com.smartqa.automationagent.corebased.TestFailingVerifications;
import com.smartqa.automationagent.corebased.TestFocusOnBlur;
import com.smartqa.automationagent.corebased.TestFramesClick;
import com.smartqa.automationagent.corebased.TestFramesClickJavascriptHref;
import com.smartqa.automationagent.corebased.TestFramesOpen;
import com.smartqa.automationagent.corebased.TestFramesSpecialTargets;
import com.smartqa.automationagent.corebased.TestFunkEventHandling;
import com.smartqa.automationagent.corebased.TestGet;
import com.smartqa.automationagent.corebased.TestGetTextContent;
import com.smartqa.automationagent.corebased.TestGettingValueOfCheckbox;
import com.smartqa.automationagent.corebased.TestGettingValueOfRadioButton;
import com.smartqa.automationagent.corebased.TestGoBack;
import com.smartqa.automationagent.corebased.TestHighlight;
import com.smartqa.automationagent.corebased.TestHtmlSource;
import com.smartqa.automationagent.corebased.TestImplicitLocators;
import com.smartqa.automationagent.corebased.TestJavaScriptAttributes;
import com.smartqa.automationagent.corebased.TestLocators;
import com.smartqa.automationagent.corebased.TestMultiSelect;
import com.smartqa.automationagent.corebased.TestOpen;
import com.smartqa.automationagent.corebased.TestOpenInTargetFrame;
import com.smartqa.automationagent.corebased.TestPatternMatching;
import com.smartqa.automationagent.corebased.TestPause;
import com.smartqa.automationagent.corebased.TestProxy;
import com.smartqa.automationagent.corebased.TestQuickOpen;
import com.smartqa.automationagent.corebased.TestSelect;
import com.smartqa.automationagent.corebased.TestSelectMultiLevelFrame;
import com.smartqa.automationagent.corebased.TestSelectPopUp;
import com.smartqa.automationagent.corebased.TestSelectWindow;
import com.smartqa.automationagent.corebased.TestSelectWindowTitle;
import com.smartqa.automationagent.corebased.TestStore;
import com.smartqa.automationagent.corebased.TestSubmit;
import com.smartqa.automationagent.corebased.TestTextWhitespace;
import com.smartqa.automationagent.corebased.TestType;
import com.smartqa.automationagent.corebased.TestTypeRichText;
import com.smartqa.automationagent.corebased.TestVerifications;
import com.smartqa.automationagent.corebased.TestVisibility;
import com.smartqa.automationagent.corebased.TestWait;
import com.smartqa.automationagent.corebased.TestWaitFor;
import com.smartqa.automationagent.corebased.TestWaitForNot;
import com.smartqa.automationagent.corebased.TestWaitInPopupWindow;
import com.smartqa.automationagent.corebased.TestXPathLocators;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    SeleniumMouseTest.class,
    StartTest.class,
    TestAddLocationStrategy.class,
    TestAddSelection.class,
    TestAlerts.class,
//    TestBasicAuth.class,
    TestBrowserVersion.class,
    TestCheckUncheck.class,
    TestClick.class,
    TestClickAt.class,
//    TestClickBlankTarget.class,
    TestClickJavascriptHref.class,
    TestClickJavascriptHrefChrome.class, // alerts
//    TestClickJavascriptHrefWithVoidChrome.class, // fails in IE
    TestCommandError.class,
    TestComments.class,
    TestConfirmations.class, // fails in IE
//    TestCookie.class,
    TestCssLocators.class,
//    TestCursorPosition.class,
    TestDojoDragAndDrop.class, // fails in IE
//    TestDomainCookie.class,
//    TestDragAndDrop.class,
    TestEditable.class,
    TestElementIndex.class,
    TestElementOrder.class,
    TestElementPresent.class,
    TestErrorChecking.class,
    TestEval.class,
    TestEvilClosingWindow.class,
    TestFailingAssert.class,
    TestFailingVerifications.class,
    TestFocusOnBlur.class,
    TestFramesClick.class,
    TestFramesClickJavascriptHref.class,
//    TestFramesNested.class,
    TestFramesOpen.class,
    TestFramesSpecialTargets.class,
    TestFunkEventHandling.class,
    TestGet.class,
    TestGetTextContent.class,
    TestGettingValueOfCheckbox.class,
    TestGettingValueOfRadioButton.class,
    TestGoBack.class,
    TestHighlight.class,
    TestHtmlSource.class,
    TestImplicitLocators.class,
    TestJavaScriptAttributes.class,
//    TestJavascriptParameters.class,
    TestLargeHtml.class,
    TestLocators.class,
    TestMultiSelect.class,
//    TestModalDialog.class,
    TestOpen.class,
    TestOpenInTargetFrame.class,
    TestPatternMatching.class,
    TestPause.class,
//    TestPrompt.class,
    TestProxy.class,
    TestQuickOpen.class,
//    TestRefresh.class,
//    TestRollup.class,
    TestSelect.class,
    TestSelectMultiLevelFrame.class,
    TestSelectPopUp.class,
    TestSelectWindow.class,
    TestSelectWindowTitle.class,
//    TestSetSpeed.class,
    TestStore.class,
    TestSubmit.class,
    TestTextWhitespace.class,
    TestType.class,
    TestTypeRichText.class,
//    TestUIElementLocators.class,
//    TestUseXpathLibrary.class,
    TestVerifications.class,
    TestVisibility.class,
    TestWait.class,
    TestWaitFor.class,
    TestWaitForNot.class,
    TestWaitInPopupWindow.class,
//    TestXPathLocatorInXHtml.class,
    TestXPathLocators.class,
    RealDealIntegrationTest.class
})
public class WebDriverSeleniumTestSuite extends BaseSuite {
  // Empty
}
