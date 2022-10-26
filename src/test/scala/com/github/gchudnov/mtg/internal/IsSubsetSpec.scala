package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * IsSubset
 *
 * {{{
 *   isSubset                  AAAAA            |  a- >= b- ; a+ <= b+
 *                             :   :
 *   starts(a,b)      s        BBBBBBBBB
 *   during(a,b)      d      BBBBBBBBB
 *   finishes(a,b)    f    BBBBBBBBB
 *   equals(a, b)     e        BBBBB
 * }}}
 */
final class IsSubsetSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IsSubset" when {
    "isSubset" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isSubset(yy)) {
            assertOneOf(Set(Rel.Starts, Rel.During, Rel.Finishes, Rel.EqualsTo))(xx, yy)
          }
        }
      }

      "manual check" in {
        Interval.open(4, 7).isSubset(Interval.open(4, 10)) mustBe (true)
        Interval.open(4, 7).isSubset(Interval.open(2, 10)) mustBe (true)
        Interval.open(4, 7).isSubset(Interval.open(2, 7)) mustBe (true)
        Interval.open(4, 7).isSubset(Interval.open(4, 7)) mustBe (true)
      }
    }
  }
