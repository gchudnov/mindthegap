package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IsAdjacentSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Mark[Int]] = summon[Ordering[Mark[Int]]]

  "IsAdjacent" when {
    import IntervalRelAssert.*

    "a.isAdjacent(b)" should {
      "b.isAdjacent(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isAdjacent(yy)) {
            yy.isAdjacent(xx) mustBe true

            assertOneOf(Set(Rel.Before, Rel.After))(xx, yy)

            // succ(a+) = b- OR succ(b+) = a-
            if xx.before(yy) then
              // succ(a+) = b-
              val b1 = argsY.left
              val a2 = argsX.right

              ordM.equiv(a2.succ, b1) mustBe (true)
            else if xx.after(yy) then
              // succ(b+) = a-
              val a1 = argsX.left
              val b2 = argsY.right

              ordM.equiv(b2.succ, a1) mustBe (true)
          }
        }
      }
    }

    "a.isAdjacent(b) AND b.isAdjacent(a)" should {

      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.isAdjacent(yy)
          val expected = yy.isAdjacent(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // (1, 4)  (3, 6)
        Interval.open(1, 4).isAdjacent(Interval.open(3, 6)) mustBe (true)
        Interval.open(3, 6).isAdjacent(Interval.open(1, 4)) mustBe (true)

        // (1, 4)  (4, 6)
        Interval.open(1, 4).isAdjacent(Interval.open(4, 7)) mustBe (false)
        Interval.open(4, 7).isAdjacent(Interval.open(1, 4)) mustBe (false)

        // [1, 4]  [5, 6]
        Interval.closed(1, 4).isAdjacent(Interval.closed(5, 6)) mustBe (true)
        Interval.closed(5, 6).isAdjacent(Interval.closed(1, 4)) mustBe (true)
      }
    }
  }
