package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.Domain
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
final class EqualsToSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "EqualsTo" when {
    import IntervalRelAssert.*

    "a.equalsTo(b)" should {

      "b.equalsTo(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.equalsTo(yy)
          val expected = yy.equalsTo(xx)

          // aliasing
          Interval.equalsTo(xx, yy) shouldBe expected
          Interval.equalsTo(yy, xx) shouldBe actual

          actual shouldBe expected
        }
      }

      "a- = b- AND a+ = b+" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.equalsTo(yy)) {
            // a- = b- && a+ = b+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            val eqBoundaries = (ordE.equiv(a1, b1) && ordE.equiv(a2, b2))

            yy.equalsTo(xx) shouldBe true
            eqBoundaries shouldBe true

            assertOne(Rel.EqualsTo)(xx, yy)
          }
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].equalsTo(Interval.empty[Int]) shouldBe (true)
        Interval.empty[Int].equalsTo(Interval.point(0)) shouldBe (false)
        Interval.empty[Int].equalsTo(Interval.closed(0, 1)) shouldBe (false)

        // Point
        Interval.point(5).equalsTo(Interval.point(5)) shouldBe (true)
        Interval.point(5).equalsTo(Interval.empty[Int]) shouldBe (false)

        // Proper
        Interval.open(4, 7).equalsTo(Interval.open(4, 7)) shouldBe (true)
        Interval.open(0, 5).equalsTo(Interval.open(0, 5)) shouldBe (true)
        Interval.closed(0, 5).equalsTo(Interval.closed(0, 5)) shouldBe (true)
        Interval.leftOpenRightClosed(0, 5).equalsTo(Interval.leftOpenRightClosed(0, 5)) shouldBe (true)
        Interval.leftClosedRightOpen(0, 5).equalsTo(Interval.leftClosedRightOpen(0, 5)) shouldBe (true)

        // Infinity
        // [5, +inf)  [5, +inf)
        Interval.leftClosed(5).equalsTo(Interval.leftClosed(5)) shouldBe (true)

        // (-inf, 5]  (-inf, 5]
        Interval.rightClosed(5).equalsTo(Interval.rightClosed(5)) shouldBe (true)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].equalsTo(Interval.unbounded[Int]) shouldBe (true)

        // (-inf, 5)  (-inf, 5)
        Interval.rightOpen(5).equalsTo(Interval.rightOpen(5)) shouldBe (true)

        // [doc]
        Interval.closed(1, 5).equalsTo(Interval.closed(1, 5)) shouldBe (true)
      }
    }
  }
