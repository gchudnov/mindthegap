package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Value

final class SpanSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Span" when {

    "a.span(b)" should {

      "any if A and B are empty" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.isEmpty mustBe (true)

          val actual   = xx.span(yy).canonical
          val expected = yy.span(xx).canonical

          actual mustBe expected
        }
      }

      "[,] if A is empty" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          val actual   = xx.span(yy).canonical
          val expected = yy.span(xx).canonical

          actual.isEmpty mustBe false
          expected.isEmpty mustBe false

          actual mustBe expected
        }
      }

      "[,] if A and B are non-empty" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          val actual   = xx.span(yy).canonical
          val expected = yy.span(xx).canonical

          actual.isEmpty mustBe false
          expected.isEmpty mustBe false

          actual mustBe expected
        }
      }

      "[,] if A before B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] if A after B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] if A starts B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.starts(yy)) {
            val actual   = xx.span(yy).canonical
            val expected = yy.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }

      "[,] if A during B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.during(yy)) {
            val actual   = xx.span(yy).canonical
            val expected = yy.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }

      "[,] if A finishes B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.finishes(yy)) {
            val actual   = xx.span(yy).canonical
            val expected = yy.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }

      "[,] if A equals B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.equalsTo(yy) && xx.nonEmpty && yy.nonEmpty) {
            val actual   = xx.span(yy).canonical
            val expected = xx.span(yy).canonical

            actual.isEmpty mustBe false

            actual mustBe expected
          }
        }
      }

      "[,] if A is-overlapped-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isOverlapedBy(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] if A is-met-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] if A is-started-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            val actual   = xx.span(yy).canonical
            val expected = xx.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }

      "[,] in A meets B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.meets(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] in A overlaps B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] in A is-finished-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isFinishedBy(yy)) {
            val actual   = xx.span(yy).canonical
            val expected = xx.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }

      "[,] if A contains B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            val actual   = xx.span(yy).canonical
            val expected = xx.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }
    }

    "A, B" should {
      "A # B = B # A" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.span(yy).canonical
          val expected = yy.span(xx).canonical

          actual mustBe expected
        }
      }
    }

    "A, B, C" should {
      "(A # B) # C = A # (B # C)" in {
        forAll(genAnyIntArgs, genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY, argsZ) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)
          val zz = Interval.make(argsZ.left, argsZ.right)

          val actual   = ((xx.span(yy)).span(zz)).canonical
          val expected = xx.span(yy.span(zz)).canonical

          actual mustBe expected
        }
      }
    }

    "Interval" should {
      "Interval.span(a, b)" in {
        val a = Interval.closed(1, 5)  // [1, 5]
        val b = Interval.closed(7, 10) // [7, 10]

        val expected = Interval.closed(1, 10) // [1, 10]

        val c1 = Interval.span(a, b).canonical
        val c2 = Interval.span(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }
  }
