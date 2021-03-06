/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.transforms;

import static org.apache.beam.sdk.transforms.display.DisplayDataMatchers.hasDisplayItem;

import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.RunnableOnService;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.values.PCollection;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.Serializable;

/**
 * Tests for {@link Filter}.
 */
@RunWith(JUnit4.class)
public class FilterTest implements Serializable {

  static class TrivialFn implements SerializableFunction<Integer, Boolean> {
    private final Boolean returnVal;

    TrivialFn(Boolean returnVal) {
      this.returnVal = returnVal;
    }

    @Override
    public Boolean apply(Integer elem) {
      return this.returnVal;
    }
  }

  static class EvenFn implements SerializableFunction<Integer, Boolean> {
    @Override
    public Boolean apply(Integer elem) {
      return elem % 2 == 0;
    }
  }

  @Deprecated
  @Test
  @Category(RunnableOnService.class)
  public void testIdentityFilterBy() {
    TestPipeline p = TestPipeline.create();

    PCollection<Integer> output = p
        .apply(Create.of(591, 11789, 1257, 24578, 24799, 307))
        .apply(Filter.by(new TrivialFn(true)));

    PAssert.that(output).containsInAnyOrder(591, 11789, 1257, 24578, 24799, 307);
    p.run();
  }

  @Deprecated
  @Test
  @Category(NeedsRunner.class)
  public void testNoFilter() {
    TestPipeline p = TestPipeline.create();

    PCollection<Integer> output = p
        .apply(Create.of(1, 2, 4, 5))
        .apply(Filter.by(new TrivialFn(false)));

    PAssert.that(output).empty();
    p.run();
  }

  @Deprecated
  @Test
  @Category(RunnableOnService.class)
  public void testFilterBy() {
    TestPipeline p = TestPipeline.create();

    PCollection<Integer> output = p
        .apply(Create.of(1, 2, 3, 4, 5, 6, 7))
        .apply(Filter.by(new EvenFn()));

    PAssert.that(output).containsInAnyOrder(2, 4, 6);
    p.run();
  }

  @Test
  @Category(RunnableOnService.class)
  public void testIdentityFilterByPredicate() {
    TestPipeline p = TestPipeline.create();

    PCollection<Integer> output = p
        .apply(Create.of(591, 11789, 1257, 24578, 24799, 307))
        .apply(Filter.byPredicate(new TrivialFn(true)));

    PAssert.that(output).containsInAnyOrder(591, 11789, 1257, 24578, 24799, 307);
    p.run();
  }

  @Test
  @Category(RunnableOnService.class)
  public void testNoFilterByPredicate() {
    TestPipeline p = TestPipeline.create();

    PCollection<Integer> output = p
        .apply(Create.of(1, 2, 4, 5))
        .apply(Filter.byPredicate(new TrivialFn(false)));

    PAssert.that(output).empty();
    p.run();
  }

  @Test
  @Category(RunnableOnService.class)
  public void testFilterByPredicate() {
    TestPipeline p = TestPipeline.create();

    PCollection<Integer> output = p
        .apply(Create.of(1, 2, 3, 4, 5, 6, 7))
        .apply(Filter.byPredicate(new EvenFn()));

    PAssert.that(output).containsInAnyOrder(2, 4, 6);
    p.run();
  }

  @Test
  @Category(RunnableOnService.class)
  public void testFilterLessThan() {
    TestPipeline p = TestPipeline.create();

    PCollection<Integer> output = p
        .apply(Create.of(1, 2, 3, 4, 5, 6, 7))
        .apply(Filter.lessThan(4));

    PAssert.that(output).containsInAnyOrder(1, 2, 3);
    p.run();
  }

  @Test
  @Category(RunnableOnService.class)
  public void testFilterGreaterThan() {
    TestPipeline p = TestPipeline.create();

    PCollection<Integer> output = p
        .apply(Create.of(1, 2, 3, 4, 5, 6, 7))
        .apply(Filter.greaterThan(4));

    PAssert.that(output).containsInAnyOrder(5, 6, 7);
    p.run();
  }

  @Test
  public void testDisplayData() {
    ParDo.Bound<Integer, Integer> lessThan = Filter.lessThan(123);
    assertThat(DisplayData.from(lessThan), hasDisplayItem("predicate", "x < 123"));

    ParDo.Bound<Integer, Integer> lessThanOrEqual = Filter.lessThanEq(234);
    assertThat(DisplayData.from(lessThanOrEqual), hasDisplayItem("predicate", "x ≤ 234"));

    ParDo.Bound<Integer, Integer> greaterThan = Filter.greaterThan(345);
    assertThat(DisplayData.from(greaterThan), hasDisplayItem("predicate", "x > 345"));

    ParDo.Bound<Integer, Integer> greaterThanOrEqual = Filter.greaterThanEq(456);
    assertThat(DisplayData.from(greaterThanOrEqual), hasDisplayItem("predicate", "x ≥ 456"));
  }
}
