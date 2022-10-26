package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * IsLess
 *
 * {{{
 *   isLess                    AAAAA            |  a- < b- AND a+ < b+
 *                             :   :
 *   before(a,b)      b        :   : BBBBBBBBB
 *   meets(a,b)       m        :   BBBBBBBBB
 *   overlaps(a,b)    o        : BBBBBBBBB
 * }}}
 */
final class IsLessSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IsLess" when {
    "isLess" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isLess(yy)) {
            assertOneOf(Set(Rel.Before, Rel.Meets, Rel.Overlaps))(xx, yy)
          }
        }
      }

      "manual check" in {
        Interval.open(4, 7).isLess(Interval.open(10, 15)) mustBe (true)
        Interval.open(4, 7).isLess(Interval.open(6, 15)) mustBe (true)
        Interval.open(4, 7).isLess(Interval.open(5, 15)) mustBe (true)
      }
    }
  }
