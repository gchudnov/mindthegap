package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Overlaps, IsOverlapedBy
 *
 * {{{
 *   AAAA
 *     BBBB
 * }}}
 */
final class OverlapsSpec extends TestSpec: // with IntervalRelAssert {}

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Overlap" when {
    "overlaps & isOverlapedBy" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.overlaps(yy)) {
            assertOne(Rel.Overlaps)(xx, yy)
          }
        }
      }

      "manual check" in {
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
        Interval.open(5, 30).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

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
        Interval.open(1, 10).isOverlapedBy(Interval.open(2, 11)) mustBe (false)

        // (1, 10)  (2, 10)
        Interval.open(1, 10).isOverlapedBy(Interval.open(2, 10)) mustBe (false)

        // (2, 12)  (1, 10)
        Interval.open(2, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

        // (1, 12)  (1, 10)
        Interval.open(1, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (false)

        // (2, 12)  (1, 10)
        Interval.open(2, 12).isOverlapedBy(Interval.open(1, 10)) mustBe (true)

        // (1, 10)  (5, 20)
        Interval.open(1, 10).overlaps(Interval.open(5, 20)) mustBe (true)

        // Infinity
        // [1, 5]  [3, +inf)
        Interval.closed(1, 5).overlaps(Interval.leftClosed(3)) mustBe (true)

        // (-inf, 5]  [3, 10]
        Interval.rightClosed(5).overlaps(Interval.closed(3, 10)) mustBe (true)

        // (-inf, 5]  [3, +inf)
        Interval.rightClosed(5).overlaps(Interval.leftClosed(3)) mustBe (true)

        // [-inf, 1)  (-inf, +inf)
        Interval.proper(None, true, Some(1), false).overlaps(Interval.unbounded[Int]) mustBe (true)

        // (-inf, +inf)  (0, +inf]
        Interval.unbounded[Int].overlaps(Interval.proper(Some(0), false, None, true)) mustBe (true)

        // (-inf, +inf)  [0, +inf]
        Interval.unbounded[Int].overlaps(Interval.proper(Some(0), true, None, true)) mustBe (true)

        // (-inf, +inf) (1, 10)
        Interval.unbounded[Int].overlaps(Interval.open(1, 10)) mustBe (false)
        Interval.open(1, 10).overlaps(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isOverlapedBy(Interval.open(1, 10)) mustBe (false)
        Interval.open(1, 10).isOverlapedBy(Interval.unbounded[Int]) mustBe (false)

        // (-inf, +inf) {2}
        Interval.unbounded[Int].isOverlapedBy(Interval.point(2)) mustBe (false)
        Interval.point(2).isOverlapedBy(Interval.unbounded[Int]) mustBe (false)

        // (-inf, 2)  (-2, +inf)
        Interval.rightOpen(2).overlaps(Interval.leftOpen(-2)) mustBe (true)
        Interval.leftOpen(-2).isOverlapedBy(Interval.rightOpen(2)) mustBe (true)
      }
    }
  }
