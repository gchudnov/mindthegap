package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Before, After (Preceeds, IsPreceededBy)
 *
 * {{{
 *   AAA
 *        BBB
 * }}}
 */
final class BeforeSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "Before" when {
    import IntervalRelAssert.*

    "a.before(b)" should {
      "b.after(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy)) {
            yy.after(xx) shouldBe true

            // aliases
            Interval.before(xx, yy) shouldBe true
            Interval.after(yy, xx) shouldBe true

            assertOne(Rel.Before)(xx, yy)

            // a+ < b-
            val a2 = argsX.right
            val b1 = argsY.left

            ordE.lt(a2, b1) shouldBe true
          }
        }
      }
    }

    "a.after(b)" should {
      "b.before(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy)) {
            yy.before(xx) shouldBe true

            assertOne(Rel.After)(xx, yy)

            // a- > b+
            val a1 = argsX.left
            val b2 = argsY.right

            ordE.gt(a1, b2) shouldBe true
          }
        }
      }
    }

    "a.before(b) AND b.after(a)" should {

      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.before(yy)
          val expected = yy.after(xx)

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].before(Interval.empty[Int]) shouldBe (false)
        Interval.empty[Int].before(Interval.point(1)) shouldBe (false)
        Interval.empty[Int].before(Interval.closed(1, 4)) shouldBe (false)
        Interval.empty[Int].before(Interval.open(1, 4)) shouldBe (false)
        Interval.empty[Int].before(Interval.unbounded[Int]) shouldBe (false)

        // Point
        Interval.point(5).before(Interval.empty[Int]) shouldBe (false)
        Interval.point(5).before(Interval.point(5)) shouldBe (false)
        Interval.point(5).before(Interval.point(6)) shouldBe (true)
        Interval.point(6).after(Interval.point(5)) shouldBe (true)
        Interval.point(5).before(Interval.point(10)) shouldBe (true)
        Interval.point(5).before(Interval.open(5, 10)) shouldBe (true)
        Interval.point(5).before(Interval.closed(5, 10)) shouldBe (false)
        Interval.point(5).before(Interval.closed(6, 10)) shouldBe (true)
        Interval.point(5).before(Interval.leftClosed(5)) shouldBe (false)
        Interval.point(5).before(Interval.leftClosed(6)) shouldBe (true)
        Interval.point(5).before(Interval.unbounded[Int]) shouldBe (false)

        // (-∞,0], {4}
        Interval.point(4).after(Interval.rightClosed(0)) shouldBe (true)
        Interval.rightClosed(0).before(Interval.point(4)) shouldBe (true)

        // Proper
        Interval.open(4, 7).before(Interval.open(4, 7)) shouldBe (false)
        Interval.open(1, 4).before(Interval.empty[Int]) shouldBe (false)
        Interval.open(1, 4).before(Interval.open(5, 8)) shouldBe (true)
        Interval.open(5, 8).after(Interval.open(1, 4)) shouldBe (true)
        Interval.open(1, 4).before(Interval.closed(5, 6)) shouldBe (true)

        Interval.open(1, 4).before(Interval.open(3, 6)) shouldBe (true)
        Interval.open(3, 6).after(Interval.open(1, 4)) shouldBe (true)

        // Infinity
        // (1, 4)  (3, +inf)
        Interval.open(1, 4).before(Interval.leftOpen(3)) shouldBe (true)

        // (-inf, 2)  (3, 6)
        Interval.rightOpen(2).before(Interval.open(3, 6)) shouldBe (true)

        // (-inf, 2)  (3, +inf)
        Interval.rightOpen(2).before(Interval.leftOpen(3)) shouldBe (true)

        // (-inf, 2]  (3, +inf)
        Interval.rightClosed(2).before(Interval.leftOpen(3)) shouldBe (true)

        // (-inf, 2)  [3, +inf)
        Interval.rightOpen(2).before(Interval.leftClosed(3)) shouldBe (true)

        // [doc]
        Interval.closed(1, 4).before(Interval.closed(5, 8)) shouldBe (true)
        Interval.closed(5, 8).after(Interval.closed(1, 4)) shouldBe (true)
      }
    }
  }
