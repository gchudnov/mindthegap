package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Boundary

final class IsDisjointSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordB: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "IsDisjoint" when {
    import IntervalRelAssert.*

    "a.isDisjoint(b)" should {
      "b.isDisjoint(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isDisjoint(yy)) {
            yy.isDisjoint(xx) mustBe true

            assertOneOf(Set(Rel.Before, Rel.After))(xx, yy)

            // a+ < b- || a- > b+
            val a1 = Boundary.Left(ox1, ix1)
            val b1 = Boundary.Left(oy1, iy1)

            val a2 = Boundary.Right(ox2, ix2)
            val b2 = Boundary.Right(oy2, iy2)

            (ordB.lt(xx.right, yy.left) || ordB.gt(xx.left, yy.right)) mustBe true
          }
        }
      }
    }

    "a.isDisjoint(b) AND b.isDisjoint(a)" should {
      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          val actual   = xx.isDisjoint(yy)
          val expected = yy.isDisjoint(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        Interval.open(1, 4).isDisjoint(Interval.open(3, 6)) mustBe (true)
        Interval.open(3, 6).isDisjoint(Interval.open(1, 4)) mustBe (true)
      }
    }
  }
