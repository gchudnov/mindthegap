package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class GroupSpec extends TestSpec:

  "Group" when {
    "a series of intervals are grouped" should {
      "have only disjoint intervals" in {

      }
    }

    "a prefefined intervals to group are provided" should {
      "produce the expected groups" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)

        val d = Interval.closed(60, 70)
        val e = Interval.closed(71, 80)

        // val actual = Interval.group(List(a, b, c, d, e))
        // val expected = List(Interval.closed(0, 50), Interval.closed(60, 80))

        // actual mustBe expected
      }
    }
  }
