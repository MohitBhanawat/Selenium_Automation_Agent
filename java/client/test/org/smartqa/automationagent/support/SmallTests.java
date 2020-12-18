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
package org.smartqa.automationagent.support;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.smartqa.automationagent.support.events.EventFiringWebDriverTest;
import org.smartqa.automationagent.support.pagefactory.AjaxElementLocatorTest;
import org.smartqa.automationagent.support.pagefactory.AnnotationsTest;
import org.smartqa.automationagent.support.pagefactory.ByAllTest;
import org.smartqa.automationagent.support.pagefactory.ByChainedTest;
import org.smartqa.automationagent.support.pagefactory.DefaultElementLocatorTest;
import org.smartqa.automationagent.support.pagefactory.DefaultFieldDecoratorTest;
import org.smartqa.automationagent.support.pagefactory.internal.LocatingElementHandlerTest;
import org.smartqa.automationagent.support.pagefactory.internal.LocatingElementListHandlerTest;
import org.smartqa.automationagent.support.ui.ExpectedConditionsTest;
import org.smartqa.automationagent.support.ui.FluentWaitTest;
import org.smartqa.automationagent.support.ui.HowTest;
import org.smartqa.automationagent.support.ui.LoadableComponentTest;
import org.smartqa.automationagent.support.ui.QuotesTest;
import org.smartqa.automationagent.support.ui.SelectTest;
import org.smartqa.automationagent.support.ui.SlowLoadableComponentTest;
import org.smartqa.automationagent.support.ui.WebDriverWaitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AjaxElementLocatorTest.class,
    AnnotationsTest.class,
    ByChainedTest.class,
    ByAllTest.class,
    ColorTest.class,
    DefaultElementLocatorTest.class,
    DefaultFieldDecoratorTest.class,
    EventFiringWebDriverTest.class,
    ExpectedConditionsTest.class,
    FluentWaitTest.class,
    HowTest.class,
    LoadableComponentTest.class,
    LocatingElementHandlerTest.class,
    LocatingElementListHandlerTest.class,
    PageFactoryTest.class,
    SelectTest.class,
    QuotesTest.class,
    SlowLoadableComponentTest.class,
    ThreadGuardTest.class,
    WebDriverWaitTest.class
})
public class SmallTests {}
