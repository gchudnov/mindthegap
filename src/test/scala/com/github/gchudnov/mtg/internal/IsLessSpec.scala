package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * IsLess
 *
 * {{{
 *   isLess                    AAAAA            |  a- < b-
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
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isLess(yy)) {
            yy.isGreater(xx) mustBe true

            if (xx.nonEmpty && yy.nonEmpty) then assertOneOf(Set(Rel.Before, Rel.Meets, Rel.Overlaps, Rel.Contains, Rel.IsFinishedBy))(xx, yy)
          }
        }
      }
    }

    "a.isGreater(b)" should {
      "b.isLess(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isGreater(yy)) {
            yy.isLess(xx) mustBe true

            if (xx.nonEmpty && yy.nonEmpty) then assertOneOf(Set(Rel.After, Rel.IsMetBy, Rel.IsOverlappedBy, Rel.During, Rel.Finishes))(xx, yy)
          }
        }
      }
    }

    "a.isLess(b) AND b.isGreater(a)" should {

      "verify" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(!xx.equalsTo(yy)) {
            val actual   = xx.isLess(yy)
            val expected = yy.isGreater(xx)

            actual mustBe expected
          }
        }
      }

      "valid in edge cases" in {
        Interval.open(4, 7).isLess(Interval.open(10, 15)) mustBe (true)
        Interval.open(4, 7).isLess(Interval.open(6, 15)) mustBe (true)
        Interval.open(4, 7).isLess(Interval.open(5, 15)) mustBe (true)

        // empty
        Interval.empty[Int].isLess(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].isGreater(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].equalsTo(Interval.empty[Int]) mustBe (true)

        Interval.open(1, 5).isLess(Interval.empty[Int]) mustBe (true)
        Interval.empty[Int].isLess(Interval.open(1, 5)) mustBe (false)

        Interval.open(1, 5).isGreater(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].isGreater(Interval.open(1, 5)) mustBe (true)

        // unbounded
        Interval.unbounded[Int].isLess(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isGreater(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].equalsTo(Interval.unbounded[Int]) mustBe (true)

        Interval.open(1, 5).isLess(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isLess(Interval.open(1, 5)) mustBe (true)

        Interval.open(1, 5).isGreater(Interval.unbounded[Int]) mustBe (true)
        Interval.unbounded[Int].isGreater(Interval.open(1, 5)) mustBe (false)

        // unbounded, empty
        Interval.unbounded[Int].isLess(Interval.empty[Int]) mustBe (true)
        Interval.unbounded[Int].isGreater(Interval.empty[Int]) mustBe (false)
        Interval.unbounded[Int].equalsTo(Interval.empty[Int]) mustBe (false)

        Interval.empty[Int].isLess(Interval.unbounded[Int]) mustBe (false)
        Interval.unbounded[Int].isLess(Interval.empty[Int]) mustBe (true)

        Interval.empty[Int].isGreater(Interval.unbounded[Int]) mustBe (true)
        Interval.unbounded[Int].isGreater(Interval.empty[Int]) mustBe (false)

        // [doc]
        Interval.closed(1, 5).isLess(Interval.closed(5, 10)) // true

        // [doc]
        Interval.closed(5, 10).isGreater(Interval.closed(1, 5)) // true
      }
    }
  }
