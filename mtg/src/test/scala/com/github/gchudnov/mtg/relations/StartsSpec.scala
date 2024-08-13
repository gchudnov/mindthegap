package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Starts, IsStartedBy
 *
 * {{{
 *   AAA
 *   BBBBBB
 * }}}
 */
final class StartsSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "Starts" when {
    import IntervalRelAssert.*

    "a.starts(b)" should {
      "b.isStartedBy(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.starts(yy)) {
            yy.isStartedBy(xx) shouldBe true

            assertOne(Rel.Starts)(xx, yy)

            // a- = b- && b.isSuperset(a) && !a.equalsTo(b)
            val a1 = argsX.left
            val b1 = argsY.left

            (ordE.equiv(a1, b1) && yy.isSuperset(xx) && !xx.equalsTo(yy)) shouldBe true
          }
        }
      }
    }

    "a.isStartedBy(b)" should {
      "b.starts(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            yy.starts(xx) shouldBe true

            assertOne(Rel.IsStartedBy)(xx, yy)

            // a- = b- && a.isSuperset(b) && !a.equalsTo(b)
            val a1 = argsX.left
            val b1 = argsY.left

            (ordE.equiv(a1, b1) && xx.isSuperset(yy) && !xx.equalsTo(yy)) shouldBe true
          }
        }
      }
    }

    "a.starts(b) AND b.isStartedBy(a)" should {
      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.starts(yy)
          val expected = yy.isStartedBy(xx)

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].starts(Interval.point(0)) shouldBe (false)
        Interval.empty[Int].starts(Interval.closed(0, 1)) shouldBe (false)
        Interval.empty[Int].starts(Interval.unbounded[Int]) shouldBe (false)

        // Point
        Interval.point(5).starts(Interval.closed(5, 10)) shouldBe (true)
        Interval.point(5).starts(Interval.open(4, 10)) shouldBe (true)
        Interval.point(5).starts(Interval.open(5, 10)) shouldBe (false)
        Interval.point(5).starts(Interval.empty[Int]) shouldBe (false)
        Interval.point(5).starts(Interval.unbounded[Int]) shouldBe (false)

        // Proper
        // [1, 2]  [1, 10]
        Interval.closed(1, 2).starts(Interval.closed(1, 10)) shouldBe (true)
        Interval.closed(1, 10).isStartedBy(Interval.closed(1, 2)) shouldBe (true)

        // (1, 3)  (1, 10)
        Interval.open(1, 4).starts(Interval.open(1, 10)) shouldBe (true)

        // Infinity
        // [1, 5] [1, +inf)
        Interval.closed(1, 5).starts(Interval.leftClosed(1)) shouldBe (true)

        // (-inf, 5]  (-inf, 10]
        Interval.rightClosed(5).starts(Interval.rightClosed(10)) shouldBe (true)

        // (-inf, 5)  (-inf, +inf)
        Interval.rightClosed(5).starts(Interval.unbounded[Int]) shouldBe (true)

        //  [5, 10)  [5, +inf)
        Interval.leftClosedRightOpen(5, 10).starts(Interval.leftClosed(5)) shouldBe (true)
        Interval.leftClosedRightOpen(5, 10).isStartedBy(Interval.leftClosed(5)) shouldBe (false)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].starts(Interval.unbounded[Int]) shouldBe (false)
        Interval.unbounded[Int].isStartedBy(Interval.unbounded[Int]) shouldBe (false)

        // (0,2]  (0,+∞]
        Interval.leftOpenRightClosed(0, 2).starts(Interval.leftOpen(0)) shouldBe (true)

        // [doc]
        Interval.closed(1, 4).starts(Interval.closed(1, 6)) shouldBe (true)
        Interval.closed(1, 6).isStartedBy(Interval.closed(1, 4)) shouldBe (true)
      }
    }
  }
