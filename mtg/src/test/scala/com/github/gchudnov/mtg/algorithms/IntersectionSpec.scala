package com.github.gchudnov.mtg.algorithms

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Domain
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IntersectionSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordI: Ordering[Interval[Int]] = summon[Ordering[Interval[Int]]]

  "Intersection" when {

    "a.intersection(b)" should {

      "∅ if A and B are empty" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty shouldBe (true)
          yy.isEmpty shouldBe (true)

          val actual   = xx.intersection(yy).canonical
          val expected = yy.intersection(xx).canonical

          actual.isEmpty shouldBe true
          expected.isEmpty shouldBe true

          actual shouldBe expected
        }
      }

      "∅ if A is empty, B is non-empty" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          val actual   = xx.intersection(yy).canonical
          val expected = yy.intersection(xx).canonical

          actual.isEmpty shouldBe true
          expected.isEmpty shouldBe true

          actual shouldBe expected
        }
      }

      "∅ if A before B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "∅ if A after B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe true
          }
        }
      }

      "[,] if A starts B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.starts(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A during B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.during(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A finishes B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.finishes(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A equals B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.equalsTo(yy) && xx.nonEmpty && yy.nonEmpty) {
            val actual   = xx.intersection(yy).canonical
            val expected = xx.intersection(yy).canonical

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
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A is-met-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A is-started-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] in A meets B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.meets(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] in A overlaps B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] in A is-finished-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isFinishedBy(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] if A contains B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            val actual = xx.intersection(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "∅ if A = unbounded, B = empty" in {
        val a = Interval.unbounded[Int]
        val b = Interval.empty[Int]

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual.isEmpty shouldBe (true)
        actual shouldBe expected
      }

      "B if A = unbounded, B = non-empty" in {
        forAll(genNonEmptyIntArgs) { case (argsY) =>
          val xx = Interval.unbounded[Int]
          val yy = Interval.make(argsY.left, argsY.right)

          val actual = xx.intersection(yy).canonical

          actual.isEmpty shouldBe false
        }
      }

      "∅ if A and B non-empty and disjoint" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          whenever(xx.isDisjoint(yy)) {
            val actual   = xx.intersection(yy).canonical
            val expected = yy.intersection(xx).canonical

            actual.isEmpty shouldBe true
            expected.isEmpty shouldBe true

            actual shouldBe expected
          }
        }
      }

      "non-empty if A and B are non-empty and !disjoint" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          whenever(!xx.isDisjoint(yy)) {
            val actual   = xx.intersection(yy).canonical
            val expected = yy.intersection(xx).canonical

            actual.isEmpty shouldBe false
            expected.isEmpty shouldBe false

            actual shouldBe expected
          }
        }
      }
    }

    "A, B" should {
      "A & B = B & A" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.intersection(yy).canonical
          val expected = yy.intersection(xx).canonical

          actual shouldBe expected

          // (a & b).swap == (a ∥ b).inflate
          actual.swap.canonical shouldBe xx.gap(yy).inflate.canonical
        }
      }

      "(2, +∞) & (3, 5) = (3, 5) & (2, +∞)" in {
        val a = Interval.leftOpen(2)  // (2, +∞]
        val b = Interval.closed(3, 5) // (3, 5)

        val c1 = a.intersection(b).canonical
        val c2 = b.intersection(a).canonical

        c1 shouldBe c2
        c2 shouldBe c1
      }
    }

    "A, B, C" should {
      "(A & B) & C = A & (B & C)" in {
        forAll(genAnyIntArgs, genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY, argsZ) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)
          val zz = Interval.make(argsZ.left, argsZ.right)

          val actual   = ((xx.intersection(yy)).intersection(zz)).canonical
          val expected = xx.intersection(yy.intersection(zz)).canonical

          actual shouldBe expected
        }
      }
    }

    "Interval" should {
      "Interval.intersection(a, b)" in {
        val a = Interval.rightClosed(2) // (-∞, 2]
        val b = Interval.closed(1, 10)  // [1, 10]

        val expected = Interval.closed(1, 2) // [1, 2]

        val c1 = Interval.intersection(a, b).canonical
        val c2 = Interval.intersection(b, a).canonical

        c1 shouldBe c2
        c2 shouldBe c1

        c1 shouldBe expected
      }
    }
  }
