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
       *   - Point      | [a, a] = {a}
       *   - Proper     | otherwise
       * }}}
       */
      "create intervals" in {
        forAll(genOneOfIntArgs) { case ((ox, ix), (oy, iy)) =>
          val actual = Interval.make(ox, ix, oy, iy)

          actual match
            case Interval.Empty =>
              actual.isEmpty mustBe (true)
              actual.nonEmpty mustBe (false)
              actual.isPoint mustBe (false)
              actual.nonPoint mustBe (true)
              actual.isProper mustBe (false)
              actual.nonProper mustBe (true)

            case Interval.Point(_) =>
              actual.isEmpty mustBe (false)
              actual.nonEmpty mustBe (true)
              actual.isPoint mustBe (true)
              actual.nonPoint mustBe (false)
              actual.isProper mustBe (false)
              actual.nonProper mustBe (true)

              summon[Ordering[Boundary[Int]]].equiv(Boundary.Left(ox, ix), Boundary.Right(oy, iy)) mustBe (true)

            case Interval.Proper(_, _) =>
              actual.isEmpty mustBe (false)
              actual.nonEmpty mustBe (true)
              actual.isPoint mustBe (false)
              actual.nonPoint mustBe (true)
              actual.isProper mustBe (true)
              actual.nonProper mustBe (false)

              summon[Ordering[Boundary[Int]]].lt(Boundary.Left(ox, ix), Boundary.Right(oy, iy)) mustBe (true)
        }
      }

      "handle edge cases" in {
        // [0, 0)
        Interval.make(Some(0), true, Some(0), false) mustBe Interval.empty[Int]

        // [-inf, +inf]
        Interval.make[Int](None, true, None, true).isUnbounded mustBe true

        // (-inf, +inf)
        Interval.make[Int](None, false, None, false) mustBe Interval.unbounded[Int]

        // (3, 5)
        Interval.make(Some(3), false, Some(5), false) mustBe Interval.point(4)

        // [4, 4]
        Interval.make(Some(4), true, Some(4), true) mustBe Interval.point(4)

        // (3, 4]
        Interval.make(Some(3), false, Some(4), true) mustBe Interval.point(4)

        // [4, 5)
        Interval.make(Some(4), true, Some(5), false) mustBe Interval.point(4)

        // [-inf, inf]

      }
    }

    "factory methods" should {

      "Interval.empty[Int]" in {
        val a = Interval.empty[Int]

        a.isEmpty mustBe (true)
        a.isPoint mustBe (false)
        a.isProper mustBe (false)

        a.nonEmpty mustBe (false)
        a.nonPoint mustBe (true)
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

      "Interval.empty" in {
        val a = Interval.empty

        a.isEmpty mustBe (true)
        a.isPoint mustBe (false)
        a.isProper mustBe (false)

        a.nonEmpty mustBe (false)
        a.nonPoint mustBe (true)
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

      "Interval.point(x)" in {
        val a = Interval.point(5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (true)
        a.isProper mustBe (false)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (false)
        a.nonProper mustBe (true)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "Interval.proper(x, y, w, z)" in {
        val a = Interval.proper(Some(1), false, Some(5), false)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "Interval.proper(a, b)" in {
        val a = Interval.proper(Boundary.Left(Some(1), true), Boundary.Right(Some(5), true))

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "Interval.unbounded[Int]" in {
        val a = Interval.unbounded[Int]

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
        a.isUnbounded mustBe (true)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "Interval.open(x, y)" in {
        val a = Interval.open(1, 5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "Interval.closed(x, y)" in {
        val a = Interval.closed(1, 5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "Interval.leftOpen(x)" in {
        val a = Interval.leftOpen(1)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "Interval.leftClosed(x)" in {
        val a = Interval.leftClosed(5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (false)
        a.right.isUnbounded mustBe (true)
      }

      "Interval.rightOpen(x)" in {
        val a = Interval.rightOpen(1)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "Interval.rightClosed(x)" in {
        val a = Interval.rightClosed(5)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (false)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (false)
        a.left.isUnbounded mustBe (true)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "Interval.leftClosedRightOpen(x, y)" in {
        val a = Interval.leftClosedRightOpen(1, 10)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

      "Interval.leftOpenRightClosed(x, y)" in {
        val a = Interval.leftOpenRightClosed(1, 10)

        a.isEmpty mustBe (false)
        a.isPoint mustBe (false)
        a.isProper mustBe (true)

        a.nonEmpty mustBe (true)
        a.nonPoint mustBe (true)
        a.nonProper mustBe (false)

        a.isBounded mustBe (true)
        a.isUnbounded mustBe (false)

        a.left.isBounded mustBe (true)
        a.left.isUnbounded mustBe (false)

        a.right.isBounded mustBe (true)
        a.right.isUnbounded mustBe (false)
      }

    }

    "canonical" should {
      "represent" in {
        // (1, 5) = [2, 4]
        Interval.open(1, 5).canonical mustBe Interval.closed(2, 4)

        // [2, 5) = [2, 4]
        Interval.leftClosedRightOpen(2, 5).canonical mustBe Interval.closed(2, 4)

        // (1, 4] = [2, 4]
        Interval.leftOpenRightClosed(1, 4).canonical mustBe Interval.closed(2, 4)

        // [2, 4] = [2, 4]
        Interval.closed(2, 4).canonical mustBe Interval.closed(2, 4)

        // {2} = {2}
        Interval.point(2).canonical mustBe Interval.point(2)
        Interval.make(Some(2), true, Some(2), true).canonical mustBe Interval.point(2)
        Interval.make(Some(1), false, Some(3), false).canonical mustBe Interval.point(2)
        Interval.make(Boundary.Left(Some(1), false), Boundary.Right(Some(3), false)).canonical mustBe Interval.point(2)
      }

      "double canonical is canonical" in {
        Interval.open(1, 5).canonical.canonical mustBe Interval.closed(2, 4)
      }
    }
  }
