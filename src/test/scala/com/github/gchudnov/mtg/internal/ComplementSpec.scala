package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import org.scalacheck.Gen

final class ComplementSpec extends TestSpec:

  given intRange: IntRange = intRange100
  given intProb: IntProb   = intProb028

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Complement" when {
    "complement for a series of intervals is calculated" should {
      "produce only disjoint intervals" in {
        forAll(Gen.choose(0, 15).flatMap(n => Gen.listOfN(n, genNonEmptyIntArgs))) { case (nArgs) =>
          val input = nArgs.map(args => Interval.make(args.left, args.right))

          val actual = Interval.complement(input)

          actual
            .combinations(2)
            .foreach(ab =>
              val a = ab.head
              val b = ab.last

              a.isDisjoint(b) mustBe (true)
            )
        }
      }

      "complement(complement([x1, x2, ... xn])) == group([x1, x2, ... xn])" in {
        forAll(Gen.choose(0, 15).flatMap(n => Gen.listOfN(n, genNonEmptyIntArgs))) { case (nArgs) =>
          val input = nArgs.map(args => Interval.make(args.left, args.right))

          val actual = Interval.complement(Interval.complement(input)).map(_.canonical)
          val expected = Interval.group(input).map(_.canonical)

          actual mustBe expected
        }
      }
    }

    "two points" should {
      "produce a complement" in {
        val a = Interval.point(79)
        val b = Interval.point(51)

        val e0 = Interval.rightClosed(50)
        val e1 = Interval.closed(52, 78)
        val e2 = Interval.leftClosed(80)

        val input = List(a, b)

        val actual = Interval.complement(input)
        val expected = List(e0, e1, e2)

        actual mustBe expected
      }
    }

    "one unbounded interval" should {
      "produce no complements" in {
        val a = Interval.unbounded[Int]

        val input = List(a)

        val actual = Interval.complement(input)
        val expected = List.empty[Interval[Int]]

        actual mustBe expected
      }
    }

    "two points -inf and +inf" should {
      "produce an unbounded interval" in {
        val a = Interval.point[Int](Value.infNeg)
        val b = Interval.point[Int](Value.infPos)

        val e0 = Interval.unbounded[Int]

        val input = List(a, b)

        val actual = Interval.complement(input)
        val expected = List(e0)

        actual mustBe expected
      }
    }

    /**
      * {{{
      *      [*******]                             | [0,10]  : a
      *          [************]                    | [5,20]  : b
      *                           [***]            | [25,30] : c
      *                                   [***]    | [35,40] : d
      *   (*]                                      | (-∞,-1] : e0
      *                       [**]                 | [21,24] : e1
      *                                [*]         | [31,34] : e2
      *                                        [*) | [41,+∞) : e3
      *   +-++---+---+--------+--++---++-++---++-+ |
      *   -∞ 0   5  10       20 24   30 34   40 +∞ |
      * }}}
      */
    "several intervals with gaps in between" should {
      "produce the complement" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(5, 20)
        val c = Interval.closed(25, 30)
        val d = Interval.closed(35, 40)

        val e0 = Interval.rightClosed(-1)
        val e1 = Interval.closed(21, 24)
        val e2 = Interval.closed(31, 34)
        val e3 = Interval.leftClosed(41)

        val input = List(a, b, c, d)

        val actual   = Interval.complement(input)
        val expected = List(e0, e1, e2, e3)

        actual mustBe expected
      }
    }

    "complement [doc]" should {
      "be expected" in {
        val a = Interval.closed(0, 10)  // [0, 10]
        val b = Interval.closed(5, 20)  // [5, 20]
        val c = Interval.closed(25, 30) // [25, 30]
        val d = Interval.closed(35, 40) // [35, 40]

        val e0 = Interval.rightClosed(-1) // (-∞, -1]
        val e1 = Interval.closed(21, 24)  // [21, 24]
        val e2 = Interval.closed(31, 34)  // [31, 34]
        val e3 = Interval.leftClosed(41)  // [41, +∞)

        val input = List(a, b, c, d)

        val is   = Interval.complement(input) // [ (-∞, -1], [21, 24], [31, 34], [41, +∞) ]

        val actual = is
        val expected = List(e0, e1, e2, e3)

        actual mustBe expected

        import com.github.gchudnov.mtg.Diagram.Canvas
        import com.github.gchudnov.mtg.Diagram.View
        import com.github.gchudnov.mtg.Diagram

        val canvas: Canvas  = Canvas.make(40, 2)
        val view: View[Int] = View.default[Int]
        val diagram = Diagram.make(List(a, b, c, d, e0, e1, e2, e3), view, canvas)

        val diag = Diagram.render(diagram)

        println(diag)
      }
    }
  }
