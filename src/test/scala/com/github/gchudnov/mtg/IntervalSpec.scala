package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration

import java.time.Instant

final class IntervalSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  "Interval" when {

    "make" should {

      /**
       * {{{
       *   Given that a < b:
       *
       *   - Empty      | [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
       *   - Degenerate | [a, a] = {a}
       *   - Proper     | otherwise
       * }}}
       */
      "create intervals" in {
        forAll(genOneIntTuple) { case ((ox, oy), ix, iy) =>
          val actual = Interval.make(ox, oy, ix, iy)

          actual match
            case Empty =>
              actual.isEmpty mustBe (true)
              actual.isDegenrate mustBe (false)
              actual.isProper mustBe (false)

              (ox, oy) match
                case (Some(x), Some(y)) =>
                  val isXgtY = x > y
                  val isXeqY = x == y

                  (isXgtY || (isXeqY && ((ix == false && iy == false) || (ix == true && iy == false) || (ix == false && iy == true)))) mustBe (true)
                case _ =>
                  fail("Empty Interval boundaries must be defined.")
            case Degenerate(a) =>
              actual.isEmpty mustBe (false)
              actual.isDegenrate mustBe (true)
              actual.isProper mustBe (false)

              (ox, oy) match
                case (Some(x), Some(y)) =>
                  ix mustBe (true)
                  iy mustBe (true)
                  x mustEqual (y)
                case _ =>
                  fail("Degenerate Interval boundaries must be defined.")
            case Proper(oa, ob, ia, ib) =>
              actual.isEmpty mustBe (false)
              actual.isDegenrate mustBe (false)
              actual.isProper mustBe (true)

              (ox, oy) match
                case (Some(x), Some(y)) =>
                  (x < y) mustBe (true)
                case _ =>
                  succeed
        }
      }
    }

    "toString" should {

      "represent an Empty interval" in {
        val value    = Interval.empty[Int]
        val actual   = value.toString()
        val expected = "∅"

        actual mustBe expected
      }

      "represent a Degenerate interval" in {
        val value    = Interval.degenerate(1)
        val actual   = value.toString()
        val expected = "{1}"

        actual mustBe expected
      }

      "represent a Proper interval" in {
        val t = Table(
          ("x", "expected"),
          (Interval.proper(Some(1), Some(2), true, true), "[1,2]"),
          (Interval.proper(Some(1), Some(2), true, false), "[1,2)"),
          (Interval.proper(Some(1), Some(2), false, true), "(1,2]"),
          (Interval.proper(Some(1), Some(2), false, false), "(1,2)"),
          (Interval.proper(None, Some(2), true, true), "[-∞,2]"),
          (Interval.proper(None, Some(2), true, false), "[-∞,2)"),
          (Interval.proper(None, Some(2), false, true), "(-∞,2]"),
          (Interval.proper(None, Some(2), false, false), "(-∞,2)"),
          (Interval.proper(Some(1), None, true, true), "[1,+∞]"),
          (Interval.proper(Some(1), None, true, false), "[1,+∞)"),
          (Interval.proper(Some(1), None, false, true), "(1,+∞]"),
          (Interval.proper(Some(1), None, false, false), "(1,+∞)"),
          (Interval.proper[Nothing](None, None, true, true), "[-∞,+∞]"),
          (Interval.proper[Nothing](None, None, true, false), "[-∞,+∞)"),
          (Interval.proper[Nothing](None, None, false, true), "(-∞,+∞]"),
          (Interval.proper[Nothing](None, None, false, false), "(-∞,+∞)")
        )

        forAll(t) { (x, expected) =>
          val actual = x.toString()
          actual mustBe expected
        }
      }
    }
  }
