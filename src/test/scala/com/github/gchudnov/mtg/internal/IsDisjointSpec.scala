package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IsDisjointSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IsDisjoint" when {
    "isDisjoint" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isDisjoint(yy)) {
            assertOneOf(Set(Rel.Before, Rel.After))(xx, yy)
          }
        }
      }
    }

    "manual check" in {
      Interval.open(1, 4).isDisjoint(Interval.open(3, 6)) mustBe (true)
      Interval.open(3, 6).isDisjoint(Interval.open(1, 4)) mustBe (true)
    }
  }
