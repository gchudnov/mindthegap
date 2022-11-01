package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class GapSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Gap" when {
    "a.gap(b)" should {
      "b.gap(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val zz = xx.gap(yy)

          whenever(zz.nonEmpty) {
            val ww = yy.gap(xx)

            zz.canonical mustBe ww.canonical

            // gap should be not intersecting with `a` or `b`
            zz.intersects(xx) mustBe false
            zz.intersects(yy) mustBe false

            // gap must be adjacent
            zz.isAdjacent(xx) mustBe true
            zz.isAdjacent(yy) mustBe true
          }
        }
      }
    }

    "a.gap(b)" should {

      "∅ if A = (-inf, 0], B = (-inf, 0)" in {
        val xx = Interval.rightClosed(0)
        val yy = Interval.rightOpen(0)

        val zz = xx.gap(yy)

        zz mustBe Interval.empty[Int]
      }

      "∅ if A = (-inf, inf), B = (-inf, inf)" in {
        val xx = Interval.unbounded[Int]
        val yy = Interval.unbounded[Int]

        val zz = xx.gap(yy)

        zz mustBe Interval.empty[Int]
      }

      "∅ if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.gap(b)
        val expected = Interval.closed(11, 19)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.closed(11, 19)

        actual mustBe expected
      }

      "∅ if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ in A overlaps B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(7, 15)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }
    }

    "negative intervals" should {
    }

    "A, B" should {
      "A ∥ B = B ∥ A" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.gap(yy).canonical
          val expected = yy.gap(xx).canonical

          actual mustBe expected
        }
      }
    }

    "Interval" should {
      "Interval.gap(a, b)" in {
        val a = Interval.closed(1, 5)  // [1, 5]
        val b = Interval.closed(7, 10) // [7, 10]

        val expected = Interval.point(6) // {6}

        val c1 = Interval.gap(a, b).canonical
        val c2 = Interval.gap(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }
  }
