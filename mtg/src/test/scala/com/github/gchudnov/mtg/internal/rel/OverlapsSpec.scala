package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Endpoint

/**
 * Overlaps, IsOverlappedBy
 *
 * {{{
 *   AAAA
 *     BBBB
 * }}}
 */
final class OverlapsSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "Overlap" when {
    import IntervalRelAssert.*

    "a.overlaps(b)" should {
      "b.isOverlappedBy(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            yy.isOverlappedBy(xx) mustBe true

            assertOne(Rel.Overlaps)(xx, yy)

            // a- < b+ && b- < a+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            (ordM.lt(a1, b2) && ordM.lt(b1, a2)) mustBe true
          }
        }
      }
    }

    "a.isOverlappedBy(b)" should {
      "b.overlaps(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isOverlappedBy(yy)) {
            yy.overlaps(xx) mustBe true

            assertOne(Rel.IsOverlappedBy)(xx, yy)
          }
        }
      }
    }

    "a.overlaps(b) AND b.isOverlappedBy(a)" should {
      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.overlaps(yy)
          val expected = yy.isOverlappedBy(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        // {}  (-inf, +inf)
        Interval.empty[Int].overlaps(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].overlaps(Interval.empty[Int]) mustBe (false)

        // {} [1, 2]
        Interval.empty[Int].overlaps(Interval.closed(1, 2)) mustBe (false)
        Interval.closed(1, 2).overlaps(Interval.empty[Int]) mustBe (false)

        // Point
        Interval.point(5).overlaps(Interval.closed(1, 10)) mustBe (false)
        Interval.point(5).overlaps(Interval.closed(1, 3)) mustBe (false)
        Interval.point(5).overlaps(Interval.closed(7, 10)) mustBe (false)
        Interval.point(5).overlaps(Interval.empty[Int]) mustBe (false)
        Interval.point(5).overlaps(Interval.unbounded[Int]) mustBe (false)

        // Proper
        // (1, 10)  (5, 20)
        Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)
        Interval.open(5, 30).isOverlappedBy(Interval.open(1, 10)) mustBe (true)

        // (1, 10)  (2, 11)
        Interval.open(1, 10).overlaps(Interval.open(2, 11)) mustBe (true)

        // (1, 10)  (11, 20)
        Interval.open(1, 10).overlaps(Interval.open(11, 20)) mustBe (false)

        // (1, 10)  (1, 11)
        Interval.open(1, 10).overlaps(Interval.open(1, 11)) mustBe (false)

        // (1, 10)  (20, 30)
        Interval.open(1, 10).overlaps(Interval.open(20, 30)) mustBe (false)

        // (1, 10)  {10}
        Interval.open(1, 10).overlaps(Interval.point(10)) mustBe (false)

        // [1, 10], {10}
        Interval.closed(1, 10).overlaps(Interval.point(10)) mustBe (false)

        // (1, 10)  (-10, 20)
        Interval.open(1, 10).overlaps(Interval.open(-10, 20)) mustBe (false)

        // (1, 10)  (2, 11)
        Interval.open(1, 10).isOverlappedBy(Interval.open(2, 11)) mustBe (false)

        // (1, 10)  (2, 10)
        Interval.open(1, 10).isOverlappedBy(Interval.open(2, 10)) mustBe (false)

        // (2, 12)  (1, 10)
        Interval.open(2, 12).isOverlappedBy(Interval.open(1, 10)) mustBe (true)

        // (1, 12)  (1, 10)
        Interval.open(1, 12).isOverlappedBy(Interval.open(1, 10)) mustBe (false)

        // (2, 12)  (1, 10)
        Interval.open(2, 12).isOverlappedBy(Interval.open(1, 10)) mustBe (true)

        // (1, 10)  (5, 20)
        Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)

        // Infinity
        // [1, 5]  [3, +inf)
        Interval.closed(1, 5).overlaps(Interval.leftClosed(3)) mustBe (true)

        // (-inf, 5]  [3, 10]
        Interval.rightClosed(5).overlaps(Interval.closed(3, 10)) mustBe (true)

        // (-inf, 5]  [3, +inf)
        Interval.rightClosed(5).overlaps(Interval.leftClosed(3)) mustBe (true)

        // (-inf, 1)  (-inf, +inf)
        Interval.rightOpen(1).overlaps(Interval.unbounded[Int]) mustBe (false)

        // (-inf, +inf)  (0, +inf)
        Interval.unbounded[Int].overlaps(Interval.leftOpen(0)) mustBe (false)

        // (-inf, +inf)  [0, +inf)
        Interval.unbounded[Int].overlaps(Interval.leftClosed(0)) mustBe (false)

        // (-inf, +inf) (1, 10)
        Interval.unbounded[Int].overlaps(Interval.open(1, 10)) mustBe (false)
        Interval.open(1, 10).overlaps(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isOverlappedBy(Interval.open(1, 10)) mustBe (false)
        Interval.open(1, 10).isOverlappedBy(Interval.unbounded[Int]) mustBe (false)

        // (-inf, +inf) {2}
        Interval.unbounded[Int].isOverlappedBy(Interval.point(2)) mustBe (false)
        Interval.point(2).isOverlappedBy(Interval.unbounded[Int]) mustBe (false)

        // (-inf, 2)  (-2, +inf)
        Interval.rightOpen(2).overlaps(Interval.leftOpen(-2)) mustBe (true)
        Interval.leftOpen(-2).isOverlappedBy(Interval.rightOpen(2)) mustBe (true)

        // [doc]
        Interval.closed(1, 10).overlaps(Interval.closed(5, 15)) mustBe (true)
        Interval.closed(5, 15).isOverlappedBy(Interval.closed(1, 10)) mustBe (true)
      }
    }
  }
