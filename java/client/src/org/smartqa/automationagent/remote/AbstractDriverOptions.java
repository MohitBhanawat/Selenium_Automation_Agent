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

package org.smartqa.automationagent.remote;

import static org.smartqa.automationagent.remote.CapabilityType.ACCEPT_INSECURE_CERTS;
import static org.smartqa.automationagent.remote.CapabilityType.PAGE_LOAD_STRATEGY;
import static org.smartqa.automationagent.remote.CapabilityType.PROXY;
import static org.smartqa.automationagent.remote.CapabilityType.STRICT_FILE_INTERACTABILITY;
import static org.smartqa.automationagent.remote.CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR;
import static org.smartqa.automationagent.remote.CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR;

import org.smartqa.automationagent.MutableCapabilities;
import org.smartqa.automationagent.PageLoadStrategy;
import org.smartqa.automationagent.Proxy;
import org.smartqa.automationagent.UnexpectedAlertBehaviour;
import org.smartqa.automationagent.internal.Require;

public class AbstractDriverOptions<DO extends AbstractDriverOptions> extends MutableCapabilities {

  public DO setPageLoadStrategy(PageLoadStrategy strategy) {
    setCapability(
        PAGE_LOAD_STRATEGY,
        Require.nonNull("Page load strategy", strategy));
    return (DO) this;
  }

  public DO setUnhandledPromptBehaviour(UnexpectedAlertBehaviour behaviour) {
    setCapability(
        UNHANDLED_PROMPT_BEHAVIOUR,
        Require.nonNull("Unhandled prompt behavior", behaviour));
    setCapability(UNEXPECTED_ALERT_BEHAVIOUR, behaviour);
    return (DO) this;
  }

  public DO setAcceptInsecureCerts(boolean acceptInsecureCerts) {
    setCapability(ACCEPT_INSECURE_CERTS, acceptInsecureCerts);
    return (DO) this;
  }

  public DO setStrictFileInteractability(boolean strictFileInteractability) {
    setCapability(STRICT_FILE_INTERACTABILITY, strictFileInteractability);
    return (DO) this;
  }

  public DO setProxy(Proxy proxy) {
    setCapability(PROXY, Require.nonNull("Proxy", proxy));
    return (DO) this;
  }

}
