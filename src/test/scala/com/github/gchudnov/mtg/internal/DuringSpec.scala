package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * During, Contains
 *
 * {{{
 *     AA
 *   BBBBBB
 * }}}
 */
final class DuringSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Mark[Int]] = summon[Ordering[Mark[Int]]]

  "During" when {
    import IntervalRelAssert.*

    "a.during(b)" should {
      "b.contains(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.during(yy)) {
            yy.contains(xx) mustBe true

            assertOne(Rel.During)(xx, yy)

            // a- > b- && a+ < b+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            (ordM.gt(a1, b1) && ordM.lt(a2, b2)) mustBe true
          }
        }
      }
    }

    "a.contains(b)" should {
      "b.during(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            yy.during(xx) mustBe true

            assertOne(Rel.Contains)(xx, yy)

            // a- < b- && b+ < a+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            (ordM.lt(a1, b1) && ordM.lt(b2, a2)) mustBe true
          }
        }
      }
    }

    "a.during(b) AND b.contains(a)" should {

      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.during(yy)
          val expected = yy.contains(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].during(Interval.open(5, 10)) mustBe (false)
        Interval.empty[Int].during(Interval.point(0)) mustBe (false)
        Interval.empty[Int].during(Interval.unbounded[Int]) mustBe (false)

        // Point
        // {5}  (2, 9)
        Interval.point(5).during(Interval.open(2, 9)) mustBe (true)
        Interval.point(5).during(Interval.closed(2, 9)) mustBe (true)

        // (2, 9)  {5}
        Interval.open(2, 9).contains(Interval.point(5)) mustBe (true)

        Interval.point(5).during(Interval.closed(5, 10)) mustBe (false)
        Interval.point(5).during(Interval.open(5, 10)) mustBe (false)
        Interval.point(6).during(Interval.open(5, 10)) mustBe (false)
        Interval.point(7).during(Interval.open(5, 10)) mustBe (true)

        // Proper
        Interval.closed(5, 7).during(Interval.closed(2, 10)) mustBe (true)
        Interval.open(5, 8).during(Interval.closed(5, 8)) mustBe (true)

        // Infinity
        // [5, 7]  [3, +inf)
        Interval.closed(5, 7).during(Interval.leftClosed(3)) mustBe (true)

        // [5, 7]  (-inf, 10]
        Interval.closed(5, 7).during(Interval.rightClosed(10)) mustBe (true)

        // [5, 7] (-inf, +inf)
        Interval.closed(5, 7).during(Interval.unbounded[Int]) mustBe (true)

        // (-∞,0]  (-∞,+∞)
        Interval.rightClosed(0).during(Interval.unbounded[Int]) mustBe (false)

        // [0, 2)  (-∞,+∞)
        Interval.closed(0, 2).during(Interval.unbounded[Int]) mustBe (true)

        // [0]  (-∞,+∞)
        Interval.point(0).during(Interval.unbounded[Int]) mustBe (true)

        // (3,10), (0,+∞)
        Interval.open(3, 10).during(Interval.leftOpen(0)) mustBe (true)
        Interval.leftOpen(0).contains(Interval.open(3, 10)) mustBe (true)
      }
    }
  }
