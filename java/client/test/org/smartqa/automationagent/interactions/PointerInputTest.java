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

package org.smartqa.automationagent.interactions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smartqa.automationagent.remote.Dialect.W3C;

import org.junit.Test;
import org.smartqa.automationagent.WebElement;
import org.smartqa.automationagent.WrappedWebElement;
import org.smartqa.automationagent.interactions.Interaction;
import org.smartqa.automationagent.interactions.PointerInput;
import org.smartqa.automationagent.interactions.Sequence;
import org.smartqa.automationagent.interactions.PointerInput.Kind;
import org.smartqa.automationagent.interactions.PointerInput.Origin;
import org.smartqa.automationagent.json.Json;
import org.smartqa.automationagent.json.PropertySetting;
import org.smartqa.automationagent.remote.RemoteWebElement;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Unit test for PointerInputs.
 */
public class PointerInputTest {

  @Test
  public void encodesWrappedElementInMoveOrigin() {
    RemoteWebElement innerElement = new RemoteWebElement();
    innerElement.setId("12345");
    WebElement element = new WrappedWebElement(innerElement);

    PointerInput pointerInput = new PointerInput(Kind.MOUSE, null);
    Interaction move = pointerInput.createPointerMove(
        Duration.ofMillis(100), Origin.fromElement(element), 0, 0);
    Sequence sequence = new Sequence(move.getSource(), 0).addAction(move);

    String rawJson = new Json().toJson(sequence);
    ActionSequenceJson json = new Json().toType(
        rawJson,
        ActionSequenceJson.class,
        PropertySetting.BY_FIELD);

    assertThat(json.actions).hasSize(1);
    ActionJson firstAction = json.actions.get(0);
    assertThat(firstAction.origin).containsEntry(W3C.getEncodedElementKey(), "12345");
  }

  private static class ActionSequenceJson {
    public List<ActionJson> actions;
  }

  private static class ActionJson {
    public Map<String, String> origin;
  }
}
