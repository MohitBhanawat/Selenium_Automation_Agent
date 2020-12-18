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

import org.junit.Ignore;
import org.junit.Test;

import com.smartqa.automationagent.InternalSelenseTestBase;

import java.util.regex.Pattern;

@Ignore("Incorrectly calculated base path")
public class TestCookie extends InternalSelenseTestBase {
  @Test
  public void testCookie() {
    String base =
        automationAgent
            .getEval("parseUrl(canonicalize(absolutify(\"html\", automationagent.browserbot.baseUrl))).pathname;");
    System.out.println(base);
    automationAgent.open(base + "/path1/cookie1.html");
    automationAgent.deleteAllVisibleCookies();
    assertEquals(automationAgent.getCookie(), "");
    automationAgent.open(base + "/path2/cookie2.html");
    automationAgent.deleteAllVisibleCookies();
    assertEquals(automationAgent.getCookie(), "");
    automationAgent.open(base + "/path1/cookie1.html");
    automationAgent.createCookie("addedCookieForPath1=new value1", "");
    automationAgent
        .createCookie("addedCookieForPath2=new value2", "path=" + base + "/path2/, max_age=60");
    automationAgent.open(base + "/path1/cookie1.html");
    verifyTrue(Pattern.compile("addedCookieForPath1=new value1").matcher(automationAgent.getCookie())
        .find());
    assertTrue(automationAgent.isCookiePresent("addedCookieForPath1"));
    verifyEquals(automationAgent.getCookieByName("addedCookieForPath1"), "new value1");
    verifyFalse(automationAgent.isCookiePresent("testCookie"));
    verifyFalse(automationAgent.isCookiePresent("addedCookieForPath2"));
    automationAgent.deleteCookie("addedCookieForPath1", base + "/path1/");
    verifyEquals(automationAgent.getCookie(), "");
    automationAgent.open(base + "/path2/cookie2.html");
    verifyEquals(automationAgent.getCookieByName("addedCookieForPath2"), "new value2");
    verifyFalse(automationAgent.isCookiePresent("addedCookieForPath1"));
    automationAgent.deleteCookie("addedCookieForPath2", base + "/path2/");
    verifyEquals(automationAgent.getCookie(), "");
    automationAgent.createCookie("testCookieWithSameName=new value1", "path=/");
    automationAgent.createCookie("testCookieWithSameName=new value2", "path=" + base + "/path2/");
    automationAgent.open(base + "/path1/cookie1.html");
    verifyEquals(automationAgent.getCookieByName("testCookieWithSameName"), "new value1");
    automationAgent.open(base + "/path2/cookie2.html");
    verifyTrue(Pattern.compile("testCookieWithSameName=new value1").matcher(automationAgent.getCookie())
        .find());
    verifyTrue(Pattern.compile("testCookieWithSameName=new value2").matcher(automationAgent.getCookie())
        .find());
    automationAgent.deleteCookie("testCookieWithSameName", base + "/path2/");
    automationAgent.open(base + "/path2/cookie2.html");
    verifyEquals(automationAgent.getCookieByName("testCookieWithSameName"), "new value1");
    verifyFalse(Pattern.compile("testCookieWithSameName=new value2").matcher(automationAgent.getCookie())
        .find());
  }
}
