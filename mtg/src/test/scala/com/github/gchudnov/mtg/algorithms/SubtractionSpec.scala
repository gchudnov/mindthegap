package com.github.gchudnov.mtg.algorithms

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration

final class SubtractionSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "Subtraction" when {
    "a.minus(b)" should {

      "∅ if A and B are empty" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty shouldBe (true)
          yy.isEmpty shouldBe (true)

          val actual = xx.minus(yy).canonical

          actual.isEmpty shouldBe true
        }
      }

      "∅ if A is empty" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty shouldBe (true)
          yy.nonEmpty shouldBe (true)

          val actual = xx.minus(yy).canonical

          actual.isEmpty shouldBe true
        }
      }

      "A if B is empty" in {
        forAll(genNonEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty shouldBe (true)
          yy.isEmpty shouldBe (true)

          val actual   = xx.minus(yy).canonical
          val expected = xx.canonical

          actual.isEmpty shouldBe false
          actual shouldBe (expected)
        }
      }

      "[,] if A before B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.before(yy)) {
            val actual   = xx.minus(yy).canonical
            val expected = xx.canonical

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe true
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe true
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe true
            actual shouldBe (expected)
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

            actual.isEmpty shouldBe true
            actual shouldBe expected
          }
        }
      }

      "[,] if A is-overlapped-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isOverlappedBy(yy)) {
            val actual = xx.minus(yy).canonical

            actual.isEmpty shouldBe false
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

            actual.isEmpty shouldBe false
            actual shouldBe expected
          }
        }
      }

      "[,] if A is-started-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isStartedBy(yy)) {
            val actual = xx.minus(yy).canonical

            actual.isEmpty shouldBe false
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

            actual.isEmpty shouldBe false
            actual shouldBe (expected)
          }
        }
      }

      "[,] in A overlaps B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy)) {
            val actual = xx.minus(yy).canonical

            actual.isEmpty shouldBe false
          }
        }
      }

      "[,] in A is-finished-by B" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isFinishedBy(yy)) {
            val actual = xx.minus(yy).canonical

            actual.isEmpty shouldBe false
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

        actual shouldBe expected
      }

      "[,] if minus right [doc]" in {
        val a = Interval.closed(5, 15) // [5, 15]
        val b = Interval.closed(1, 10) // [1, 10]

        val actual = a.minus(b).canonical // [11, 15]

        val expected = Interval.closed(11, 15)

        actual shouldBe expected
      }
    }

    "Interval.difference(a, b)" should {
      "check" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy)) {
            val actual = Interval.difference(xx, yy)

            actual.size shouldBe (2)
            actual.foreach(_.nonEmpty shouldBe (true))

            // [left][intersection][right] = [original]
            val ix = xx.intersection(yy)
            val zz = actual(0).union(ix).union(actual(1)).canonical
            zz shouldBe (xx.canonical)
          }
        }
      }

      "[,] if a.contains(b) [doc]" in {
        val a = Interval.closed(1, 15) // [1, 15]
        val b = Interval.closed(5, 10) // [5, 10]

        // val c = a.minus(b)           // throws UnsupportedOperationException
        val cs = Interval.difference(a, b) // [[1, 4], [11, 15]]

        val actual   = cs.map(_.canonical)
        val expected = List(Interval.closed(1, 4), Interval.closed(11, 15))

        //
        actual shouldBe expected
      }
    }

    "Interval.differenceSymmetric(a, b)" should {
      "check if overlap" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.overlaps(yy) || yy.overlaps(xx)) {
            val actual = Interval.differenceSymmetric(xx, yy)

            actual.size shouldBe (2)
            actual.foreach(_.nonEmpty shouldBe (true))

            // (1) The symmetric difference is equivalent to the union of both relative complements: A Δ B = (A \ B) ∪ (B \ A).
            val XXdiffYY = Interval.difference(xx, yy)
            val YYdiffXX = Interval.difference(yy, xx)
            val expected = XXdiffYY ++ YYdiffXX
            actual.sorted shouldBe expected.sorted

            // (2) The symmetric difference is equivalent to the union of two intervals minus their intersection: A Δ B = (A ∪ B) \ (A ∩ B).
            val union     = xx.union(yy)
            val intersect = xx.intersection(yy)

            val expected2 = Interval.difference(union, intersect)
            actual.sorted shouldBe expected2.sorted
          }
        }
      }

      /**
       * Commutativity: symmetricDifference(a, b) == symmetricDifference(b, a)
       * 
       * The symmetric difference is commutative, meaning that the order of the intervals does not affect the result.
       */
      "check commutativity: A Δ B = B Δ A" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual1 = Interval.differenceSymmetric(xx, yy)
          val actual2 = Interval.differenceSymmetric(yy, xx)

          actual1.sorted shouldBe actual2.sorted
        }
      }

      // "check associative property: A Δ (B Δ C) = (A Δ B) Δ C" in {
      //   forAll(genNonEmptyIntArgs, genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY, argsZ) =>
      //     val xx = Interval.make(argsX.left, argsX.right)
      //     val yy = Interval.make(argsY.left, argsY.right)
      //     val zz = Interval.make(argsZ.left, argsZ.right)

      //     val actual1 = Interval.differenceSymmetric(yy, zz).flatMap(Interval.differenceSymmetric(xx, _)).map(_.canonical).distinct
      //     val actual2 = Interval.differenceSymmetric(xx, yy).flatMap(Interval.differenceSymmetric(_, zz)).map(_.canonical).distinct

      //     actual1.sorted shouldBe actual2.sorted
      //   }
      // }

      /**
       * Symmetric Difference with Empty: symmetricDifference(a, Interval.empty) == List(a)
       */
      "the empty interval is neutral: A Δ ∅ = A" in {
        forAll(genNonEmptyIntArgs) { argsX =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.empty[Int]

          val actual1 = Interval.differenceSymmetric(xx, yy) // A Δ ∅ = A
          val actual2 = Interval.differenceSymmetric(yy, xx) // ∅ Δ A = A

          actual1.size shouldBe (1)
          actual1.head shouldBe (xx)

          actual2.size shouldBe (1)
          actual2.head shouldBe (xx)
        }
      }

      /**
       * Idempotence: symmetricDifference(a, a) == Interval.empty
       */
      "check idempotence -- every interval is its own inverse: A Δ A = ∅" in {
        forAll(genNonEmptyIntArgs) { argsX =>
          val xx = Interval.make(argsX.left, argsX.right)

          // return an empty list, indicating no remaining intervals after the symmetric difference operation.
          val actual = Interval.differenceSymmetric(xx, xx)

          actual.size shouldBe (0)
          actual shouldBe List.empty[Interval[Int]]
        }
      }

      /**
        * Disjoint Intervals: If a and b are disjoint, symmetricDifference(a, b) == List(a, b)
        */
      "check disjoint intervals" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.isDisjoint(yy)) {
            val actual = Interval.differenceSymmetric(xx, yy)

            actual.size shouldBe (2)
            actual.foreach(_.nonEmpty shouldBe (true))

            val expected = List(xx, yy)
            actual.sorted shouldBe expected.sorted
          }
        }
      }

      /**
       * Containment: If a.contains(b), symmetricDifference(a, b) returns the parts of a that aren't covered by b.
       */
      "check containment" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.contains(yy) || yy.contains(xx)) {
            val actual = Interval.differenceSymmetric(xx, yy)

            actual.size shouldBe (2)
            actual.foreach(_.nonEmpty shouldBe (true))

            val (qq, ww) = if xx.contains(yy) then (xx, yy) else (yy, xx)
            val expected = Interval.difference(qq, ww)

            actual.sorted shouldBe expected.sorted
          }
        }
      }

      "check selected use-cases" in {
        val t = Table(
          ("a", "b", "cs"),
          (Interval.closed(1, 5), Interval.closed(3, 7), List(Interval.closed(1, 2), Interval.closed(6, 7))),
          (Interval.open(-3, 3), Interval.point(2), List(Interval.closed(-2, 1))),
          (Interval.closed(1, 2), Interval.closed(3, 4), List(Interval.closed(1, 2), Interval.closed(3, 4))),
          (Interval.closed(4, 5), Interval.point(-3), List(Interval.closed(4, 5), Interval.point(-3))),
          (Interval.closed(3, 5), Interval.empty[Int], List(Interval.closed(3, 5))),
          (Interval.empty[Int], Interval.closed(3, 5), List(Interval.closed(3, 5))),
        )

        forAll(t) { (a, b, expected) =>
          val actual = Interval.differenceSymmetric(a, b).map(_.canonical)
          actual shouldBe expected
        }
      }
    }

    /**
     * difference
     *
     * Given a set of intervals A and an interval B, returns a collection of intervals that are the result of subtracting B from A.
     */
    "subtractAll" should {
      "[[0,2],[3,4],[5,7]] - [1,6] = [[0,1],[6,7]]" in {
        given domD: Domain[Double] = Domain.makeFractional(0.1)

        val xs = List(
          Interval.leftClosedRightOpen(0.0, 2.0),
          Interval.leftClosedRightOpen(3.0, 4.0),
          Interval.leftClosedRightOpen(5.0, 7.0),
        )

        val y = Interval.leftClosedRightOpen(1.0, 6.0)

        val actual = subtractAll(xs, y)

        // [[0,1],[6,7]]
        val expected = List(
          Interval.leftClosedRightOpen(0.0, 1.0),
          Interval.leftClosedRightOpen(6.0, 7.0),
        )

        actual.map(_.canonical) shouldBe expected.map(_.canonical)
      }

      "[[0,5]] - [2,3] = [[0,2],[3,5]]" in {
        given domD: Domain[Double] = Domain.makeFractional(0.1)

        val xs = List(
          Interval.leftClosedRightOpen(0.0, 5.0)
        )

        val y = Interval.leftClosedRightOpen(2.0, 3.0)

        val actual = subtractAll(xs, y)

        // [[0,2],[3,5]]
        val expected = List(
          Interval.leftClosedRightOpen(0.0, 2.0),
          Interval.leftClosedRightOpen(3.0, 5.0),
        )

        actual.map(_.canonical) shouldBe expected.map(_.canonical)
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

        val actual = subtractAll(xs, y)

        val z1 = Interval.leftClosedRightOpen(-5.0, -4.0)
        val z2 = Interval.leftClosedRightOpen(-3.0, -2.0)
        val z3 = Interval.leftClosedRightOpen(4.0, 5.0)
        val z4 = Interval.leftClosedRightOpen(8.0, 9.0)

        // [[-5,-4],[-3,-2],[4,5],[8,9]]
        val expected = List(z1, z2, z3, z4)

        actual.map(_.canonical) shouldBe expected.map(_.canonical)
      }
    }
  }

  final def subtractAll[T: Domain](xs: Iterable[Interval[T]], y: Interval[T]): List[Interval[T]] =
    xs.foldLeft(List.empty[Interval[T]]) { (acc, x) =>
      acc ++ Interval.difference(x, y).filter(_.nonEmpty)
    }
