package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Endpoint
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IsDisjointSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "IsDisjoint" when {
    import IntervalRelAssert.*

    "a.isDisjoint(b)" should {
      "b.isDisjoint(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isDisjoint(yy)) {
            yy.isDisjoint(xx) mustBe true

            assertOneOf(Set(Rel.Before, Rel.After))(xx, yy)

            // a+ < b- || a- > b+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            val cmpRes = ordM.lt(a2, b1) || ordM.gt(a1, b2)
            cmpRes mustBe true

            (ordM.lt(xx.right, yy.left) || ordM.gt(xx.left, yy.right)) mustBe true
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

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        Interval.open(1, 4).isDisjoint(Interval.open(3, 6)) mustBe (true)
        Interval.open(3, 6).isDisjoint(Interval.open(1, 4)) mustBe (true)

        // [doc]
        Interval.closed(5, 7).isDisjoint(Interval.closed(1, 3)) mustBe (true)
        Interval.closed(5, 7).isDisjoint(Interval.closed(8, 10)) mustBe (true)
      }
    }
  }
