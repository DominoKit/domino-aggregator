/*
 * Copyright © ${year} ${name}
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dominokit.domino.apt.client.processors.aggregate;

import org.dominokit.domino.api.shared.extension.Aggregate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AggregateTest {

  private boolean called = false;

  private EventsAggregate eventsAggregate;
  private String event1;
  private Long event2;

  @Before
  public void setup() {
    eventsAggregate = new EventsAggregate(this);
  }

  @Aggregate(name = "EventsAggregate")
  public void onAllEventCompleted(String event1, Long event2) {
    this.called = true;
    this.event1 = event1;
    this.event2 = event2;
  }

  @Test
  public void shouldCallAggregateMethod() {
    eventsAggregate.completeEvent1("some String");
    eventsAggregate.completeEvent2(5L);
    Assert.assertTrue(called);
    Assert.assertEquals(event1, "some String");
    Assert.assertTrue(event2.equals(5L));
  }
}
