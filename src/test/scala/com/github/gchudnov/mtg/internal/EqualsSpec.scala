package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Equals
 *
 * {{{
 *   AAAA
 *   BBBB
 * }}}
 */
final class EqualsSpec extends TestSpec: // with IntervalRelAssert {}

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Equals" when {
    "equals" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.equalsTo(yy)) {
            assertOne(Rel.EqualsTo)(xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].equalsTo(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].equalsTo(Interval.degenerate(0)) mustBe (false)
        Interval.empty[Int].equalsTo(Interval.closed(0, 1)) mustBe (false)

        // Degenerate
        Interval.degenerate(5).equalsTo(Interval.degenerate(5)) mustBe (true)
        Interval.degenerate(5).equalsTo(Interval.empty[Int]) mustBe (false)

        // Proper
        Interval.open(4, 7).equalsTo(Interval.open(4, 7)) mustBe (true)
        Interval.open(0, 5).equalsTo(Interval.open(0, 5)) mustBe (true)
        Interval.closed(0, 5).equalsTo(Interval.closed(0, 5)) mustBe (true)
        Interval.leftOpenRightClosed(0, 5).equalsTo(Interval.leftOpenRightClosed(0, 5)) mustBe (true)
        Interval.leftClosedRightOpen(0, 5).equalsTo(Interval.leftClosedRightOpen(0, 5)) mustBe (true)

        // Infinity
        // [5, +inf)  [5, +inf)
        Interval.leftClosed(5).equalsTo(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5]  (-inf, 5]
        Interval.rightClosed(5).equalsTo(Interval.rightClosed(5)) mustBe (true)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].equalsTo(Interval.unbounded[Int]) mustBe (true)

        // (-inf, 5)  (-inf, 5)
        Interval.rightOpen(5).equalsTo(Interval.rightOpen(5)) mustBe (true)
      }
    }
  }
