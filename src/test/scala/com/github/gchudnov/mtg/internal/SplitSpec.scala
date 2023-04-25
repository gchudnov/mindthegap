package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import org.scalacheck.Gen

final class SplitSpec extends TestSpec:

  given intRange: IntRange = intRange100
  given intProb: IntProb   = intProb028

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Split" when {
    "a series of intervals are split" should {
      "produce meeting intervals" in {
        forAll(Gen.choose(0, 15).flatMap(n => Gen.listOfN(n, genNonEmptyIntArgs))) { case (nArgs) =>
          val input = nArgs.map(args => Interval.make(args.left, args.right))

          val actual = Interval.split(input)

          println("INPUT:")
          println(input)

          println("ACTUAL:")
          println(actual)

          actual
            .combinations(2)
            .filter(ab => !ab.head.equalsTo(ab.last))
            .foreach(ab =>
              val a = ab.head
              val b = ab.last

              a.meets(b) mustBe (true)
            )
        }
      }
    }

    "no intervals" should {

      /**
       * {{{
       * }}}
       */
      "be split" in {
        val input = List.empty[Interval[Int]]

        val actual   = Interval.split(input)
        val expected = List.empty[Interval[Int]]

        val actualX   = Interval.splitFind(input)
        val expectedX = List.empty[(Interval[Int], Set[Int])]

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

    "one interval" should {

      /**
       * {{{
       *   [**********************************]   | [0,10] : a
       *   [**********************************]   | [0,10] : s0
       * --+----------------------------------+-- |
       *   0                                 10   |
       * }}}
       */
      "be split" in {
        val a = Interval.closed(0, 10)

        val s0 = a

        val input = List(a)

        val actual   = Interval.split(input)
        val expected = List(s0)

        val actualX   = Interval.splitFind(input)
        val expectedX = List((s0, Set(0)))

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

    "(-inf, +inf) interval" should {

      /**
       * {{{
       *   [******]                               | [0,10]  : a
       *     [********************************]   | [3,50]  : b
       *                 [******]                 | [20,30] : c
       * (**************************************) | (-∞,+∞) : d
       * (*]                                      | (-∞,0]  : s0
       *   [*]                                    | [0,3]   : s1
       *     [****]                               | [3,10]  : s2
       *          [******]                        | [10,20] : s3
       *                 [******]                 | [20,30] : s4
       *                        [*************]   | [30,50] : s5
       *                                      [*) | [50,+∞) : s6
       * +-+-+----+------+------+-------------+-+ |
       * -∞0 3   10     20     30            50+∞ |
       * }}}
       */
      "split with all other intervals" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)
        val d = Interval.unbounded[Int]

        val input = List(a, b, c, d)

        val s0 = Interval.rightClosed(0)
        val s1 = Interval.closed(0, 3)
        val s2 = Interval.closed(3, 10)
        val s3 = Interval.closed(10, 20)
        val s4 = Interval.closed(20, 30)
        val s5 = Interval.closed(30, 50)
        val s6 = Interval.leftClosed(50)

        val actual   = Interval.split(input)
        val expected = List(s0, s1, s2, s3, s4, s5, s6)

        val actualX   = Interval.splitFind(input)
        val expectedX = List((s0, Set(3)), (s1, Set(0, 3)), (s2, Set(0, 1, 3)), (s3, Set(1, 3)), (s4, Set(1, 2, 3)), (s5, Set(1, 3)), (s6, Set(3)))

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

    "empty and non-empty intervals" should {

      /**
       * {{{
       *   [**********************************]   | [0,10] : a
       *                                          | ∅      : b
       *   ????????????????????????????????????   |        : s0
       * --+----------------------------------+-- |
       *   0                                 10   |
       * }}}
       */
      "produce undefined results" in {
        val a = Interval.closed(0, 10)
        val b = Interval.empty[Int]

        val input = List(a, b)

        val actual  = Interval.split(input)
        val actualX = Interval.splitFind(input)

        actual.size >= 0 mustBe true
        actualX.size >= 0 mustBe true
      }
    }

    /**
     * {{{
     *   [***]                                  | [0,10]  : a
     *    [********************]                | [3,50]  : b
     *            [***]                         | [20,30] : c
     *                             [****]       | [60,70] : d
     *                                  [***]   | [71,80] : e
     *   **                                     | [0,3]   : s0
     *    [**]                                  | [3,10]  : s1
     *       [****]                             | [10,20] : s2
     *            [***]                         | [20,30] : s3
     *                [********]                | [30,50] : s4
     *                         [***]            | [50,60] : s5
     *                             [****]       | [60,70] : s6
     *                                  *       | [70,71] : s7
     *                                  [***]   | [71,80] : s8
     * --++--+----+---+--------+---+----+---+-- |
     *   0  10   20  30       50  60   70  80   |
     * }}}
     */
    "intervals where some of them are adjacent" should {
      "split adjacent into an interval without membership" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)

        val d = Interval.closed(60, 70)
        val e = Interval.closed(71, 80)

        val s0 = Interval.closed(0, 3)
        val s1 = Interval.closed(3, 10)
        val s2 = Interval.closed(10, 20)
        val s3 = Interval.closed(20, 30)
        val s4 = Interval.closed(30, 50)
        val s5 = Interval.closed(50, 60)
        val s6 = Interval.closed(60, 70)
        val s7 = Interval.closed(70, 71)
        val s8 = Interval.closed(71, 80)

        val g0 = Set(0)
        val g1 = Set(0, 1)
        val g2 = Set(1)
        val g3 = Set(1, 2)
        val g4 = Set(1)
        val g5 = Set()
        val g6 = Set(3)
        val g7 = Set()
        val g8 = Set(4)

        val input = List(a, b, c, d, e)

        val actualX   = Interval.splitFind(input)
        val expectedX = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4), (s5, g5), (s6, g6), (s7, g7), (s8, g8))

        val actual   = Interval.split(input)
        val expected = expectedX.map(_._1)

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

    /**
     * {{{
     *   [************************]             | [0,50]  : a
     *        [*********]                       | [10,30] : b
     *             [*******]                    | [20,35] : c
     *                       [**]               | [40,45] : d
     *                                 [****]   | [60,70] : e
     *   [****]                                 | [0,10]  : s0
     *        [****]                            | [10,20] : s1
     *             [****]                       | [20,30] : s2
     *                  [**]                    | [30,35] : s3
     *                     [*]                  | [35,40] : s4
     *                       [**]               | [40,45] : s5
     *                          [*]             | [45,50] : s6
     *                            [****]        | [50,60] : s7
     *                                 [****]   | [60,70] : s8
     * --+----+----+----+--+-+--+-+----+----+-- |
     *   0   10   20   30 35   45     60   70   |
     *
     * }}}
     */
    "several intervals" should {
      "produce a split" in {
        val a = Interval.closed(0, 50)  // [0, 50]
        val b = Interval.closed(10, 30) // [10, 30]
        val c = Interval.closed(20, 35) // [20, 35]
        val d = Interval.closed(40, 45) // [40, 45]
        val e = Interval.closed(60, 70) // [60, 70]

        val s0 = Interval.closed(0, 10)
        val g0 = Set(0)

        val s1 = Interval.closed(10, 20)
        val g1 = Set(0, 1)

        val s2 = Interval.closed(20, 30)
        val g2 = Set(0, 1, 2)

        val s3 = Interval.closed(30, 35)
        val g3 = Set(0, 2)

        val s4 = Interval.closed(35, 40)
        val g4 = Set(0)

        val s5 = Interval.closed(40, 45)
        val g5 = Set(0, 3)

        val s6 = Interval.closed(45, 50)
        val g6 = Set(0)

        val s7 = Interval.closed(50, 60)
        val g7 = Set.empty[Int]

        val s8 = Interval.closed(60, 70)
        val g8 = Set(4)

        val input = List(a, b, c, d, e)

        // [ ([0, 10], {0}), ([10, 20], {0, 1}), ([20, 30], {0, 1, 2}), ([30, 35], {0, 2}), ([35, 40], {0}), ([40, 45], {0, 3}), ([45, 50], {0}), ([50, 60], {}), ([60, 70], {4}) ]
        val actual   = Interval.splitFind(input)
        val expected = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4), (s5, g5), (s6, g6), (s7, g7), (s8, g8))

        actual.size mustBe 9
        actual mustBe expected

        val actualX   = Interval.split(input)
        val expectedX = expected.map(_._1)

        actualX mustBe expectedX
      }
    }

    /**
     * {{{
     *   [******]                               | [0,10]  : a
     *                 [******]                 | [20,30] : b
     *                               [******]   | [40,50] : c
     *   [******]                               | [0,10]  : s0
     *          [******]                        | [10,20] : s1
     *                 [******]                 | [20,30] : s2
     *                        [******]          | [30,40] : s3
     *                               [******]   | [40,50] : s4
     * --+------+------+------+------+------+-- |
     *   0     10     20     30     40     50   |
     * }}}
     */
    "non-intersecting intervals" should {
      "produce separate splits" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(20, 30)
        val c = Interval.closed(40, 50)

        val s0 = Interval.closed(0, 10)
        val s1 = Interval.closed(10, 20)
        val s2 = Interval.closed(20, 30)
        val s3 = Interval.closed(30, 40)
        val s4 = Interval.closed(40, 50)

        val g0 = Set(0)
        val g1 = Set()
        val g2 = Set(1)
        val g3 = Set()
        val g4 = Set(2)

        val input = List(a, b, c)

        val actualX   = Interval.splitFind(input)
        val expectedX = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4))

        val actual   = Interval.split(input)
        val expected = expectedX.map(_._1)

        actual mustBe expected
        actualX mustBe expectedX
      }
    }
  }
