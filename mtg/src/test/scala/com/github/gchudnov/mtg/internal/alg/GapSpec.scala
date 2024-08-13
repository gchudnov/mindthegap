package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.Domain
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

          xx.isEmpty shouldBe (true)
          yy.isEmpty shouldBe (true)

          val actual   = xx.gap(yy).canonical
          val expected = yy.gap(xx).canonical

          (actual.isEmpty, actual.isPoint, actual.isProper) shouldBe (expected.isEmpty, expected.isPoint, expected.isProper)

          actual shouldBe expected
        }
      }

      "any if A is empty" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          val actual   = xx.gap(yy).canonical
          val expected = yy.gap(xx).canonical

          (actual.isEmpty, actual.isPoint, actual.isProper) shouldBe (expected.isEmpty, expected.isPoint, expected.isProper)

          actual shouldBe expected
        }
      }

      "[,] if A and B are non-empty, disjoint disjoint !adjacent" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          whenever(xx.isDisjoint(yy) && !xx.isAdjacent(yy)) {
            val actual   = xx.gap(yy).canonical
            val expected = yy.gap(xx).canonical

            actual.nonEmpty shouldBe true
            expected.nonEmpty shouldBe true

            actual shouldBe expected
          }
        }
      }

      "∅ if A and B are non-empty and adjacent" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          whenever(xx.isAdjacent(yy)) {
            val actual   = xx.gap(yy).canonical
            val expected = yy.gap(xx).canonical

            actual.isEmpty shouldBe true
            expected.isEmpty shouldBe true

            actual shouldBe expected
          }
        }
      }

      "∅ if A and B are non-empty and !disjoint" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          whenever(!xx.isDisjoint(yy)) {
            val actual   = xx.gap(yy).canonical
            val expected = yy.gap(xx).canonical

            actual.isEmpty shouldBe true
            expected.isEmpty shouldBe true

            actual shouldBe expected
          }
        }
      }

      "[,] if A before B and !adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy) && !xx.isAdjacent(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "∅ if A before B and adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy) && xx.isAdjacent(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "[,] if A after B and !adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy) && !xx.isAdjacent(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A after B and adjacent" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy) && xx.isAdjacent(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ if A starts B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.starts(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ if A during B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.during(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ if A finishes B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.finishes(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
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

            actual.isEmpty shouldBe true

            actual shouldBe expected
          }
        }
      }

      "∅ if A is-overlapped-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isOverlappedBy(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ if A is-met-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ if A is-started-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ in A meets B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.meets(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ in A overlaps B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ in A is-finished-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isFinishedBy(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ if A contains B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            val actual = xx.gap(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ if A = (-inf, 0], B = (-inf, 0)" in {
        val a = Interval.rightClosed(0)
        val b = Interval.rightOpen(0)

        val actual   = a.gap(b).canonical
        val expected = Interval.make(Endpoint.at(Value.finite(0)), Endpoint.at(Value.infNeg)) // [0, -inf)

        actual.isEmpty shouldBe (true)
        actual shouldBe expected
      }

      "∅ if A = (-inf, inf), B = (-inf, inf)" in {
        val a = Interval.unbounded[Int]
        val b = Interval.unbounded[Int]

        val actual   = a.gap(b).canonical
        val expected = Interval.empty[Int] // (+inf, -inf)

        actual.isEmpty shouldBe (true)
        actual shouldBe expected
      }

      "[,] if disjoint [doc]" in {
        val a = Interval.closed(1, 4)
        val b = Interval.closed(7, 10)

        val actual   = a.gap(b).canonical
        val expected = Interval.closed(5, 6)

        actual shouldBe expected
      }

      "[,] if non-disjoint [doc]" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual = a.gap(b).canonical

        actual.isEmpty shouldBe true
      }
    }

    "A, B" should {
      "A ∥ B = B ∥ A" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.gap(yy).canonical
          val expected = yy.gap(xx).canonical

          actual shouldBe expected

          // (a & b).swap == (a ∥ b).inflate
          actual.inflate.canonical shouldBe xx.intersection(yy).swap.canonical
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

        c1 shouldBe c2
        c2 shouldBe c1

        c1 shouldBe expected
      }
    }
  }
