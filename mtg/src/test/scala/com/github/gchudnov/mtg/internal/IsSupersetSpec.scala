package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.internal.rel.IntervalRelAssert

final class IsSupersetSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Mark[Int]] = summon[Domain[Int]].ordMark

  "IsSuperset" when {
    import IntervalRelAssert.*

    "a.isSuperset(b)" should {
      "b.isSubset(a)" in {

        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isSuperset(yy)) {
            yy.isSubset(xx) mustBe true

            assertOneOf(Set(Rel.IsStartedBy, Rel.Contains, Rel.IsFinishedBy, Rel.EqualsTo))(xx, yy)

            // a- <= b- && a+ >= b+
            (ordM.lteq(xx.left, yy.left) && ordM.gteq(xx.right, yy.right)) mustBe true
          }
        }
      }

      "valid in special cases" in {
        Interval.unbounded[Int].isSuperset(Interval.closed(1, 10)) mustBe (true)

        // [doc]
        Interval.closed(4, 10).isSuperset(Interval.closed(4, 7)) mustBe (true)
        Interval.closed(2, 10).isSuperset(Interval.closed(4, 7)) mustBe (true)
        Interval.closed(2, 7).isSuperset(Interval.closed(4, 7)) mustBe (true)
        Interval.closed(4, 7).isSuperset(Interval.closed(4, 7)) mustBe (true)
      }
    }
  }
