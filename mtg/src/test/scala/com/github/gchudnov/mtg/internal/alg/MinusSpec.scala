package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Endpoint
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class MinusSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordMark

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

          xx.nonEmpty mustBe (true)
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

        //
        actual mustBe expected
      }
    }

    /**
     * MinusAll
     *
     * Given a set of intervals A and an interval B, returns a collection of intervals that are the result of subtracting B from A.
     */
    "minusAll" should {
      "[[0,2],[3,4],[5,7]] - [1,6] = [[0,1],[6,7]]" in {
        given domD: Domain[Double] = Domain.makeFractional(0.1)

        val xs = List(
          Interval.leftClosedRightOpen(0.0, 2.0),
          Interval.leftClosedRightOpen(3.0, 4.0),
          Interval.leftClosedRightOpen(5.0, 7.0),
        )

        val y = Interval.leftClosedRightOpen(1.0, 6.0)

        val actual = minusAll(xs, y)

        // [[0,1],[6,7]]
        val expected = List(
          Interval.leftClosedRightOpen(0.0, 1.0),
          Interval.leftClosedRightOpen(6.0, 7.0),
        )

        actual.map(_.canonical) mustBe expected.map(_.canonical)
      }

      "[[0,5]] - [2,3] = [[0,2],[3,5]]" in {
        given domD: Domain[Double] = Domain.makeFractional(0.1)

        val xs = List(
          Interval.leftClosedRightOpen(0.0, 5.0)
        )

        val y = Interval.leftClosedRightOpen(2.0, 3.0)

        val actual = minusAll(xs, y)

        // [[0,2],[3,5]]
        val expected = List(
          Interval.leftClosedRightOpen(0.0, 2.0),
          Interval.leftClosedRightOpen(3.0, 5.0),
        )

        actual.map(_.canonical) mustBe expected.map(_.canonical)
      }

      /**
       * {{{
       * [[-5,-4],[-3,-2],[1,2],[3,5],[8,9]]
       * -
       * [-1,4]
       * =
       * [[-5,-4],[-3,-2],[4,5],[8,9]]
       *
       *   [**)                                   | [-5.0,-4.0) : x1
       *        [**)                              | [-3.0,-2.0) : x2
       *                  [*)                     | [1.0,2.0)   : x3
       *                       [****)             | [3.0,5.0)   : x4
       *                                    [*)   | [8.0,9.0)   : x5
       *             [***********)                | [-1.0,4.0)  : y
       *   [**)                                   | [-5.0,-4.0) : z1
       *        [**)                              | [-3.0,-2.0) : z2
       *                         [**)             | [4.0,5.0)   : z3
       *                                    [*)   | [8.0,9.0)   : z4
       * --+--+-+--+-+----+-+--+-+--+-------+-+-- |
       * -5.0 -3.0 -1.0  1.0  3.0  5.0     8.0    |
       * }}}
       */
      "[[-5,-4],[-3,-2],[1,2],[3,5],[8,9]] - [-1,4] = [[-5,-4],[-3,-2],[4,5],[8,9]]" in {
        given domD: Domain[Double] = Domain.makeFractional(0.1)

        val x1 = Interval.leftClosedRightOpen(-5.0, -4.0)
        val x2 = Interval.leftClosedRightOpen(-3.0, -2.0)
        val x3 = Interval.leftClosedRightOpen(1.0, 2.0)
        val x4 = Interval.leftClosedRightOpen(3.0, 5.0)
        val x5 = Interval.leftClosedRightOpen(8.0, 9.0)

        val xs = List(x1, x2, x3, x4, x5)
        val y  = Interval.leftClosedRightOpen(-1.0, 4.0)

        val actual = minusAll(xs, y)

        val z1 = Interval.leftClosedRightOpen(-5.0, -4.0)
        val z2 = Interval.leftClosedRightOpen(-3.0, -2.0)
        val z3 = Interval.leftClosedRightOpen(4.0, 5.0)
        val z4 = Interval.leftClosedRightOpen(8.0, 9.0)

        // [[-5,-4],[-3,-2],[4,5],[8,9]]
        val expected = List(z1, z2, z3, z4)

        actual.map(_.canonical) mustBe expected.map(_.canonical)
      }
    }
  }

  final def minusAll[T: Domain](xs: Iterable[Interval[T]], y: Interval[T]): List[Interval[T]] =
    xs.foldLeft(List.empty[Interval[T]]) { (acc, x) =>
      acc ++ Interval.minus(x, y).filter(_.nonEmpty)
    }
