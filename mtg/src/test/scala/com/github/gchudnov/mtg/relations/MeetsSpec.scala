package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Meets, IsMetBy
 *
 * {{{
 *   AAA
 *      BBB
 * }}}
 */
final class MeetsSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "Meets" when {
    import IntervalRelAssert.*

    "a.meets(b)" should {
      "b.isMetBy(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.meets(yy)) {
            yy.isMetBy(xx) shouldBe true

            // aliases
            Interval.meets(xx, yy) shouldBe true
            Interval.isMetBy(yy, xx) shouldBe true

            assertOne(Rel.Meets)(xx, yy)

            // a+ = b-
            val a2 = argsX.right
            val b1 = argsY.left

            ordE.equiv(a2, b1) shouldBe true
          }
        }
      }
    }

    "a.isMetBy(b)" should {
      "b.meets(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            yy.meets(xx) shouldBe true

            assertOne(Rel.IsMetBy)(xx, yy)

            // b+ = a-
            val a1 = argsX.left
            val b2 = argsY.right

            ordE.equiv(b2, a1) shouldBe true
          }
        }
      }
    }

    "a.meets(b) AND b.isMetBy(a)" should {
      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.meets(yy)
          val expected = yy.isMetBy(xx)

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].meets(Interval.empty[Int]) shouldBe (false)
        Interval.empty[Int].meets(Interval.point(0)) shouldBe (false)
        Interval.empty[Int].meets(Interval.closed(5, 10)) shouldBe (false)
        Interval.empty[Int].meets(Interval.unbounded[Int]) shouldBe (false)

        // Point
        // 5  {}
        Interval.point(6).meets(Interval.empty[Int]) shouldBe (false)

        // 5  5
        Interval.point(5).meets(Interval.point(5)) shouldBe (false)

        // 5  6
        Interval.point(5).meets(Interval.point(6)) shouldBe (false)

        // 6  5
        Interval.point(6).meets(Interval.point(5)) shouldBe (false)

        // Proper
        // [1, 5]  [5, 10]
        Interval.closed(1, 5).meets(Interval.closed(5, 10)) shouldBe (true)
        Interval.closed(5, 10).isMetBy(Interval.closed(1, 5)) shouldBe (true)

        // [1, 5)  (3, 10]
        Interval.leftClosedRightOpen(1, 5).meets(Interval.leftOpenRightClosed(3, 10)) shouldBe (true)

        // Infinity
        // [1, 5]  [5, +inf)
        Interval.closed(1, 5).meets(Interval.leftClosed(5)) shouldBe (true)

        // (-inf, 5]  [5, 10]
        Interval.rightClosed(5).meets(Interval.closed(5, 10)) shouldBe (true)

        // (-inf, 5]  [5, +inf)
        Interval.rightClosed(5).meets(Interval.leftClosed(5)) shouldBe (true)

        // (-inf, 5)  (5, +inf)
        Interval.rightOpen(5).meets(Interval.leftOpen(5)) shouldBe (false)

        // (-inf, 6)  [5, +inf)
        Interval.rightOpen(6).meets(Interval.leftClosed(5)) shouldBe (true)

        // [doc]
        Interval.closed(1, 5).meets(Interval.closed(5, 10)) shouldBe (true)
        Interval.closed(5, 10).isMetBy(Interval.closed(1, 5)) shouldBe (true)
      }
    }
  }
