package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IsDisjointSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "IsDisjoint" when {
    import IntervalRelAssert.*

    "a.isDisjoint(b)" should {
      "b.isDisjoint(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isDisjoint(yy)) {
            yy.isDisjoint(xx) shouldBe true

            assertOneOf(Set(Rel.Before, Rel.After))(xx, yy)

            // a+ < b- || a- > b+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            val cmpRes = ordE.lt(a2, b1) || ordE.gt(a1, b2)
            cmpRes shouldBe true

            (ordE.lt(xx.rightEndpoint, yy.leftEndpoint) || ordE.gt(xx.leftEndpoint, yy.rightEndpoint)) shouldBe true
          }
        }
      }
    }

    "a.isDisjoint(b) AND b.isDisjoint(a)" should {
      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.isDisjoint(yy)
          val expected = yy.isDisjoint(xx)

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        Interval.open(1, 4).isDisjoint(Interval.open(3, 6)) shouldBe (true)
        Interval.open(3, 6).isDisjoint(Interval.open(1, 4)) shouldBe (true)

        // [doc]
        Interval.closed(5, 7).isDisjoint(Interval.closed(1, 3)) shouldBe (true)
        Interval.closed(5, 7).isDisjoint(Interval.closed(8, 10)) shouldBe (true)
      }
    }
  }
