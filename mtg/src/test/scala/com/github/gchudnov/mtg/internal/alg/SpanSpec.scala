package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

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

          xx.isEmpty shouldBe (true)
          yy.isEmpty shouldBe (true)

          val actual   = xx.span(yy).canonical
          val expected = yy.span(xx).canonical

          actual shouldBe expected
        }
      }

      "[,] if A is empty" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          val actual   = xx.span(yy).canonical
          val expected = yy.span(xx).canonical

          actual.isEmpty shouldBe false
          expected.isEmpty shouldBe false

          actual shouldBe expected
        }
      }

      "[,] if A and B are non-empty" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          val actual   = xx.span(yy).canonical
          val expected = yy.span(xx).canonical

          actual.isEmpty shouldBe false
          expected.isEmpty shouldBe false

          actual shouldBe expected
        }
      }

      "[,] if A before B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A after B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty shouldBe false
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

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe false

            actual shouldBe expected
          }
        }
      }

      "[,] if A is-overlapped-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isOverlappedBy(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A is-met-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty shouldBe false
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

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
          }
        }
      }

      "[,] in A meets B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.meets(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] in A overlaps B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            val actual = xx.span(yy).canonical

            actual.isEmpty shouldBe false
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

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
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

          actual shouldBe expected
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

          actual shouldBe expected
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

        c1 shouldBe c2
        c2 shouldBe c1

        c1 shouldBe expected
      }
    }
  }
