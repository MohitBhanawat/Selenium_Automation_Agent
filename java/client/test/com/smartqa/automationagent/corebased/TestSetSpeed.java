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

@Ignore("Set speed is a no-op, it seems")
public class TestSetSpeed extends InternalSelenseTestBase {
  @Test
  public void testSetSpeed() throws Exception {
    String lastSpeed = automationAgent.getSpeed();
    // The max value in slider is 1000, but setSpeed command can set higher than this
    automationAgent.setSpeed("1600");
    verifyEquals(automationAgent.getSpeed(), "1600");
    automationAgent.setSpeed("500");
    verifyEquals(automationAgent.getSpeed(), "500");
    // Negative value should be treated as 0
    automationAgent.setSpeed("0");
    verifyEquals(automationAgent.getSpeed(), "0");
    automationAgent.setSpeed("-100");
    verifyEquals(automationAgent.getSpeed(), "0");
    automationAgent.setSpeed(lastSpeed);
    Thread.sleep(100);
  }
}
