package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class LawsSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Laws" when {

    // "a.intersection(b) is defined" should {
    //   "a.UNION(b) is certainly defined as well" in {
    //     forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
    //       val xx = Interval.make(ox1, ix1, ox2, ix2)
    //       val yy = Interval.make(oy1, iy1, oy2, iy2)

    //       whenever(xx.intersection(yy).nonEmpty) {
    //         xx.union(yy).nonEmpty mustBe true
    //       }
    //     }
    //   }
    // }

  }
