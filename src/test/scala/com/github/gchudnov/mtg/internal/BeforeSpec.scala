package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
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

  val ordM: Ordering[Mark[Int]] = summon[Ordering[Mark[Int]]]

  "Before" when {
    import IntervalRelAssert.*

    "a.before(b)" should {
      "b.after(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy)) {
            yy.after(xx) mustBe true

            assertOne(Rel.Before)(xx, yy)

            // a+ < b-
            val a2 = argsX.right
            val b1 = argsY.left

            ordM.lt(a2, b1) mustBe true
          }
        }
      }
    }

    "a.after(b)" should {
      "b.before(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy)) {
            yy.before(xx) mustBe true

            assertOne(Rel.After)(xx, yy)

            // a- > b+
            val a1 = argsX.left
            val b2 = argsY.right

            ordM.gt(a1, b2) mustBe true
          }
        }
      }
    }

    "a.before(b) AND b.after(a)" should {

      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.before(yy)
          val expected = yy.after(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].before(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].before(Interval.point(1)) mustBe (false)
        Interval.empty[Int].before(Interval.closed(1, 4)) mustBe (false)
        Interval.empty[Int].before(Interval.open(1, 4)) mustBe (false)
        Interval.empty[Int].before(Interval.unbounded[Int]) mustBe (false)

        // Point
        Interval.point(5).before(Interval.empty[Int]) mustBe (false)
        Interval.point(5).before(Interval.point(5)) mustBe (false)
        Interval.point(5).before(Interval.point(6)) mustBe (true)
        Interval.point(6).after(Interval.point(5)) mustBe (true)
        Interval.point(5).before(Interval.point(10)) mustBe (true)
        Interval.point(5).before(Interval.open(5, 10)) mustBe (true)
        Interval.point(5).before(Interval.closed(5, 10)) mustBe (false)
        Interval.point(5).before(Interval.closed(6, 10)) mustBe (true)
        Interval.point(5).before(Interval.leftClosed(5)) mustBe (false)
        Interval.point(5).before(Interval.leftClosed(6)) mustBe (true)
        Interval.point(5).before(Interval.unbounded[Int]) mustBe (false)

        // (-∞,0], {4}
        Interval.point(4).after(Interval.rightClosed(0)) mustBe (true)
        Interval.rightClosed(0).before(Interval.point(4)) mustBe (true)

        // Proper
        Interval.open(4, 7).before(Interval.open(4, 7)) mustBe (false)
        Interval.open(1, 4).before(Interval.empty[Int]) mustBe (false)
        Interval.open(1, 4).before(Interval.open(5, 8)) mustBe (true)
        Interval.open(5, 8).after(Interval.open(1, 4)) mustBe (true)
        Interval.open(1, 4).before(Interval.closed(5, 6)) mustBe (true)

        Interval.open(1, 4).before(Interval.open(3, 6)) mustBe (true)
        Interval.open(3, 6).after(Interval.open(1, 4)) mustBe (true)

        // Infinity
        // (1, 4)  (3, +inf)
        Interval.open(1, 4).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2)  (3, 6)
        Interval.rightOpen(2).before(Interval.open(3, 6)) mustBe (true)

        // (-inf, 2)  (3, +inf)
        Interval.rightOpen(2).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2]  (3, +inf)
        Interval.rightClosed(2).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2)  [3, +inf)
        Interval.rightOpen(2).before(Interval.leftClosed(3)) mustBe (true)
      }
    }
  }
