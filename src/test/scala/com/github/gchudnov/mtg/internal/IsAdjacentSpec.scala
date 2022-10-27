package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Boundary

final class IsAdjacentSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordB: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "IsAdjacent" when {
    import IntervalRelAssert.*

    "a.isAdjacent(b)" should {
      "b.isAdjacent(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isAdjacent(yy)) {
            yy.isAdjacent(xx) mustBe true

            assertOneOf(Set(Rel.Before, Rel.After))(xx, yy)

            if xx.before(yy) then
              // succ(a+) = b-
              val b1 = Boundary.Left(oy1, iy1)
              val a2 = Boundary.Right(ox2, ix2)

              ordB.equiv(a2.succ, b1) mustBe (true)
            else if xx.after(yy) then
              // succ(b+) = a-
              val a1 = Boundary.Left(ox1, ix1)
              val b2 = Boundary.Right(oy2, iy2)

              ordB.equiv(b2.succ, a1) mustBe (true)
          }
        }
      }
    }

    "a.isAdjacent(b) AND b.isAdjacent(a)" should {

      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

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
