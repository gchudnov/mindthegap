package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * IsLess
 *
 * {{{
 *   isLess                    AAAAA            |  a- < b- AND a+ < b+
 *                             :   :
 *   before(a,b)      b        :   : BBBBBBBBB
 *   meets(a,b)       m        :   BBBBBBBBB
 *   overlaps(a,b)    o        : BBBBBBBBB
 * }}}
 */
final class IsLessSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IsLess" when {
    import IntervalRelAssert.*

    "a.isLess(b)" should {
      "b.isGreater(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isLess(yy)) {
            yy.isGreater(xx) mustBe true

            assertOneOf(Set(Rel.Before, Rel.Meets, Rel.Overlaps))(xx, yy)
          }
        }
      }
    }

    "a.isGreater(b)" should {
      "b.isLess(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isGreater(yy)) {
            yy.isLess(xx) mustBe true

            assertOneOf(Set(Rel.After, Rel.IsMetBy, Rel.IsOverlapedBy))(xx, yy)
          }
        }
      }
    }

    "a.isLess(b) AND b.isGreater(a)" should {

      "verify" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(!xx.equalsTo(yy)) {
            val actual   = xx.isLess(yy)
            val expected = yy.isGreater(xx)

            actual mustBe expected
          }
        }
      }

      "valid in special cases" in {
        Interval.open(4, 7).isLess(Interval.open(10, 15)) mustBe (true)
        Interval.open(4, 7).isLess(Interval.open(6, 15)) mustBe (true)
        Interval.open(4, 7).isLess(Interval.open(5, 15)) mustBe (true)

        Interval.empty[Int].isLess(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].isGreater(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].equalsTo(Interval.empty[Int]) mustBe (false)

        Interval.open(1, 5).isLess(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].isLess(Interval.open(1, 5)) mustBe (false)

        Interval.open(1, 5).isGreater(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].isGreater(Interval.open(1, 5)) mustBe (false)
      }
    }
  }
