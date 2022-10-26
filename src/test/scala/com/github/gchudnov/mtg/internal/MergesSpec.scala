package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Merges, IsMergedBy
 */
final class MergesSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Merges" when {
    "merges & isMergedBy" should {
      "manual check" in {
        // Empty
        Interval.empty[Int].merges(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].merges(Interval.point(1)) mustBe (false)
        Interval.empty[Int].merges(Interval.closed(1, 4)) mustBe (false)
        Interval.empty[Int].merges(Interval.open(1, 4)) mustBe (false)
        Interval.empty[Int].merges(Interval.unbounded[Int]) mustBe (false)

        // Point
        Interval.point(5).merges(Interval.empty[Int]) mustBe (false)
        Interval.point(5).merges(Interval.point(5)) mustBe (true)
        Interval.point(5).merges(Interval.point(6)) mustBe (true)
        Interval.point(5).merges(Interval.open(5, 10)) mustBe (true)

        // Proper
        Interval.open(4, 10).merges(Interval.open(5, 12)) mustBe (true)
        Interval.open(5, 12).isMergedBy(Interval.open(4, 10)) mustBe (true)
        Interval.open(4, 7).merges(Interval.open(5, 8)) mustBe (true)
      }
    }
  }
