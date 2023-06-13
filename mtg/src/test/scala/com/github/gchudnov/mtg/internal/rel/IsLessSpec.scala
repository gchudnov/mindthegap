package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * IsLess
 *
 * {{{
 *   Relation         Symbol          AAAAA
 *                                    :   :
 *   a.before(b)         b            :   : BBBBBBBBB  | a+ < b-
 *   a.meets(b)          m            :   BBBBBBBBB    | a+ = b-
 *   a.overlaps(b)       o            : BBBBBBBBB      | a- < b- < a+ < b+
 *   a.starts(b)         s            BBBBBBBBB        | a- = b- ; a+ < b+
 *   a.contains(b)       D            : B :            | a- < b- ; b+ < a+
 *   a.isFinishedBy(b)   F            : BBB            | a+ = b+ ; a- < b-
 * }}}
 */
final class IsLessSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IsLess" when {
    import IntervalRelAssert.*

    "a.isGreater(b)" should {
      "b.isLess(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isGreater(yy)) {
            yy.isLess(xx) mustBe true

            if xx.nonEmpty && yy.nonEmpty then assertOneOf(Set(Rel.After, Rel.IsMetBy, Rel.IsOverlappedBy, Rel.IsStartedBy, Rel.During, Rel.Finishes))(xx, yy)
          }
        }
      }
    }

    "a.isLess(b)" should {
      "b.isGreater(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isLess(yy)) {
            yy.isGreater(xx) mustBe true

            if xx.nonEmpty && yy.nonEmpty then assertOneOf(Set(Rel.Before, Rel.Meets, Rel.Overlaps, Rel.Starts, Rel.Contains, Rel.IsFinishedBy))(xx, yy)
          }
        }
      }

      "true if a.before(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy)) {
            xx.isLess(yy) mustBe (true)
            yy.isLess(xx) mustBe (false)

            xx.isGreater(yy) mustBe (false)
            yy.isGreater(xx) mustBe (true)
          }
        }
      }

      "true if a.meets(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.meets(yy)) {
            xx.isLess(yy) mustBe (true)
            yy.isLess(xx) mustBe (false)

            xx.isGreater(yy) mustBe (false)
            yy.isGreater(xx) mustBe (true)
          }
        }
      }

      "true if a.overlaps(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            xx.isLess(yy) mustBe (true)
            yy.isLess(xx) mustBe (false)

            xx.isGreater(yy) mustBe (false)
            yy.isGreater(xx) mustBe (true)
          }
        }
      }

      "true if a.starts(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.starts(yy)) {
            xx.isLess(yy) mustBe (true)
            yy.isLess(xx) mustBe (false)

            xx.isGreater(yy) mustBe (false)
            yy.isGreater(xx) mustBe (true)
          }
        }
      }

      "false if a.during(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.during(yy)) {
            xx.isLess(yy) mustBe (false)
            yy.isLess(xx) mustBe (true)

            xx.isGreater(yy) mustBe (true)
            yy.isGreater(xx) mustBe (false)
          }
        }
      }

      "false if a.finishes(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.finishes(yy)) {
            xx.isLess(yy) mustBe (false)
            yy.isLess(xx) mustBe (true)

            xx.isGreater(yy) mustBe (true)
            yy.isGreater(xx) mustBe (false)
          }
        }
      }

      "false if a.after(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy)) {
            xx.isLess(yy) mustBe (false)
            yy.isLess(xx) mustBe (true)

            xx.isGreater(yy) mustBe (true)
            yy.isGreater(xx) mustBe (false)
          }
        }
      }

      "false if a.isMetBy(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            xx.isLess(yy) mustBe (false)
            yy.isLess(xx) mustBe (true)

            xx.isGreater(yy) mustBe (true)
            yy.isGreater(xx) mustBe (false)
          }
        }
      }

      "false if a.isOverlappedBy(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isOverlappedBy(yy)) {
            xx.isLess(yy) mustBe (false)
            yy.isLess(xx) mustBe (true)

            xx.isGreater(yy) mustBe (true)
            yy.isGreater(xx) mustBe (false)
          }
        }
      }

      "false if a.isStartedBy(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            xx.isLess(yy) mustBe (false)
            yy.isLess(xx) mustBe (true)

            xx.isGreater(yy) mustBe (true)
            yy.isGreater(xx) mustBe (false)
          }
        }
      }

      "true if a.contains(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            xx.isLess(yy) mustBe (true)
            yy.isLess(xx) mustBe (false)

            xx.isGreater(yy) mustBe (false)
            yy.isGreater(xx) mustBe (true)
          }
        }
      }

      "true if a.isFinishedBy(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isFinishedBy(yy)) {
            xx.isLess(yy) mustBe (true)
            yy.isLess(xx) mustBe (false)

            xx.isGreater(yy) mustBe (false)
            yy.isGreater(xx) mustBe (true)
          }
        }
      }

      "false if a.equalsTo(b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.equalsTo(yy)) {
            xx.isLess(yy) mustBe (false)
            yy.isLess(xx) mustBe (false)

            xx.isGreater(yy) mustBe (false)
            yy.isGreater(xx) mustBe (false)
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
