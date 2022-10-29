package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Mark

final class IsSupersetSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  // val ordB: Ordering[Mark[Int]] = summon[Ordering[Mark[Int]]]

  "IsSuperset" when {
    // import IntervalRelAssert.*

    // "a.isSuperset(b)" should {
    //   "b.isSubset(a)" in {

    //     forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
    //       val xx = Interval.make(ox1, ix1, ox2, ix2)
    //       val yy = Interval.make(oy1, iy1, oy2, iy2)

    //       whenever(xx.isSuperset(yy)) {
    //         yy.isSubset(xx) mustBe true

    //         assertOneOf(Set(Rel.IsStartedBy, Rel.Contains, Rel.IsFinishedBy, Rel.EqualsTo))(xx, yy)

    //         // a- <= b- && a+ >= b+
    //         (ordB.lteq(xx.left, yy.left) && ordB.gteq(xx.right, yy.right)) mustBe true
    //       }
    //     }
    //   }

    //   "valid in special cases" in {
    //     Interval.open(4, 10).isSuperset(Interval.open(4, 7)) mustBe (true)
    //     Interval.open(2, 10).isSuperset(Interval.open(4, 7)) mustBe (true)
    //     Interval.open(2, 7).isSuperset(Interval.open(4, 7)) mustBe (true)
    //     Interval.open(4, 7).isSuperset(Interval.open(4, 7)) mustBe (true)

    //     Interval.unbounded[Int].isSuperset(Interval.closed(1, 10)) mustBe (true)
    //   }
    // }
  }
