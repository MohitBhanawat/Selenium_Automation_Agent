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

package org.smartqa.automationagent.remote.html5;

import static org.smartqa.automationagent.remote.CapabilityType.SUPPORTS_APPLICATION_CACHE;

import java.util.function.Predicate;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.html5.ApplicationCache;
import org.smartqa.automationagent.remote.AugmenterProvider;
import org.smartqa.automationagent.remote.ExecuteMethod;

public class AddApplicationCache implements AugmenterProvider<ApplicationCache> {

  @Override
  public Predicate<Capabilities> isApplicable() {
    return caps -> caps.is(SUPPORTS_APPLICATION_CACHE);
  }

  @Override
  public Class<ApplicationCache> getDescribedInterface() {
    return ApplicationCache.class;
  }

  @Override
  public ApplicationCache getImplementation(Capabilities capabilities, ExecuteMethod executeMethod) {
    return new RemoteApplicationCache(executeMethod);
  }
}
