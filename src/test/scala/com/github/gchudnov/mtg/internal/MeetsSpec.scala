package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
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
final class MeetsSpec extends TestSpec: // with IntervalRelAssert {}

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Meets" when {
    "meets & isMetBy" should {
//     "auto check" in {
//       forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
//         val xx = Interval.make(ox1, ox2, ix1, ix2)
//         val yy = Interval.make(oy1, oy2, iy1, iy2)

//         whenever(xx.meets(yy)) {
//           assertFwdBck("m", xx, yy)
//         }
//       }
//     }

      "manual check" in {
        // Empty
        Interval.empty[Int].meets(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].meets(Interval.degenerate(0)) mustBe (false)
        Interval.empty[Int].meets(Interval.closed(5, 10)) mustBe (false)
        Interval.empty[Int].meets(Interval.unbounded[Int]) mustBe (false)

        // Degenerate
        // 5  {}
        Interval.degenerate(6).meets(Interval.empty[Int]) mustBe (false)

        // 5  5
        Interval.degenerate(5).meets(Interval.degenerate(5)) mustBe (false)

        // 5  6
        Interval.degenerate(5).meets(Interval.degenerate(6)) mustBe (false)

        // 6  5
        Interval.degenerate(6).meets(Interval.degenerate(5)) mustBe (false)

        // Proper
        // [1, 5]  [5, 10]
        Interval.closed(1, 5).meets(Interval.closed(5, 10)) mustBe (true)
        Interval.closed(5, 10).isMetBy(Interval.closed(1, 5)) mustBe (true)

        // [1, 5)  (3, 10]
        Interval.leftClosedRightOpen(1, 5).meets(Interval.leftOpenRightClosed(3, 10)) mustBe (true)

        // Infinity
        // [1, 5]  [5, +inf)
        Interval.closed(1, 5).meets(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5]  [5, 10]
        Interval.rightClosed(5).meets(Interval.closed(5, 10)) mustBe (true)

        // (-inf, 5]  [5, +inf)
        Interval.rightClosed(5).meets(Interval.leftClosed(5)) mustBe (true)

        // (-inf, 5)  (5, +inf)
        Interval.rightOpen(5).meets(Interval.leftOpen(5)) mustBe (false)

        // (-inf, 6)  [5, +inf)
        Interval.rightOpen(6).meets(Interval.leftClosed(5)) mustBe (true)
      }
    }
  }
