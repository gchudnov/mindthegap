package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.Domain
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

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "IsSubset" when {
    import IntervalRelAssert.*

    "a.isSubset(b)" should {
      "b.isSuperset(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isSubset(yy)) {
            yy.isSuperset(xx) shouldBe true

            assertOneOf(Set(Rel.Starts, Rel.During, Rel.Finishes, Rel.EqualsTo))(xx, yy)

            // b- <= a- && b+ >= a+
            (ordE.lteq(yy.leftEndpoint, xx.leftEndpoint) && ordE.gteq(yy.rightEndpoint, xx.rightEndpoint)) shouldBe true
          }
        }
      }

      "valid in special cases" in {
        Interval.closed(1, 10).isSubset(Interval.unbounded[Int]) shouldBe (true)

        // [doc]
        Interval.closed(4, 7).isSubset(Interval.closed(4, 10)) shouldBe (true)
        Interval.closed(4, 7).isSubset(Interval.closed(2, 10)) shouldBe (true)
        Interval.closed(4, 7).isSubset(Interval.closed(2, 7)) shouldBe (true)
        Interval.closed(4, 7).isSubset(Interval.closed(4, 7)) shouldBe (true)
      }
    }
  }
