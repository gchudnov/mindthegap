package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IsAdjacentSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "IsAdjacent" when {
    import IntervalRelAssert.*

    "a.isAdjacent(b)" should {
      "b.isAdjacent(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isAdjacent(yy)) {
            yy.isAdjacent(xx) shouldBe true

            assertOneOf(Set(Rel.Before, Rel.After))(xx, yy)

            // succ(a+) = b- OR succ(b+) = a-
            if xx.before(yy) then
              // succ(a+) = b-
              val b1 = argsY.left
              val a2 = argsX.right

              ordE.equiv(a2.succ, b1) shouldBe (true)
              ()
            else if xx.after(yy) then
              // succ(b+) = a-
              val a1 = argsX.left
              val b2 = argsY.right

              ordE.equiv(b2.succ, a1) shouldBe (true)
              ()
          }
        }
      }
    }

    "a.isAdjacent(b) AND b.isAdjacent(a)" should {

      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.isAdjacent(yy)
          val expected = yy.isAdjacent(xx)

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        // (1, 4)  (3, 6)
        Interval.open(1, 4).isAdjacent(Interval.open(3, 6)) shouldBe (true)
        Interval.open(3, 6).isAdjacent(Interval.open(1, 4)) shouldBe (true)

        // (1, 4)  (4, 6)
        Interval.open(1, 4).isAdjacent(Interval.open(4, 7)) shouldBe (false)
        Interval.open(4, 7).isAdjacent(Interval.open(1, 4)) shouldBe (false)

        // [1, 4]  [5, 6]
        Interval.closed(1, 4).isAdjacent(Interval.closed(5, 6)) shouldBe (true)
        Interval.closed(5, 6).isAdjacent(Interval.closed(1, 4)) shouldBe (true)

        // [doc]
        Interval.closed(5, 7).isAdjacent(Interval.closed(8, 10)) shouldBe (true)
        Interval.closed(1, 4).isAdjacent(Interval.closed(5, 7)) shouldBe (true)
      }
    }
  }
