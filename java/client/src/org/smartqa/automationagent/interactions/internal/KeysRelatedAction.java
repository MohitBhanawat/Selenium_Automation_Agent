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

package org.smartqa.automationagent.interactions.internal;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.smartqa.automationagent.WebElement;
import org.smartqa.automationagent.interactions.Interaction;
import org.smartqa.automationagent.interactions.IsInteraction;
import org.smartqa.automationagent.interactions.Keyboard;
import org.smartqa.automationagent.interactions.Locatable;
import org.smartqa.automationagent.interactions.Mouse;
import org.smartqa.automationagent.interactions.PointerInput;
import org.smartqa.automationagent.interactions.PointerInput.MouseButton;
import org.smartqa.automationagent.interactions.PointerInput.Origin;

/**
 * Represents a general action related to keyboard input.
 */
@Deprecated
public abstract class KeysRelatedAction extends BaseAction implements IsInteraction {
  protected final Keyboard keyboard;
  protected final Mouse mouse;

  protected KeysRelatedAction(Keyboard keyboard, Mouse mouse, Locatable locationProvider) {
    super(locationProvider);
    this.keyboard = keyboard;
    this.mouse = mouse;
  }

  protected void focusOnElement() {
    if (where != null) {
      mouse.click(where.getCoordinates());
    }
  }

  protected Collection<Interaction> optionallyClickElement(PointerInput mouse) {
    List<Interaction> interactions = new ArrayList<>();

    Optional<WebElement> target = getTargetElement();
    if (target.isPresent()) {

      interactions.add(mouse.createPointerMove(
          Duration.ofMillis(500),
          target.map(Origin::fromElement).orElse(Origin.pointer()),
          0,
          0));

      interactions.add(mouse.createPointerDown(MouseButton.LEFT.asArg()));
      interactions.add(mouse.createPointerUp(MouseButton.LEFT.asArg()));
    }

    return Collections.unmodifiableList(interactions);
  }
}
