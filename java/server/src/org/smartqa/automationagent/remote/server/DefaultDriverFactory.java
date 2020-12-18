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

package org.smartqa.automationagent.remote.server;

import static org.smartqa.automationagent.Platform.ANY;
import static org.smartqa.automationagent.Platform.MAC;
import static org.smartqa.automationagent.Platform.WINDOWS;
import static org.smartqa.automationagent.remote.BrowserType.CHROME;
import static org.smartqa.automationagent.remote.BrowserType.EDGE;
import static org.smartqa.automationagent.remote.BrowserType.FIREFOX;
import static org.smartqa.automationagent.remote.BrowserType.HTMLUNIT;
import static org.smartqa.automationagent.remote.BrowserType.IE;
import static org.smartqa.automationagent.remote.BrowserType.OPERA;
import static org.smartqa.automationagent.remote.BrowserType.OPERA_BLINK;
import static org.smartqa.automationagent.remote.BrowserType.PHANTOMJS;
import static org.smartqa.automationagent.remote.BrowserType.SAFARI;
import static org.smartqa.automationagent.remote.CapabilityType.BROWSER_NAME;
import static org.smartqa.automationagent.remote.CapabilityType.PLATFORM_NAME;
import static org.smartqa.automationagent.remote.server.DefaultDriverProvider.createProvider;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.smartqa.automationagent.Capabilities;
import org.smartqa.automationagent.ImmutableCapabilities;
import org.smartqa.automationagent.Platform;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebDriverException;
import org.smartqa.automationagent.internal.Require;

public class DefaultDriverFactory implements DriverFactory {

  private static final Logger LOG = Logger.getLogger(DefaultDriverFactory.class.getName());

  private static final List<DriverProvider> DEFAULT_DRIVER_PROVIDERS =
      Stream.concat(
        new ImmutableMap.Builder<String, String>()
            .put(FIREFOX, "org.smartqa.automationagent.firefox.FirefoxDriver")
            .put(CHROME, "org.smartqa.automationagent.chrome.ChromeDriver")
            .put(OPERA, "com.opera.core.systems.OperaDriver")
            .put(OPERA_BLINK, "org.smartqa.automationagent.opera.OperaDriver")
            .put(PHANTOMJS, "org.smartqa.automationagent.phantomjs.PhantomJSDriver")
            .put(HTMLUNIT, "org.smartqa.automationagent.htmlunit.HtmlUnitDriver")
            .build().entrySet().stream()
            .map(e -> createProvider(new ImmutableCapabilities(BROWSER_NAME, e.getKey()), e.getValue()))
            .filter(Objects::nonNull),
        Stream.of(
            createProvider(new ImmutableCapabilities(BROWSER_NAME, IE, PLATFORM_NAME, WINDOWS),
                           "org.smartqa.automationagent.ie.InternetExplorerDriver"),
            createProvider(new ImmutableCapabilities(BROWSER_NAME, EDGE),
                           "org.smartqa.automationagent.edge.EdgeDriver"),
            createProvider(new ImmutableCapabilities(BROWSER_NAME, SAFARI, PLATFORM_NAME, MAC),
                           "org.smartqa.automationagent.safari.SafariDriver"))
        ).collect(ImmutableList.toImmutableList());

  private final Map<Capabilities, DriverProvider> capabilitiesToDriverProvider =
      new ConcurrentHashMap<>();

  public DefaultDriverFactory(Platform runningOn) {
    registerDefaults(runningOn);
    registerServiceLoaders(runningOn);
  }

  @Override
  public void registerDriverProvider(DriverProvider driverProvider) {
    capabilitiesToDriverProvider.put(driverProvider.getProvidedCapabilities(), driverProvider);
  }

  @VisibleForTesting
  DriverProvider getProviderMatching(Capabilities desired) {
    // We won't be able to make a match if no drivers have been registered.
    Require.stateCondition(!capabilitiesToDriverProvider.isEmpty(),
                           "No drivers have been registered, will be unable to match %s", desired);
    Capabilities bestMatchingCapabilities =
        CapabilitiesComparator.getBestMatch(desired, capabilitiesToDriverProvider.keySet());
    return capabilitiesToDriverProvider.get(bestMatchingCapabilities);
  }

  @Override
  public WebDriver newInstance(Capabilities capabilities) {
    DriverProvider provider = getProviderMatching(capabilities);
    if (provider.canCreateDriverInstanceFor(capabilities)) {
      return provider.newInstance(capabilities);
    }
    throw new WebDriverException(String.format(
      "The best matching driver provider %s can't create a new driver instance for %s",
      provider, capabilities));
  }

  private void registerDefaults(Platform current) {
    for (DriverProvider provider : DEFAULT_DRIVER_PROVIDERS) {
      registerDriverProvider(current, provider);
    }
  }

  private void registerServiceLoaders(Platform current) {
    for (DriverProvider provider : ServiceLoader.load(DriverProvider.class)) {
      registerDriverProvider(current, provider);
    }
  }

  private void registerDriverProvider(Platform current, DriverProvider provider) {
    Capabilities caps = provider.getProvidedCapabilities();
    if (!platformMatches(current, caps)) {
      LOG.info(String.format(
          "Driver provider %s registration is skipped:%n" +
          " registration capabilities %s does not match the current platform %s",
          provider, caps, current));
      return;
    }

    registerDriverProvider(provider);
  }

  private boolean platformMatches(Platform current, Capabilities caps) {
    return caps.getPlatform() == null
           || caps.getPlatform() == ANY
           || current.is(caps.getPlatform());
  }
}
