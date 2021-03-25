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
package org.dominokit.domino.api.shared.extension;

public class TestAggregate {

  private TestComplete testComplete;

  public TestAggregate() {
    TestComplete testComplete = new TestComplete().init(this);
    testComplete.completeParam1("some string");
    testComplete.completeParam2(10);
    testComplete.completeParam3(10.0);
  }

  public static void main(String[] args) {
    TestAggregate testAggregate = new TestAggregate();
  }

  @Aggregate(name = "TestComplete")
  public void onCompleteAll(String param1, Integer param2, Double param3) {
    System.out.println(param1);
    System.out.println(param2);
    System.out.println(param3);
  }
}
