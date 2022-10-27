package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Boundary

/**
 * Starts, IsStartedBy
 *
 * {{{
 *   AAA
 *   BBBBBB
 * }}}
 */
final class StartsSpec extends TestSpec: // with IntervalRelAssert {}

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordB: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "Starts" when {
    import IntervalRelAssert.*

    "a.starts(b)" should {
      "b.isStartedBy(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.starts(yy)) {
            yy.isStartedBy(xx) mustBe true

            assertOne(Rel.Starts)(xx, yy)

            // a- = b- && b.isSuperset(a+)
            val a1 = Boundary.Left(ox1, ix1)
            val b1 = Boundary.Left(oy1, iy1)

            val a2 = Boundary.Right(ox2, ix2)
            val b2 = Boundary.Right(oy2, iy2)

            ordB.equiv(a1, b1) mustBe true

            yy.isSuperset(Interval.make(a2.asLeft, a2.asRight)) mustBe true
          }
        }
      }
    }

    "a.isStartedBy(b)" should {
      "b.starts(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isStartedBy(yy)) {
            yy.starts(xx) mustBe true

            assertOne(Rel.IsStartedBy)(xx, yy)
          }
        }
      }
    }

    "a.starts(b) AND b.isStartedBy(a)" should {
      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          val actual   = xx.starts(yy)
          val expected = yy.isStartedBy(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].starts(Interval.point(0)) mustBe (false)
        Interval.empty[Int].starts(Interval.closed(0, 1)) mustBe (false)
        Interval.empty[Int].starts(Interval.unbounded[Int]) mustBe (false)

        // Point
        Interval.point(5).starts(Interval.closed(5, 10)) mustBe (true)
        Interval.point(5).starts(Interval.open(4, 10)) mustBe (true)
        Interval.point(5).starts(Interval.open(5, 10)) mustBe (false)
        Interval.point(5).starts(Interval.empty[Int]) mustBe (false)
        Interval.point(5).starts(Interval.unbounded[Int]) mustBe (false)

        // Proper
        // [1, 2]  [1, 10]
        Interval.closed(1, 2).starts(Interval.closed(1, 10)) mustBe (true)
        Interval.closed(1, 10).isStartedBy(Interval.closed(1, 2)) mustBe (true)

        // (1, 3)  (1, 10)
        Interval.open(1, 4).starts(Interval.open(1, 10)) mustBe (true)

        // Infinity
        // [1, 5] [1, +inf)
        Interval.closed(1, 5).starts(Interval.leftClosed(1)) mustBe (true)

        // (-inf, 5]  (-inf, 10]
        Interval.rightClosed(5).starts(Interval.rightClosed(10)) mustBe (true)

        // (-inf, 5)  (-inf, +inf)
        Interval.rightClosed(5).starts(Interval.unbounded[Int]) mustBe (true)

        //  [5, 10)  [5, +inf)
        Interval.leftClosedRightOpen(5, 10).starts(Interval.leftClosed(5)) mustBe (true)
        Interval.leftClosedRightOpen(5, 10).isStartedBy(Interval.leftClosed(5)) mustBe (false)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].starts(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isStartedBy(Interval.unbounded[Int]) mustBe (false)

        // (0,2]  (0,+∞]
        Interval.leftOpenRightClosed(0, 2).starts(Interval.leftOpen(0)) mustBe (true)
      }
    }
  }
