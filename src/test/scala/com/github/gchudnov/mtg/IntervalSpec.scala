package com.github.gchudnov.mtg
import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Domains.given
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant

final class IntervalSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

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
              actual.nonEmpty mustBe (false)
              actual.isDegenrate mustBe (false)
              actual.nonDegenerate mustBe (true)
              actual.isProper mustBe (false)
              actual.nonProper mustBe (true)

            case ab @ Degenerate(_) =>
              actual.isEmpty mustBe (false)
              actual.nonEmpty mustBe (true)
              actual.isDegenrate mustBe (true)
              actual.nonDegenerate mustBe (false)
              actual.isProper mustBe (false)
              actual.nonProper mustBe (true)

              bOrd.equiv(LeftBoundary(ox, ix), RightBoundary(oy, iy)) mustBe (true)

            case Proper(_, _) =>
              actual.isEmpty mustBe (false)
              actual.nonEmpty mustBe (true)
              actual.isDegenrate mustBe (false)
              actual.nonDegenerate mustBe (true)
              actual.isProper mustBe (true)
              actual.nonProper mustBe (false)

              bOrd.lt(LeftBoundary(ox, ix), RightBoundary(oy, iy)) mustBe (true)
        }
      }

      "edge cases" in {
        Interval.make(Some(0), Some(0), true, false).isEmpty mustBe (true)
      }
    }

    "create" should {

      "construct an empty interval" in {
        val a = Interval.empty[Int]

        a.isEmpty mustBe (true)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (false)

        a.isUnbounded mustBe (false)

        assertThrows[NoSuchElementException] {
          a.left
        }

        assertThrows[NoSuchElementException] {
          a.right
        }
      }

      "construct an unbounded interval" in {
        val a = Interval.unbounded[Int]

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (true)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "construct a degenerate interval" in {
        val a = Interval.degenerate(5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (true)
        a.isProper mustBe (false)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct a proper interval" in {
        val a = Interval.proper(Some(1), Some(5), false, false)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct an open interval" in {
        val a = Interval.open(1, 5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct a closed interval" in {
        val a = Interval.closed(1, 5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct a leftOpen interval" in {
        val a = Interval.leftOpen(1)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "construct a leftClosed interval" in {
        val a = Interval.leftClosed(5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "construct a rightOpen interval" in {
        val a = Interval.rightOpen(1)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct a rightClosed interval" in {
        val a = Interval.rightClosed(5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct a leftClosedRightOpen interval" in {
        val a = Interval.leftClosedRightOpen(1, 10)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct a leftOpenRightClosed interval" in {
        val a = Interval.leftOpenRightClosed(1, 10)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

    }

  }
