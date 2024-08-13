package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.internal.Endpoint

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

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "Overlap" when {
    import IntervalRelAssert.*

    "a.overlaps(b)" should {
      "b.isOverlappedBy(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            yy.isOverlappedBy(xx) shouldBe true

            // aliases
            Interval.overlaps(xx, yy) shouldBe true
            Interval.isOverlappedBy(yy, xx) shouldBe true

            assertOne(Rel.Overlaps)(xx, yy)

            // a- < b+ && b- < a+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            (ordE.lt(a1, b2) && ordE.lt(b1, a2)) shouldBe true
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
            yy.overlaps(xx) shouldBe true

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

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        // {}  (-inf, +inf)
        Interval.empty[Int].overlaps(Interval.unbounded[Int]) shouldBe (false)
        Interval.unbounded[Int].overlaps(Interval.empty[Int]) shouldBe (false)

        // {} [1, 2]
        Interval.empty[Int].overlaps(Interval.closed(1, 2)) shouldBe (false)
        Interval.closed(1, 2).overlaps(Interval.empty[Int]) shouldBe (false)

        // Point
        Interval.point(5).overlaps(Interval.closed(1, 10)) shouldBe (false)
        Interval.point(5).overlaps(Interval.closed(1, 3)) shouldBe (false)
        Interval.point(5).overlaps(Interval.closed(7, 10)) shouldBe (false)
        Interval.point(5).overlaps(Interval.empty[Int]) shouldBe (false)
        Interval.point(5).overlaps(Interval.unbounded[Int]) shouldBe (false)

        // Proper
        // (1, 10)  (5, 20)
        Interval.open(1, 10).overlaps(Interval.open(5, 20)) shouldBe (true)
        Interval.open(5, 30).isOverlappedBy(Interval.open(1, 10)) shouldBe (true)

        // (1, 10)  (2, 11)
        Interval.open(1, 10).overlaps(Interval.open(2, 11)) shouldBe (true)

        // (1, 10)  (11, 20)
        Interval.open(1, 10).overlaps(Interval.open(11, 20)) shouldBe (false)

        // (1, 10)  (1, 11)
        Interval.open(1, 10).overlaps(Interval.open(1, 11)) shouldBe (false)

        // (1, 10)  (20, 30)
        Interval.open(1, 10).overlaps(Interval.open(20, 30)) shouldBe (false)

        // (1, 10)  {10}
        Interval.open(1, 10).overlaps(Interval.point(10)) shouldBe (false)

        // [1, 10], {10}
        Interval.closed(1, 10).overlaps(Interval.point(10)) shouldBe (false)

        // (1, 10)  (-10, 20)
        Interval.open(1, 10).overlaps(Interval.open(-10, 20)) shouldBe (false)

        // (1, 10)  (2, 11)
        Interval.open(1, 10).isOverlappedBy(Interval.open(2, 11)) shouldBe (false)

        // (1, 10)  (2, 10)
        Interval.open(1, 10).isOverlappedBy(Interval.open(2, 10)) shouldBe (false)

        // (2, 12)  (1, 10)
        Interval.open(2, 12).isOverlappedBy(Interval.open(1, 10)) shouldBe (true)

        // (1, 12)  (1, 10)
        Interval.open(1, 12).isOverlappedBy(Interval.open(1, 10)) shouldBe (false)

        // (2, 12)  (1, 10)
        Interval.open(2, 12).isOverlappedBy(Interval.open(1, 10)) shouldBe (true)

        // (1, 10)  (5, 20)
        Interval.open(1, 10).overlaps(Interval.open(5, 20)) shouldBe (true)

        // Infinity
        // [1, 5]  [3, +inf)
        Interval.closed(1, 5).overlaps(Interval.leftClosed(3)) shouldBe (true)

        // (-inf, 5]  [3, 10]
        Interval.rightClosed(5).overlaps(Interval.closed(3, 10)) shouldBe (true)

        // (-inf, 5]  [3, +inf)
        Interval.rightClosed(5).overlaps(Interval.leftClosed(3)) shouldBe (true)

        // (-inf, 1)  (-inf, +inf)
        Interval.rightOpen(1).overlaps(Interval.unbounded[Int]) shouldBe (false)

        // (-inf, +inf)  (0, +inf)
        Interval.unbounded[Int].overlaps(Interval.leftOpen(0)) shouldBe (false)

        // (-inf, +inf)  [0, +inf)
        Interval.unbounded[Int].overlaps(Interval.leftClosed(0)) shouldBe (false)

        // (-inf, +inf) (1, 10)
        Interval.unbounded[Int].overlaps(Interval.open(1, 10)) shouldBe (false)
        Interval.open(1, 10).overlaps(Interval.unbounded[Int]) shouldBe (false)
        Interval.unbounded[Int].isOverlappedBy(Interval.open(1, 10)) shouldBe (false)
        Interval.open(1, 10).isOverlappedBy(Interval.unbounded[Int]) shouldBe (false)

        // (-inf, +inf) {2}
        Interval.unbounded[Int].isOverlappedBy(Interval.point(2)) shouldBe (false)
        Interval.point(2).isOverlappedBy(Interval.unbounded[Int]) shouldBe (false)

        // (-inf, 2)  (-2, +inf)
        Interval.rightOpen(2).overlaps(Interval.leftOpen(-2)) shouldBe (true)
        Interval.leftOpen(-2).isOverlappedBy(Interval.rightOpen(2)) shouldBe (true)

        // [doc]
        Interval.closed(1, 10).overlaps(Interval.closed(5, 15)) shouldBe (true)
        Interval.closed(5, 15).isOverlappedBy(Interval.closed(1, 10)) shouldBe (true)
      }
    }
  }
