package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Boundary

/**
 * Intersects
 */
final class IntersectsSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordB: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "Intersects" when {
    import IntervalRelAssert.*

    "a.intersects(b)" should {
      "b.intersects(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.intersects(yy)) {
            yy.intersects(xx) mustBe true

            assertOneOf(
              Set(Rel.Meets, Rel.IsMetBy, Rel.Overlaps, Rel.IsOverlapedBy, Rel.During, Rel.Contains, Rel.Starts, Rel.IsStartedBy, Rel.Finishes, Rel.IsFinishedBy, Rel.EqualsTo)
            )(xx, yy)

            // a- <= b+ && b- <= a+
            val a1 = Boundary.Left(ox1, ix1)
            val b1 = Boundary.Left(oy1, iy1)

            val a2 = Boundary.Right(ox2, ix2)
            val b2 = Boundary.Right(oy2, iy2)

            (ordB.lteq(a1, b2) && ordB.lteq(b1, a2)) mustBe (true)
          }
        }
      }
    }

    "a.intersects(b) AND b.intersects(a)" should {

      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          val actual   = xx.intersects(yy)
          val expected = yy.intersects(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].intersects(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].intersects(Interval.point(0)) mustBe (false)
        Interval.empty[Int].intersects(Interval.closed(0, 1)) mustBe (false)

        // Point
        Interval.point(5).intersects(Interval.point(5)) mustBe (true)
        Interval.point(5).intersects(Interval.point(6)) mustBe (false)
        Interval.point(5).intersects(Interval.empty[Int]) mustBe (false)

        // Proper
        Interval.open(4, 7).intersects(Interval.open(4, 7)) mustBe (true)
        Interval.open(0, 5).intersects(Interval.open(0, 5)) mustBe (true)
        Interval.closed(0, 5).intersects(Interval.closed(1, 6)) mustBe (true)
        Interval.closed(1, 6).intersects(Interval.closed(0, 5)) mustBe (true)
        Interval.leftOpenRightClosed(0, 5).intersects(Interval.leftOpenRightClosed(1, 15)) mustBe (true)
        Interval.leftClosedRightOpen(0, 5).intersects(Interval.leftClosedRightOpen(0, 5)) mustBe (true)

        // Infinity
        // [5, +inf)  (-inf, 10)
        Interval.leftClosed(5).intersects(Interval.rightOpen(10)) mustBe (true)
      }
    }
  }
