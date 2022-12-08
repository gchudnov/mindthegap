package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class MinusSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Mark[Int]] = summon[Ordering[Mark[Int]]]

  "Minus" when {
    "a.minus(b)" should {

      "∅ if A and B are empty" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.isEmpty mustBe (true)

          val actual = xx.minus(yy).canonical

          actual.isEmpty mustBe true
        }
      }

      "∅ if A is empty" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          val actual = xx.minus(yy).canonical

          actual.isEmpty mustBe true
        }
      }

      "A if B is empty" in {
        forAll(genNonEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (false)
          yy.isEmpty mustBe (true)

          val actual   = xx.minus(yy).canonical
          val expected = xx.canonical

          actual.isEmpty mustBe false
          actual mustBe (expected)
        }
      }

      "[,] if A before B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy)) {
            val actual   = xx.minus(yy).canonical
            val expected = xx.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }

      "[,] if A after B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.after(yy)) {
            val actual   = xx.minus(yy).canonical
            val expected = xx.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }

      "∅ if A starts B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.starts(yy)) {
            val actual   = xx.minus(yy).canonical
            val expected = Interval.empty[Int]

            actual.isEmpty mustBe true
            actual mustBe (expected)
          }
        }
      }

      "∅ if A during B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.during(yy)) {
            val actual   = xx.minus(yy).canonical
            val expected = Interval.empty[Int]

            actual.isEmpty mustBe true
            actual mustBe (expected)
          }
        }
      }

      "∅ if A finishes B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.finishes(yy)) {
            val actual   = xx.minus(yy).canonical
            val expected = Interval.empty[Int]

            actual.isEmpty mustBe true
            actual mustBe (expected)
          }
        }
      }

      "∅ if A equals B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.equalsTo(yy) && xx.nonEmpty && yy.nonEmpty) {
            val actual   = xx.minus(yy).canonical
            val expected = xx.minus(yy).canonical

            actual.isEmpty mustBe true
            actual mustBe expected
          }
        }
      }

      "[,] if A is-overlapped-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isOverlappedBy(yy)) {
            val actual = xx.minus(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] if A is-met-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isMetBy(yy)) {
            val actual   = xx.minus(yy).canonical
            val expected = xx.deflateLeft.canonical

            actual.isEmpty mustBe false
            actual mustBe expected
          }
        }
      }

      "[,] if A is-started-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            val actual = xx.minus(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] in A meets B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.meets(yy)) {
            val actual   = xx.minus(yy).canonical
            val expected = xx.deflateRight.canonical

            actual.isEmpty mustBe false
            actual mustBe (expected)
          }
        }
      }

      "[,] in A overlaps B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            val actual = xx.minus(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "[,] in A is-finished-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isFinishedBy(yy)) {
            val actual = xx.minus(yy).canonical

            actual.isEmpty mustBe false
          }
        }
      }

      "undefined if A contains B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            assertThrows[UnsupportedOperationException] {
              xx.minus(yy)
            }
          }
        }
      }

      "[,] if minus left [doc]" in {
        val a = Interval.closed(1, 10) // [1, 10]
        val b = Interval.closed(5, 15) // [5, 15]

        val actual = a.minus(b).canonical // [1, 4]

        val expected = Interval.closed(1, 4)

        actual mustBe expected
      }

      "[,] if minus right [doc]" in {
        val a = Interval.closed(5, 15) // [5, 15]
        val b = Interval.closed(1, 10) // [1, 10]

        val actual = a.minus(b).canonical // [11, 15]

        val expected = Interval.closed(11, 15)

        actual mustBe expected
      }
    }

    "Interval" should {
      "Interval.minus(a, b)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            val actual = Interval.minus(xx, yy)

            actual.size mustBe (2)
            actual.foreach(_.nonEmpty mustBe (true))

            // [left][intersection][right] = [original]
            val ix = xx.intersection(yy)
            val zz = actual(0).union(ix).union(actual(1)).canonical
            zz mustBe (xx.canonical)
          }
        }
      }

      "[,] if a.contains(b) [doc]" in {
        val a = Interval.closed(1, 15) // [1, 15]
        val b = Interval.closed(5, 10) // [5, 10]

        // val c = a.minus(b)           // throws UnsupportedOperationException
        val cs = Interval.minus(a, b) // [[1, 4], [11, 15]]

        val actual   = cs.map(_.canonical)
        val expected = List(Interval.closed(1, 4), Interval.closed(11, 15))
      }
    }
  }
