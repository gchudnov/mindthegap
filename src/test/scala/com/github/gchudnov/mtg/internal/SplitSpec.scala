package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import org.scalacheck.Gen

final class SplitSpec extends TestSpec:

  given intRange: IntRange = intRange100
  given intProb: IntProb   = intProb028

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Mark[Int]] = summon[Ordering[Mark[Int]]]

  "Split" when {
    "a series of intervals are split" should {
      "produce intervals where a.right = b.left" in {
        forAll(Gen.choose(0, 15).flatMap(n => Gen.listOfN(n, genNonEmptyIntArgs))) { case (nArgs) =>
          val input = nArgs.map(args => Interval.make(args.left, args.right))

          val actual = Interval.split(input)

          if actual.size <= 1 then input mustBe actual
          else
            actual
              .zip(actual.tail)
              .foreach(ab =>
                val a = ab.head
                val b = ab.last

                ordM.equiv(a.right.succ, b.left) mustBe true
              )
        }
      }
    }

    "no intervals" should {

      /**
       * {{{
       *   no-data
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
       *    [*****]                               | [0,10]  : a
       *      [******************************]    | [3,50]  : b
       *                 [******]                 | [20,30] : c
       * (**************************************) | (-∞,+∞) : d
       * (*]                                      | (-∞,-1] : s0
       *    **                                    | [0,2]   : s1
       *      [***]                               | [3,10]  : s2
       *           [****]                         | [11,19] : s3
       *                 [******]                 | [20,30] : s4
       *                         [***********]    | [31,50] : s5
       *                                      [*) | [51,+∞) : s6
       * +-++++---++----++------++-----------++-+ |
       * -∞ 0 3  10    19      30           50 +∞ |
       * }}}
       */
      "split with all other intervals" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)
        val d = Interval.unbounded[Int]

        val input = List(a, b, c, d)

        val s0 = Interval.rightClosed(-1)
        val s1 = Interval.closed(0, 2)
        val s2 = Interval.closed(3, 10)
        val s3 = Interval.closed(11, 19)
        val s4 = Interval.closed(20, 30)
        val s5 = Interval.closed(31, 50)
        val s6 = Interval.leftClosed(51)

        val g0 = Set(3)
        val g1 = Set(0, 3)
        val g2 = Set(0, 1, 3)
        val g3 = Set(1, 3)
        val g4 = Set(1, 2, 3)
        val g5 = Set(1, 3)
        val g6 = Set(3)

        val expectedX = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4), (s5, g5), (s6, g6))
        val expected  = expectedX.map(_._1)

        val actualX = Interval.splitFind(input).map(it => (it._1.canonical, it._2))
        val actual  = Interval.split(input).map(_.canonical)

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
     *   **                                     | [0,2]   : s0
     *    [**]                                  | [3,10]  : s1
     *        [**]                              | [11,19] : s2
     *            [***]                         | [20,30] : s3
     *                 [*******]                | [31,50] : s4
     *                         [***]            | [51,59] : s5
     *                             [****]       | [60,70] : s6
     *                                  [***]   | [71,80] : s7
     * --++--++--++---++-------+---+----+---+-- |
     *   0  10  19   30       50  60   70  80   |
     * }}}
     */
    "some of intervals are adjacent" should {
      "split doesn't exist for an adjacent intervals" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)

        val d = Interval.closed(60, 70)
        val e = Interval.closed(71, 80)

        val s0 = Interval.closed(0, 2)
        val s1 = Interval.closed(3, 10)
        val s2 = Interval.closed(11, 19)
        val s3 = Interval.closed(20, 30)
        val s4 = Interval.closed(31, 50)
        val s5 = Interval.closed(51, 59)
        val s6 = Interval.closed(60, 70)
        val s7 = Interval.closed(71, 80)

        val g0 = Set(0)
        val g1 = Set(0, 1)
        val g2 = Set(1)
        val g3 = Set(1, 2)
        val g4 = Set(1)
        val g5 = Set()
        val g6 = Set(3)
        val g7 = Set(4)

        val input = List(a, b, c, d, e)

        val expectedX = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4), (s5, g5), (s6, g6), (s7, g7))
        val expected  = expectedX.map(_._1)

        val actualX = Interval.splitFind(input).map(it => (it._1.canonical, it._2))
        val actual  = Interval.split(input).map(_.canonical)

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

    "equal intervals" should {

      /**
       * {{{
       *   [**********************************]   | [10,20] : a
       *   [**********************************]   | [10,20] : b
       *   [**********************************]   | [10,20] : c
       *   [**********************************]   | [10,20] : s0
       * --+----------------------------------+-- |
       *  10                                 20   |
       * }}}
       */
      "produce one split" in {
        val a = Interval.closed(10, 20)
        val b = Interval.closed(10, 20)
        val c = Interval.closed(10, 20)

        val s0 = Interval.closed(10, 20)
        val g0 = Set(0, 1, 2)

        val input = List(a, b, c)

        val actualX   = Interval.splitFind(input)
        val expectedX = List((s0, g0))

        val actual   = Interval.split(input)
        val expected = expectedX.map(_._1)

        actual mustBe expected
        actualX mustBe expectedX
      }

      /**
       * {{{
       *                     *                    | {10} : a
       *                     *                    | {10} : b
       *                     *                    | {10} : c
       *                     *                    | {10} : s0
       * --------------------+------------------- |
       *                    10                    |
       * }}}
       */
      "produce one split for a point" in {
        val a = Interval.point(10)
        val b = Interval.point(10)
        val c = Interval.point(10)

        val s0 = Interval.point(10)
        val g0 = Set(0, 1, 2)

        val input = List(a, b, c)

        val actualX   = Interval.splitFind(input)
        val expectedX = List((s0, g0))

        val actual   = Interval.split(input)
        val expected = expectedX.map(_._1)

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

    "several intervals meet" should {

      /**
       * {{{
       *   [*****************]                    | [60,70] : a
       *                     [****************]   | [70,80] : b
       *   [***************]                      | [60,69] : s0
       *                     *                    | {70}    : s1
       *                      [***************]   | [71,80] : s2
       * --+---------------+-++---------------+-- |
       *  60              69 71              80   |
       * }}}
       */
      "split meeting intervals" in {
        val a = Interval.closed(60, 70)
        val b = Interval.closed(70, 80)

        val s0 = Interval.closed(60, 69)
        val s1 = Interval.point(70)
        val s2 = Interval.closed(71, 80)

        val g0 = Set(0)
        val g1 = Set(0, 1)
        val g2 = Set(1)

        val input = List(a, b)

        val expectedX = List((s0, g0), (s1, g1), (s2, g2))
        val expected  = expectedX.map(_._1)

        val actualX = Interval.splitFind(input).map(it => (it._1.canonical, it._2))
        val actual  = Interval.split(input).map(_.canonical)

        actual mustBe expected
        actualX mustBe expectedX
      }

      /**
       * {{{
       *   [*****************]                    | [60,70] : a
       *   [*****************]                    | [60,70] : b
       *                     [****************]   | [70,80] : c
       *                     [****************]   | [70,80] : d
       *   [***************]                      | [60,69] : s0
       *                     *                    | {70}    : s1
       *                      [***************]   | [71,80] : s2
       * --+---------------+-++---------------+-- |
       *  60              69 71              80   |
       * }}}
       */
      "split 4 meeting intervals" in {
        val a = Interval.closed(60, 70)
        val b = Interval.closed(60, 70)
        val c = Interval.closed(70, 80)
        val d = Interval.closed(70, 80)

        val s0 = Interval.closed(60, 69)
        val s1 = Interval.point(70)
        val s2 = Interval.closed(71, 80)

        val g0 = Set(0, 1)
        val g1 = Set(0, 1, 2, 3)
        val g2 = Set(2, 3)

        val input = List(a, b, c, d)

        val expectedX = List((s0, g0), (s1, g1), (s2, g2))
        val expected  = expectedX.map(_._1)

        val actualX = Interval.splitFind(input).map(it => (it._1.canonical, it._2))
        val actual  = Interval.split(input).map(_.canonical)

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
     *   [****]                                 | [0,9]   : s0
     *        [****]                            | [10,19] : s1
     *             [****]                       | [20,30] : s2
     *                   [*]                    | [31,35] : s3
     *                     [*]                  | [36,39] : s4
     *                       [**]               | [40,45] : s5
     *                          [*]             | [46,50] : s6
     *                             [***]        | [51,59] : s7
     *                                 [****]   | [60,70] : s8
     * --+----+----+----++-+-+--+-++---+----+-- |
     *   0   10   20   30 35   45 51  60   70   |
     *
     * }}}
     */
    "several intervals with different relations" should {
      "produce a split" in {
        val a = Interval.closed(0, 50)  // [0, 50]
        val b = Interval.closed(10, 30) // [10, 30]
        val c = Interval.closed(20, 35) // [20, 35]
        val d = Interval.closed(40, 45) // [40, 45]
        val e = Interval.closed(60, 70) // [60, 70]

        val s0 = Interval.closed(0, 9)
        val s1 = Interval.closed(10, 19)
        val s2 = Interval.closed(20, 30)
        val s3 = Interval.closed(31, 35)
        val s4 = Interval.closed(36, 39)
        val s5 = Interval.closed(40, 45)
        val s6 = Interval.closed(46, 50)
        val s7 = Interval.closed(51, 59)
        val s8 = Interval.closed(60, 70)

        val g0 = Set(0)
        val g1 = Set(0, 1)
        val g2 = Set(0, 1, 2)
        val g3 = Set(0, 2)
        val g4 = Set(0)
        val g5 = Set(0, 3)
        val g6 = Set(0)
        val g7 = Set.empty[Int]
        val g8 = Set(4)

        val input = List(a, b, c, d, e)

        val expectedX = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4), (s5, g5), (s6, g6), (s7, g7), (s8, g8))
        val expected  = expectedX.map(_._1)

        val actualX = Interval.splitFind(input).map(it => (it._1.canonical, it._2))
        val actual  = Interval.split(input).map(_.canonical)

        actualX mustBe expectedX
        actual mustBe expected
      }
    }

    /**
     * {{{
     *   [******]                               | [0,10]  : a
     *                 [******]                 | [20,30] : b
     *                               [******]   | [40,50] : c
     *   [******]                               | [0,10]  : s0
     *           [****]                         | [11,19] : s1
     *                 [******]                 | [20,30] : s2
     *                         [****]           | [31,39] : s3
     *                               [******]   | [40,50] : s4
     * --+------++----++------++----++------+-- |
     *   0     10    19      30    39      50   |
     * }}}
     */
    "non-intersecting intervals" should {
      "produce separate splits" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(20, 30)
        val c = Interval.closed(40, 50)

        val s0 = Interval.closed(0, 10)
        val s1 = Interval.closed(11, 19)
        val s2 = Interval.closed(20, 30)
        val s3 = Interval.closed(31, 39)
        val s4 = Interval.closed(40, 50)

        val g0 = Set(0)
        val g1 = Set()
        val g2 = Set(1)
        val g3 = Set()
        val g4 = Set(2)

        val input = List(a, b, c)

        val expectedX = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4))
        val expected  = expectedX.map(_._1)

        val actualX = Interval.splitFind(input).map(it => (it._1.canonical, it._2))
        val actual  = Interval.split(input).map(_.canonical)

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

    "three example intervals, [1, 10], [7, 12] and [9, 15]" should {

      /**
       * {{{
       *   [**********************]               | [1,10]  : a
       *                  [************]          | [7,12]  : b
       *                       [**************]   | [9,15]  : c
       *   [************]                         | [1,6]   : s0
       *                  [**]                    | [7,8]   : s1
       *                       [**]               | [9,10]  : s2
       *                            [**]          | [11,12] : s3
       *                                 [****]   | [13,15] : s4
       * --+------------+-+--+-+--+-+--+-+----+-- |
       *   1            6 7  8 9 10   12     15   |
       * }}}
       */
      "produce an expected split" in {
        val a = Interval.closed(1, 10) // [1, 10]
        val b = Interval.closed(7, 12) // [7, 12]
        val c = Interval.closed(9, 15) // [9, 15]

        val s0 = Interval.closed(1, 6)
        val s1 = Interval.closed(7, 8)
        val s2 = Interval.closed(9, 10)
        val s3 = Interval.closed(11, 12)
        val s4 = Interval.closed(13, 15)

        val g0 = Set(0)
        val g1 = Set(0, 1)
        val g2 = Set(0, 1, 2)
        val g3 = Set(1, 2)
        val g4 = Set(2)

        val expectedX = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4))
        val expected  = expectedX.map(_._1)

        val actualX = Interval.splitFind(List(a, b, c)).map(it => (it._1.canonical, it._2))
        val actual  = Interval.split(List(a, b, c)).map(_.canonical)

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

    "split [doc]" should {

      /**
       * {{{
       *   [*************]                        | [0,20]  : a
       *          [*************]                 | [10,30] : b
       *                               [******]   | [40,50] : c
       *   [*****]                                | [0,9]   : s0
       *          [******]                        | [10,20] : s1
       *                  [*****]                 | [21,30] : s2
       *                         [****]           | [31,39] : s3
       *                               [******]   | [40,50] : s4
       * --+-----++------++-----++----++------+-- |
       *   0     10     20     30    39      50   |
       * }}}
       */
      "be expected" in {
        val a = Interval.closed(0, 20)  // [0, 20]
        val b = Interval.closed(10, 30) // [10, 30]
        val c = Interval.closed(40, 50) // [40, 50]

        val input = List(a, b, c)

        val ss = Interval.split(input)
        // [ [0, 9], [10, 20], [21, 30], [31, 39], [40, 50]

        val gs = Interval.splitFind(input)
        // [ ([0, 9], {0}), ([10, 20], {0, 1}), ([21, 30], {1}), ([31, 39], {}), ([40, 50], {2}) ]

        // ==
        val s0 = Interval.closed(0, 9)
        val s1 = Interval.closed(10, 20)
        val s2 = Interval.closed(21, 30)
        val s3 = Interval.closed(31, 39)
        val s4 = Interval.closed(40, 50)

        val g0 = Set(0)
        val g1 = Set(0, 1)
        val g2 = Set(1)
        val g3 = Set()
        val g4 = Set(2)

        val expectedX = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4))
        val expected  = expectedX.map(_._1)

        val actualX = Interval.splitFind(input).map(it => (it._1.canonical, it._2))
        val actual  = Interval.split(input).map(_.canonical)

        actual mustBe expected
        actualX mustBe expectedX
      }
    }

  }
