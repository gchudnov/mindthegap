package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Domain
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

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "During" when {
    import IntervalRelAssert.*

    "a.during(b)" should {
      "b.contains(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.during(yy)) {
            yy.contains(xx) shouldBe true

            assertOne(Rel.During)(xx, yy)

            // a- > b- && a+ < b+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            (ordE.gt(a1, b1) && ordE.lt(a2, b2)) shouldBe true
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
            yy.during(xx) shouldBe true

            assertOne(Rel.Contains)(xx, yy)

            // a- < b- && b+ < a+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            (ordE.lt(a1, b1) && ordE.lt(b2, a2)) shouldBe true
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

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].during(Interval.open(5, 10)) shouldBe (false)
        Interval.empty[Int].during(Interval.point(0)) shouldBe (false)
        Interval.empty[Int].during(Interval.unbounded[Int]) shouldBe (false)

        // Point
        // {5}  (2, 9)
        Interval.point(5).during(Interval.open(2, 9)) shouldBe (true)
        Interval.point(5).during(Interval.closed(2, 9)) shouldBe (true)

        // (2, 9)  {5}
        Interval.open(2, 9).contains(Interval.point(5)) shouldBe (true)

        Interval.point(5).during(Interval.closed(5, 10)) shouldBe (false)
        Interval.point(5).during(Interval.open(5, 10)) shouldBe (false)
        Interval.point(6).during(Interval.open(5, 10)) shouldBe (false)
        Interval.point(7).during(Interval.open(5, 10)) shouldBe (true)

        // Proper
        Interval.closed(5, 7).during(Interval.closed(2, 10)) shouldBe (true)
        Interval.open(5, 8).during(Interval.closed(5, 8)) shouldBe (true)

        // Infinity
        // [5, 7]  [3, +inf)
        Interval.closed(5, 7).during(Interval.leftClosed(3)) shouldBe (true)

        // [5, 7]  (-inf, 10]
        Interval.closed(5, 7).during(Interval.rightClosed(10)) shouldBe (true)

        // [5, 7] (-inf, +inf)
        Interval.closed(5, 7).during(Interval.unbounded[Int]) shouldBe (true)

        // (-∞,0]  (-∞,+∞)
        Interval.rightClosed(0).during(Interval.unbounded[Int]) shouldBe (false)

        // [0, 2)  (-∞,+∞)
        Interval.closed(0, 2).during(Interval.unbounded[Int]) shouldBe (true)

        // [0]  (-∞,+∞)
        Interval.point(0).during(Interval.unbounded[Int]) shouldBe (true)

        // (3,10), (0,+∞)
        Interval.open(3, 10).during(Interval.leftOpen(0)) shouldBe (true)
        Interval.leftOpen(0).contains(Interval.open(3, 10)) shouldBe (true)

        // [doc]
        Interval.closed(3, 7).during(Interval.closed(1, 10)) shouldBe (true)
        Interval.closed(1, 10).contains(Interval.closed(3, 7)) shouldBe (true)
      }
    }
  }
