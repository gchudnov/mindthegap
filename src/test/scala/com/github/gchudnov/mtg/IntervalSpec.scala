package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.Arbitraries.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

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
        // given bOrd: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

        forAll(genOneIntTuple) { case ((ox, oy), ix, iy) =>
          val actual = Interval.make(ox, ix, oy, iy)

          actual match
            case Interval.Empty =>
              actual.isEmpty mustBe (true)
              actual.nonEmpty mustBe (false)
              actual.isDegenrate mustBe (false)
              actual.nonDegenerate mustBe (true)
              actual.isProper mustBe (false)
              actual.nonProper mustBe (true)

            case Interval.Degenerate(_) =>
              actual.isEmpty mustBe (false)
              actual.nonEmpty mustBe (true)
              actual.isDegenrate mustBe (true)
              actual.nonDegenerate mustBe (false)
              actual.isProper mustBe (false)
              actual.nonProper mustBe (true)

              // bOrd.equiv(Boundary.Left(ox, ix), Boundary.Right(oy, iy)) mustBe (true)

            case Interval.Proper(_, _) =>
              actual.isEmpty mustBe (false)
              actual.nonEmpty mustBe (true)
              actual.isDegenrate mustBe (false)
              actual.nonDegenerate mustBe (true)
              actual.isProper mustBe (true)
              actual.nonProper mustBe (false)

              // bOrd.lt(Boundary.Left(ox, ix), Boundary.Right(oy, iy)) mustBe (true)
        }
      }

      "handle edge cases" in {
        // [0, 0)
        Interval.make(Some(0), true, Some(0), false).isEmpty mustBe (true)

        // [-inf, +inf]
        Interval.make[Int](None, true, None, true).isEmpty mustBe (false)
      }
    }

    "factory methods" should {

      "construct an empty interval with type parameter" in {
        val a = Interval.empty[Int]

        a.isEmpty mustBe (true)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (false)

        a.nonEmpty mustBe (false)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (true)

        a.isBounded mustBe (false)
        a.isUnbounded mustBe (false)

        assertThrows[NoSuchElementException] {
          a.left
        }

        assertThrows[NoSuchElementException] {
          a.right
        }
      }

      "construct an empty interval without type parameter" in {
        val a = Interval.empty

        a.isEmpty mustBe (true)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (false)

        a.nonEmpty mustBe (false)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (true)

        a.isBounded mustBe (false)
        a.isUnbounded mustBe (false)

        assertThrows[NoSuchElementException] {
          a.left
        }

        assertThrows[NoSuchElementException] {
          a.right
        }
      }

      "construct a degenerate interval" in {
        val a = Interval.degenerate(5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (true)
        a.isProper mustBe (false)

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (false)
        a.nonProper mustBe (true)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct a proper interval" in {
        val a = Interval.proper(Some(1), false, Some(5), false)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct a proper interval with left and right boundaries" in {
        val a = Interval.proper(Boundary.Left(Some(1), true), Boundary.Right(Some(5), true))

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "construct an unbounded interval" in {
        val a = Interval.unbounded[Int]

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
        a.isUnbounded mustBe (true)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "construct an open interval" in {
        val a = Interval.open(1, 5)

        a.isEmpty mustBe (false)
        a.isDegenrate mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
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

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
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

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
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

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
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

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
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

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
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

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
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

        a.nonEmpty mustBe (true)
        a.nonDegenerate mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

    }

  }
