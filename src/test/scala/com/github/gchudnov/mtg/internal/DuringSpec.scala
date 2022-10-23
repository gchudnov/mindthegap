package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
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

  "During" when {
    "during & contains (includes)" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.during(yy)) {
            assertOne(Rel.During)(xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].during(Interval.open(5, 10)) mustBe (false)
        Interval.empty[Int].during(Interval.point(0)) mustBe (false)
        Interval.empty[Int].during(Interval.unbounded[Int]) mustBe (false)

        // Point
        // {5}  (2, 9)
        Interval.point(5).during(Interval.open(2, 9)) mustBe (true)
        Interval.point(5).during(Interval.closed(2, 9)) mustBe (true)

        // (2, 9)  {5}
        Interval.open(2, 9).contains(Interval.point(5)) mustBe (true)

        Interval.point(5).during(Interval.closed(5, 10)) mustBe (false)
        Interval.point(5).during(Interval.open(5, 10)) mustBe (false)
        Interval.point(6).during(Interval.open(5, 10)) mustBe (false)
        Interval.point(7).during(Interval.open(5, 10)) mustBe (true)

        // Proper
        Interval.closed(5, 7).during(Interval.closed(2, 10)) mustBe (true)
        Interval.open(5, 8).during(Interval.closed(5, 8)) mustBe (true)

        // Infinity
        // [5, 7]  [3, +inf)
        Interval.closed(5, 7).during(Interval.leftClosed(3)) mustBe (true)

        // [5, 7]  (-inf, 10]
        Interval.closed(5, 7).during(Interval.rightClosed(10)) mustBe (true)

        // [5, 7] (-inf, +inf)
        Interval.closed(5, 7).during(Interval.unbounded[Int]) mustBe (true)

        // [-∞,0]  [-∞,+∞)
        Interval.proper(None, true, Some(0), true).during(Interval.proper[Int](None, true, None, false)) mustBe (false)

        // [0, 2)  [-∞,+∞]
        Interval.proper(Some(0), true, Some(2), false).during(Interval.proper[Int](None, true, None, true)) mustBe (true)

        // [0]  [-∞,+∞)
        Interval.point(0).during(Interval.proper[Int](None, true, None, false)) mustBe (true)

        // (3,+∞), (0,+∞]
        Interval.leftOpen(3).during(Interval.proper(Some(0), false, None, true)) mustBe (true)
        Interval.proper(Some(0), false, None, true).contains(Interval.leftOpen(3)) mustBe (true)
      }
    }
  }
