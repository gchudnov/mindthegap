package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class StaticIntersectSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "StaticIntersect" when {
    "intersection" should {
      "calculate it" in {
        val a = Interval.rightClosed(2) // (-∞, 2]
        val b = Interval.closed(1, 10)  // [1, 10]

        val expected = Interval.closed(1, 2) // [1, 2]

        val c1 = Interval.intersection(a, b).canonical
        val c2 = Interval.intersection(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }

    "span" should {
      "calculate it" in {
        val a = Interval.closed(1, 5)  // [1, 5]
        val b = Interval.closed(7, 10) // [7, 10]

        val expected = Interval.closed(1, 10) // [1, 10]

        val c1 = Interval.span(a, b).canonical
        val c2 = Interval.span(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }

    "union" should {
      "calculate it" in {
        val a = Interval.closed(1, 5)  // [1, 5]
        val b = Interval.closed(3, 10) // [7, 10]

        val expected = Interval.closed(1, 10) // [1, 10]

        val c1 = Interval.union(a, b).canonical
        val c2 = Interval.union(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }

    "gap" should {
      "calculate it" in {
        val a = Interval.closed(1, 5)  // [1, 5]
        val b = Interval.closed(7, 10) // [7, 10]

        val expected = Interval.closed(5, 7) // [5, 7]

        val c1 = Interval.gap(a, b).canonical
        val c2 = Interval.gap(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }
  }
