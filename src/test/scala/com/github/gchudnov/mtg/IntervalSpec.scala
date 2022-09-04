package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import org.scalacheck.Gen

import java.time.Instant

final class IntervalSpec extends TestSpec:

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
        import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

        forAll(genOneIntTuple) { case ((x, y), ix, iy) =>
          val actual = Interval.make(x, y, ix, iy)
          actual match
            case Empty =>
              (x, y) match
                case (Some(x1), Some(y1)) =>
                  val isXgtY = x1 > y1
                  val isXeqY = x == y

                  (isXgtY || (isXeqY && ((ix == false && iy == false) || (ix == true && iy == false) || (ix == false && iy == true)))) mustBe (true)
                case _ =>
                  fail("Empty Interval boundaries must be defined.")
            case Degenerate(a) =>
              x.isDefined mustBe (true)
              y.isDefined mustBe (true)
              ix mustBe (true)
              iy mustBe (true)
              x mustEqual (y)
            case Proper(a, b, ia, ib) =>
              (x, y) match
                case (Some(x1), Some(y1)) =>
                  (x1 < y1) mustBe (true)
                case _ =>
                  succeed
        }
      }
    }

    "toString" should {

      "represent an Empty interval" in {
        val value    = Interval.empty
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
        import org.scalatest.prop.TableDrivenPropertyChecks.*

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
