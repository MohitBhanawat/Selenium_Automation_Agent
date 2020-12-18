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

package org.smartqa.automationagent.remote.server.handler.interactions.touch;

import org.smartqa.automationagent.WebElement;
import org.smartqa.automationagent.interactions.Coordinates;
import org.smartqa.automationagent.interactions.HasTouchScreen;
import org.smartqa.automationagent.interactions.Locatable;
import org.smartqa.automationagent.interactions.TouchScreen;
import org.smartqa.automationagent.remote.server.Session;
import org.smartqa.automationagent.remote.server.handler.WebElementHandler;

import java.util.Map;

public class SingleTapOnElement extends WebElementHandler<Void> {

  private static final String ELEMENT = "element";
  private String elementId;

  public SingleTapOnElement(Session session) {
    super(session);
  }

  @Override
  public void setJsonParameters(Map<String, Object> allParameters) throws Exception {
    super.setJsonParameters(allParameters);
    if (allParameters.containsKey(ELEMENT) && allParameters.get(ELEMENT) != null) {
      elementId = (String) allParameters.get(ELEMENT);
    }
  }

  @Override
  public Void call() {
    TouchScreen touchScreen = ((HasTouchScreen) getDriver()).getTouch();
    WebElement element = getKnownElements().get(elementId);
    Coordinates elementLocation = ((Locatable) element).getCoordinates();

    touchScreen.singleTap(elementLocation);

    return null;
  }

  @Override
  public String toString() {
    return String.format("[singleTap: %s]", elementId);
  }

}
