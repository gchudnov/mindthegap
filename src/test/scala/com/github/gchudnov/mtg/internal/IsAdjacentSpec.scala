package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IsAdjacentSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IsAdjacent" when {
    "isAdjacent" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isAdjacent(yy)) {
            assertOneOf(Set(Rel.Before, Rel.After))(xx, yy)
          }
        }
      }
    }

    "manual check" in {
      // (1, 4)  (3, 6)
      Interval.open(1, 4).isAdjacent(Interval.open(3, 6)) mustBe (true)
      Interval.open(3, 6).isAdjacent(Interval.open(1, 4)) mustBe (true)

      // (1, 4)  (4, 6)
      Interval.open(1, 4).isAdjacent(Interval.open(4, 7)) mustBe (false)
      Interval.open(4, 7).isAdjacent(Interval.open(1, 4)) mustBe (false)

      // [1, 4]  [5, 6]
      Interval.closed(1, 4).isAdjacent(Interval.closed(5, 6)) mustBe (true)
      Interval.closed(5, 6).isAdjacent(Interval.closed(1, 4)) mustBe (true)
    }
  }
