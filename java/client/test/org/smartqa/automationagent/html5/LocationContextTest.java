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

package org.smartqa.automationagent.html5;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.junit.Assume.assumeTrue;
import static org.smartqa.automationagent.testing.drivers.Browser.CHROME;
import static org.smartqa.automationagent.testing.drivers.Browser.CHROMIUMEDGE;

import org.junit.Before;
import org.junit.Test;
import org.smartqa.automationagent.html5.Location;
import org.smartqa.automationagent.html5.LocationContext;
import org.smartqa.automationagent.testing.JUnit4TestBase;
import org.smartqa.automationagent.testing.NotYetImplemented;

public class LocationContextTest extends JUnit4TestBase {

  @Before
  public void hasLocationContext() {
    assumeTrue(driver instanceof LocationContext);
  }

  @Test
  @NotYetImplemented(CHROME)
  @NotYetImplemented(CHROMIUMEDGE)
  public void testShouldSetAndGetLatitude() {
    driver.get(pages.html5Page);

    ((LocationContext) driver).setLocation(new Location(40.714353, -74.005973, 0.056747));
    Location location = ((LocationContext) driver).location();
    assertThat(location).isNotNull();
    assertThat(location.getLatitude()).isCloseTo(40.714353, byLessThan(0.000001));
  }

  @Test
  @NotYetImplemented(CHROME)
  @NotYetImplemented(CHROMIUMEDGE)
  public void testShouldSetAndGetLongitude() {
    driver.get(pages.html5Page);

    ((LocationContext) driver).setLocation(new Location(40.714353, -74.005973, 0.056747));
    Location location = ((LocationContext) driver).location();
    assertThat(location).isNotNull();
    assertThat(location.getLongitude()).isCloseTo(-74.005973, byLessThan(0.000001));
  }

  @Test
  @NotYetImplemented(CHROME)
  @NotYetImplemented(CHROMIUMEDGE)
  public void testShouldSetAndGetAltitude() {
    driver.get(pages.html5Page);

    ((LocationContext) driver).setLocation(new Location(40.714353, -74.005973, 0.056747));
    Location location = ((LocationContext) driver).location();
    assertThat(location).isNotNull();
    assertThat(location.getAltitude()).isCloseTo(0.056747, byLessThan(0.000001));
  }
}
