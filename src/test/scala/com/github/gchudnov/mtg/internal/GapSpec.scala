package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class GapSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Gap" when {

    "a.gap(b)" should {

      "∅ if A = (-inf, 0], B = (-inf, 0)" in {
        val a = Interval.rightClosed(0)
        val b = Interval.rightOpen(0)

        val actual   = a.gap(b).canonical
        val expected = Interval.make(Mark.at(Value.finite(0)), Mark.at(Value.infNeg)) // [0, -inf)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ if A = (-inf, inf), B = (-inf, inf)" in {
        val a = Interval.unbounded[Int]
        val b = Interval.unbounded[Int]

        val actual   = a.gap(b).canonical
        val expected = Interval.empty[Int] // (+inf, -inf)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "proper if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.gap(b).canonical
        val expected = Interval.unbounded[Int] // (-inf, +inf)

        actual.isProper mustBe (true)
        actual mustBe expected
      }

      "proper if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b).canonical
        val expected = Interval.unbounded[Int] // (-inf, +inf)

        actual.isProper mustBe (true)
        actual mustBe expected
      }

      "proper if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.gap(b).canonical
        val expected = Interval.unbounded[Int] // (-inf, +inf)

        actual.isProper mustBe (true)
        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.gap(b).canonical
        val expected = Interval.closed(11, 19) // [11, 19]

        actual.isProper mustBe (true)
        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b).canonical
        val expected = Interval.closed(11, 19) // [11, 19]

        actual.isProper mustBe (true)
        actual mustBe expected
      }

      "∅ if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(5)), Mark.pred(Value.finite(1))) // (5, 1)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(7)), Mark.pred(Value.finite(5))) // (7, 5)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval(Mark.succ(Value.finite(10)), Mark.pred(Value.finite(5))) // (10, 5)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(10)), Mark.pred(Value.finite(5))) // (10, 5)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(7)), Mark.pred(Value.finite(5))) // (7, 5)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(5)), Mark.pred(Value.finite(5))) // (5, 5)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(5)), Mark.pred(Value.finite(1)))

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(5)), Mark.pred(Value.finite(5))) // (5, 5)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ in A overlaps B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(7, 15)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(10)), Mark.pred(Value.finite(7))) // (10, 7)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(10)), Mark.pred(Value.finite(7))) // (10, 7)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }

      "∅ if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        val actual   = a.gap(b)
        val expected = Interval.make(Mark.succ(Value.finite(7)), Mark.pred(Value.finite(5))) // (7, 5)

        actual.isEmpty mustBe (true)
        actual mustBe expected
      }
    }

    "negative intervals" should {
      "empty ∥ proper is (proper OR point OR empty)" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          val actual   = xx.gap(yy).canonical
          val expected = yy.gap(xx).canonical

          (actual.isEmpty, actual.isPoint, actual.isProper) mustBe (expected.isEmpty, expected.isPoint, expected.isProper)

          actual mustBe expected
        }
      }

      "empty ∥ empty is (proper OR point OR empty)" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.isEmpty mustBe (true)

          val actual   = xx.gap(yy).canonical
          val expected = yy.gap(xx).canonical

          (actual.isEmpty || actual.isProper) mustBe true
          (expected.isEmpty || expected.isProper) mustBe true

          (actual.isEmpty, actual.isPoint, actual.isProper) mustBe (expected.isEmpty, expected.isPoint, expected.isProper)

          actual mustBe expected
        }
      }

      "proper & proper is non-empty IF (disjoint AND !adjacent)" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          whenever(xx.isDisjoint(yy) && !xx.isAdjacent(yy)) {
            val actual   = xx.gap(yy).canonical
            val expected = yy.gap(xx).canonical

            actual.nonEmpty mustBe true
            expected.nonEmpty mustBe true

            actual mustBe expected
          }
        }
      }

      "proper & proper is empty IF adjacent" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          whenever(xx.isAdjacent(yy)) {
            val actual   = xx.gap(yy).canonical
            val expected = yy.gap(xx).canonical

            actual.isEmpty mustBe true
            expected.isEmpty mustBe true

            actual mustBe expected
          }
        }
      }

      "proper & proper is empty IF !disjoint" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          whenever(!xx.isDisjoint(yy)) {
            val actual   = xx.gap(yy).canonical
            val expected = yy.gap(xx).canonical

            actual.isEmpty mustBe true
            expected.isEmpty mustBe true

            actual mustBe expected
          }
        }
      }
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

    // // gap should be not intersecting with `a` or `b`
    // zz.intersects(xx) mustBe false
    // zz.intersects(yy) mustBe false

    // // gap must be adjacent
    // zz.isAdjacent(xx) mustBe true
    // zz.isAdjacent(yy) mustBe true

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
