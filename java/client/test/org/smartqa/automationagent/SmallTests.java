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
package org.smartqa.automationagent;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.smartqa.automationagent.interactions.CompositeActionTest;
import org.smartqa.automationagent.interactions.IndividualKeyboardActionsTest;
import org.smartqa.automationagent.interactions.IndividualMouseActionsTest;
import org.smartqa.automationagent.interactions.PointerInputTest;
import org.smartqa.automationagent.io.FileHandlerTest;
import org.smartqa.automationagent.io.TemporaryFilesystemTest;
import org.smartqa.automationagent.io.ZipTest;
import org.smartqa.automationagent.logging.LoggingTest;
import org.smartqa.automationagent.logging.PerformanceLoggingMockTest;
import org.smartqa.automationagent.net.LinuxEphemeralPortRangeDetectorTest;
import org.smartqa.automationagent.net.NetworkUtilsTest;
import org.smartqa.automationagent.net.UrlCheckerTest;
import org.smartqa.automationagent.os.CommandLineTest;
import org.smartqa.automationagent.testing.IgnoreComparatorUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    ByTest.class,
    CommandLineTest.class,
    CookieTest.class,
    CompositeActionTest.class,
    DimensionTest.class,
    FileHandlerTest.class,
    IgnoreComparatorUnitTest.class,
    ImmutableCapabilitiesTest.class,
    IndividualKeyboardActionsTest.class,
    IndividualMouseActionsTest.class,
    KeysTest.class,
    LinuxEphemeralPortRangeDetectorTest.class,
    LoggingTest.class,
    NetworkUtilsTest.class,
    OutputTypeTest.class,
    PerformanceLoggingMockTest.class,
    PlatformTest.class,
    PointTest.class,
    PointerInputTest.class,
    ProxyTest.class,
    TemporaryFilesystemTest.class,
    UrlCheckerTest.class,
    WebDriverExceptionTest.class,
    ZipTest.class,

    org.smartqa.automationagent.support.SmallTests.class,
    com.smartqa.automationagent.webdriven.SmallTests.class
})
public class SmallTests {}
