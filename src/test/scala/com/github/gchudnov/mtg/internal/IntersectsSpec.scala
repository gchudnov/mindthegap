package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Intersects
 */
final class IntersectsSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Intersects" when {
    "intersects" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.intersects(yy)) {
            assertOneOf(
              Set(Rel.Meets, Rel.IsMetBy, Rel.Overlaps, Rel.IsOverlapedBy, Rel.During, Rel.Contains, Rel.Starts, Rel.IsStartedBy, Rel.Finishes, Rel.IsFinishedBy, Rel.EqualsTo)
            )(xx, yy)
          }
        }
      }

      "manual check" in {
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

    "A, B" should {
      /**
       * Commutative Property
       */
      "A interscts B = B interscts A" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          xx.intersects(yy) mustBe yy.intersects(xx)
        }
      }
    }
  }
