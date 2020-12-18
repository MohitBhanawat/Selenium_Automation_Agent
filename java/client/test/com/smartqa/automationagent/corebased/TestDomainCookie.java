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

public class TestDomainCookie extends InternalSelenseTestBase {
  @Test
  public void testDomainCookie() {
    String host =
        automationAgent
            .getEval("parseUrl(canonicalize(absolutify(\"html\", automationagent.browserbot.baseUrl))).host;");

    if (!automationAgent.getExpression(host).matches("^[\\s\\S]*\\.[\\s\\S]*\\.[\\s\\S]*$")) {
      System.out.println("Skipping test: hostname too short: " + host);
      return;
    }

    assertTrue(automationAgent.getExpression(host).matches("^[\\s\\S]*\\.[\\s\\S]*\\.[\\s\\S]*$"));
    String domain =
        automationAgent
            .getEval("var host = parseUrl(canonicalize(absolutify(\"html\", automationagent.browserbot.baseUrl))).host; host.replace(/^[^\\.]*/, \"\");");
    String base =
        automationAgent
            .getEval("parseUrl(canonicalize(absolutify(\"html\", automationagent.browserbot.baseUrl))).pathname;");
    automationAgent.open(base + "/path1/cookie1.html");
    automationAgent.deleteCookie("testCookieWithSameName", "path=/");
    automationAgent.deleteCookie("addedCookieForPath1", "path=" + base + "/path1/");
    automationAgent.deleteCookie("domainCookie", "domain=" + domain + "; path=/");
    assertEquals(automationAgent.getCookie(), "");
    automationAgent.open(base + "/path1/cookie1.html");
    automationAgent.createCookie("domainCookie=domain value", "domain=" + domain + "; path=/");
    assertEquals(automationAgent.getCookieByName("domainCookie"), "domain value");
    automationAgent.deleteCookie("domainCookie", "domain=" + domain + "; path=/");
    assertFalse(automationAgent.isCookiePresent("domainCookie"));
    assertEquals(automationAgent.getCookie(), "");
  }
}
