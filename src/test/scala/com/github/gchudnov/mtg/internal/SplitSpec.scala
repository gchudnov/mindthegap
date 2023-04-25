package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import org.scalacheck.Gen

final class SplitSpec extends TestSpec:

  given intRange: IntRange = intRange100
  given intProb: IntProb   = intProb028

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Split" when {
    "a series of intervals are split" should {
      "produce only disjoint intervals" in {
        // TODO: impl
      }
    }

    "no intervals" should {
      "be split" in {
        val input = List.empty[Interval[Int]]

        val actual   = Interval.split(input)
        val expected = List.empty[Interval[Int]]

        val actualInfo   = Interval.splitFind(input)
        val expectedInfo = List.empty[(Interval[Int], Set[Int])]

        actual mustBe expected
        actualInfo mustBe expectedInfo
      }
    }
  }
