package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Mark

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

  // val ordM: Ordering[Mark[Int]] = summon[Ordering[Mark[Int]]]

  "Equals" when {
    // import IntervalRelAssert.*

    // "a.equals(b)" should {

    //   "b.equals(a)" in {
    //     forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
    //       val xx = Interval.make(argsX.left, argsX.right)
    //       val yy = Interval.make(argsY.left, argsY.right)

    //       val actual   = xx.equalsTo(yy)
    //       val expected = yy.equalsTo(xx)

    //       actual mustBe expected
    //     }
    //   }

    //   "a- = b- AND a+ = b+" in {
    //     forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
    //       val xx = Interval.make(argsX.left, argsX.right)
    //       val yy = Interval.make(argsY.left, argsY.right)

    //       whenever(xx.equalsTo(yy)) {
    //         // a- = b- && a+ = b+
    //         val a1 = argsX.left
    //         val b1 = argsY.left

    //         val a2 = argsX.right
    //         val b2 = argsY.right

    //         val bothEmpty    = xx.isEmpty && yy.isEmpty
    //         val eqBoundaries = (ordM.equiv(a1, b1) && ordM.equiv(a2, b2))

    //         yy.equalsTo(xx) mustBe true
    //         (bothEmpty || eqBoundaries) mustBe true

    //         assertOne(Rel.EqualsTo)(xx, yy)
    //       }
    //     }
    //   }

    //   "valid in special cases" in {
    //     // Empty
    //     Interval.empty[Int].equalsTo(Interval.empty[Int]) mustBe (false)
    //     Interval.empty[Int].equalsTo(Interval.point(0)) mustBe (false)
    //     Interval.empty[Int].equalsTo(Interval.closed(0, 1)) mustBe (false)

    //     // Point
    //     Interval.point(5).equalsTo(Interval.point(5)) mustBe (true)
    //     Interval.point(5).equalsTo(Interval.empty[Int]) mustBe (false)

    //     // Proper
    //     Interval.open(4, 7).equalsTo(Interval.open(4, 7)) mustBe (true)
    //     Interval.open(0, 5).equalsTo(Interval.open(0, 5)) mustBe (true)
    //     Interval.closed(0, 5).equalsTo(Interval.closed(0, 5)) mustBe (true)
    //     Interval.leftOpenRightClosed(0, 5).equalsTo(Interval.leftOpenRightClosed(0, 5)) mustBe (true)
    //     Interval.leftClosedRightOpen(0, 5).equalsTo(Interval.leftClosedRightOpen(0, 5)) mustBe (true)

    //     // Infinity
    //     // [5, +inf)  [5, +inf)
    //     Interval.leftClosed(5).equalsTo(Interval.leftClosed(5)) mustBe (true)

    //     // (-inf, 5]  (-inf, 5]
    //     Interval.rightClosed(5).equalsTo(Interval.rightClosed(5)) mustBe (true)

    //     // (-inf, +inf)  (-inf, +inf)
    //     Interval.unbounded[Int].equalsTo(Interval.unbounded[Int]) mustBe (true)

    //     // (-inf, 5)  (-inf, 5)
    //     Interval.rightOpen(5).equalsTo(Interval.rightOpen(5)) mustBe (true)
    //   }
    // }
  }
