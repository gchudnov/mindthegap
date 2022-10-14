package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IsSupersetSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IsSuperset" when {
    "isSuperset" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneIntTuple, genOneIntTuple) { case (((ox1, ox2), ix1, ix2), ((oy1, oy2), iy1, iy2)) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isSuperset(yy)) {
            assertOneOf(Set(Rel.IsStartedBy, Rel.Contains, Rel.IsFinishedBy, Rel.EqualsTo))(xx, yy)
          }
        }
      }

      "manual check" in {
        Interval.open(4, 10).isSuperset(Interval.open(4, 7)) mustBe (true)
        Interval.open(2, 10).isSuperset(Interval.open(4, 7)) mustBe (true)
        Interval.open(2, 7).isSuperset(Interval.open(4, 7)) mustBe (true)
        Interval.open(4, 7).isSuperset(Interval.open(4, 7)) mustBe (true)
      }
    }
  }
