package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Domain
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class UnionSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Union" when {

    "a.union(b)" should {

      "any if A and B are empty" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty shouldBe (true)
          yy.isEmpty shouldBe (true)

          val actual   = xx.union(yy).canonical
          val expected = yy.union(xx).canonical

          (actual.isEmpty, actual.isPoint, actual.isProper) shouldBe (expected.isEmpty, expected.isPoint, expected.isProper)

          actual shouldBe expected
        }
      }

      "B if A is empty" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          val actual   = xx.union(yy).canonical
          val expected = yy.union(xx).canonical

          actual.isEmpty shouldBe false
          expected.isEmpty shouldBe false

          actual shouldBe expected
        }
      }

      "[,] if A and B are non-empty and merges" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          whenever(xx.merges(yy)) {
            val actual   = xx.union(yy).canonical
            val expected = yy.union(xx).canonical

            actual.isEmpty shouldBe false
            expected.isEmpty shouldBe false

            actual shouldBe expected
          }
        }
      }

      "∅ if A and B are non-empty and !merges" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          whenever(!xx.merges(yy)) {
            val actual   = xx.union(yy).canonical
            val expected = yy.union(xx).canonical

            actual.isEmpty shouldBe true
            expected.isEmpty shouldBe true

            actual shouldBe expected
          }
        }
      }

      "∅ if A before B and !adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy) && !xx.isAdjacent(yy)) {
            val actual = xx.union(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "[,] if A before B and adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy) && xx.isAdjacent(yy)) {
            val actual = xx.union(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "∅ if A after B and !adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy) && !xx.isAdjacent(yy)) {
            val actual = xx.union(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "[,] if A after B and adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy) && xx.isAdjacent(yy)) {
            val actual = xx.union(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A starts B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.starts(yy)) {
            val actual   = xx.union(yy).canonical
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
            val actual   = xx.union(yy).canonical
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
            val actual   = xx.union(yy).canonical
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
            val actual   = xx.union(yy).canonical
            val expected = xx.union(yy).canonical

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
            val actual = xx.union(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A is-met-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            val actual = xx.union(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A is-started-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            val actual   = xx.union(yy).canonical
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
            val actual = xx.union(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] in A overlaps B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            val actual = xx.union(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] in A is-finished-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isFinishedBy(yy)) {
            val actual   = xx.union(yy).canonical
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
            val actual   = xx.union(yy).canonical
            val expected = xx.canonical

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
          }
        }
      }
    }

    "A, B" should {
      "A U B = B U A" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.union(yy).canonical
          val expected = yy.union(xx).canonical

          actual shouldBe expected
        }
      }

      "[-2, -5] U [20, 10] = [20, 10] U [-2, -5]" in {
        val a = Interval.closed(-5, -2).swap
        val b = Interval.closed(10, 20).swap

        a.isEmpty shouldBe (true)
        b.isEmpty shouldBe (true)

        val actual   = a.union(b).canonical
        val expected = b.union(a).canonical

        actual shouldBe expected
      }
    }

    "Interval" should {
      "Interval.union(a, b)" in {
        val a = Interval.closed(1, 5)  // [1, 5]
        val b = Interval.closed(3, 10) // [7, 10]

        val expected = Interval.closed(1, 10) // [1, 10]

        val c1 = Interval.union(a, b).canonical
        val c2 = Interval.union(b, a).canonical

        c1 shouldBe c2
        c2 shouldBe c1

        c1 shouldBe expected
      }
    }
  }
