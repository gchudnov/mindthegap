package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Boundary

/**
 * Equals
 *
 * {{{
 *   AAAA
 *   BBBB
 * }}}
 */
final class EqualsSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordB: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "Equals" when {
    import IntervalRelAssert.*

    "a.equals(b)" should {

      "b.equals(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          val actual   = xx.equalsTo(yy)
          val expected = yy.equalsTo(xx)

          actual mustBe expected
        }
      }

      "a- = b- AND a+ = b+" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.equalsTo(yy)) {
            // a- = b- && a+ = b+
            val a1 = Boundary.Left(ox1, ix1)
            val b1 = Boundary.Left(oy1, iy1)

            val a2 = Boundary.Right(ox2, ix2)
            val b2 = Boundary.Right(oy2, iy2)

            val bothEmpty    = xx.isEmpty && yy.isEmpty
            val eqBoundaries = (ordB.equiv(a1, b1) && ordB.equiv(a2, b2))

            yy.equalsTo(xx) mustBe true
            (bothEmpty || eqBoundaries) mustBe true

            assertOne(Rel.EqualsTo)(xx, yy)
          }
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].equalsTo(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].equalsTo(Interval.point(0)) mustBe (false)
        Interval.empty[Int].equalsTo(Interval.closed(0, 1)) mustBe (false)

        // Point
        Interval.point(5).equalsTo(Interval.point(5)) mustBe (true)
        Interval.point(5).equalsTo(Interval.empty[Int]) mustBe (false)

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
