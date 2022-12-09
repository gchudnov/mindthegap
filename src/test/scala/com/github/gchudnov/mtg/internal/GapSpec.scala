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

      "any if A and B are empty" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.isEmpty mustBe (true)

          val actual   = xx.gap(yy).canonical
          val expected = yy.gap(xx).canonical

          (actual.isEmpty, actual.isPoint, actual.isProper) mustBe (expected.isEmpty, expected.isPoint, expected.isProper)

          actual mustBe expected
        }
      }

      "any if A is empty" in {
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

      "[,] if A and B are non-empty, disjoint disjoint !adjacent" in {
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

      "∅ if A and B are non-empty and adjacent" in {
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

      "∅ if A and B are non-empty and !disjoint" in {
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

      "[,] if A before B and !adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy) && !xx.isAdjacent(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "∅ if A before B and adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy) && xx.isAdjacent(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "[,] if A after B and !adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy) && !xx.isAdjacent(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] if A after B and adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy) && xx.isAdjacent(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ if A starts B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.starts(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ if A during B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.during(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ if A finishes B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.finishes(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ if A equals B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.equalsTo(yy) && xx.nonEmpty && yy.nonEmpty) {
            val actual   = xx.gap(yy).canonical
            val expected = xx.gap(yy).canonical

            actual.isEmpty mustBe true

            actual mustBe expected
          }
        }
      }

      "∅ if A is-overlapped-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isOverlappedBy(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ if A is-met-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ if A is-started-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ in A meets B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.meets(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ in A overlaps B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ in A is-finished-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isFinishedBy(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

      "∅ if A contains B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty mustBe true
          }
        }
      }

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

      "[,] if disjoint [doc]" in {
        val a = Interval.closed(1, 4)
        val b = Interval.closed(7, 10)

        val actual   = a.gap(b).canonical
        val expected = Interval.closed(5, 6)

        actual mustBe expected
      }

      "[,] if non-disjoint [doc]" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual = a.gap(b).canonical

        actual.isEmpty mustBe true
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

          // (a & b).swap == (a ∥ b).inflate
          actual.inflate.canonical mustBe xx.intersection(yy).swap.canonical
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
