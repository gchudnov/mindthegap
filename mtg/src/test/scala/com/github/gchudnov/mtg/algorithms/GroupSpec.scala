package com.github.gchudnov.mtg.algorithms

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Domain
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import org.scalacheck.Gen

final class GroupSpec extends TestSpec:

  given intRange: IntRange = intRange100
  given intProb: IntProb   = intProb028

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Group" when {
    "a series of intervals are grouped" should {
      "produce only disjoint intervals" in {
        forAll(Gen.choose(0, 15).flatMap(n => Gen.listOfN(n, genNonEmptyIntArgs))) { case (nArgs) =>
          val input = nArgs.map(args => Interval.make(args.left, args.right))

          val actual = Interval.group(input)

          actual
            .combinations(2)
            .filter(ab => !ab.head.equalsTo(ab.last))
            .foreach(ab =>
              val a = ab.head
              val b = ab.last

              a.isDisjoint(b) shouldBe (true),
            )
        }
      }
    }

    "no intervals" should {

      /**
       * {{{
       * }}}
       */
      "be grouped" in {
        val input = List.empty[Interval[Int]]

        val actualX   = Interval.groupFind(input)
        val expectedX = List.empty[(Interval[Int], Set[Int])]

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX
      }
    }

    "one interval" should {

      /**
       * {{{
       *   [**********************************]   | [0,10] : a
       *   [**********************************]   | [0,10] : g1
       * --+----------------------------------+-- |
       *   0                                 10   |
       * }}}
       */
      "be grouped" in {
        val a = Interval.closed(0, 10)

        val input = List(a)

        val actualX   = Interval.groupFind(input)
        val expectedX = List((a, Set(0)))

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX
      }
    }

    "(-inf, +inf) interval" should {

      /**
       * {{{
       *   [******]                               | [0,10]  : a
       *     [********************************]   | [3,50]  : b
       *                 [******]                 | [20,30] : c
       * (**************************************) | (-∞,+∞) : d
       * (**************************************) | (-∞,+∞) : g1
       * +-+-+----+------+------+-------------+-+ |
       * -∞0 3   10     20     30            50+∞ |
       * }}}
       */
      "group with all other intervals" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)
        val d = Interval.unbounded[Int]

        val g1 = d

        val input = List(a, b, c, d)

        val actualX   = Interval.groupFind(input)
        val expectedX = List((g1, Set(0, 1, 2, 3)))

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX
      }
    }

    "empty and non-empty intervals" should {

      /**
       * {{{
       *   [**********************************]   | [0,10] : a
       *                                          | ∅      : b
       *   [**********************************]   | [0,10] : g1
       * --+----------------------------------+-- |
       *   0                                 10   |
       * }}}
       */
      "group" in {
        val a = Interval.closed(0, 10)
        val b = Interval.empty[Int]

        val g1 = a

        val input = List(a, b)

        val actualX   = Interval.groupFind(input)
        val expectedX = List((g1, Set(0, 1)))

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX
      }
    }

    "some of intervals are adjacent" should {

      /**
       * {{{
       *   [***]                                  | [0,10]  : a
       *    [********************]                | [3,50]  : b
       *            [***]                         | [20,30] : c
       *                             [****]       | [60,70] : d
       *                                  [***]   | [71,80] : e
       *   [*********************]                | [0,50]  : g1
       *                             [********]   | [60,80] : g2
       * --++--+----+---+--------+---+----+---+-- |
       *   0  10   20  30       50  60   70  80   |
       * }}}
       */
      "group adjacent into one interval if isAdjacent = true" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)

        val d = Interval.closed(60, 70)
        val e = Interval.closed(71, 80)

        val g1 = Interval.closed(0, 50)
        val g2 = Interval.closed(60, 80)

        val input = List(a, b, c, d, e)

        val actualX   = Interval.groupFind(input)
        val expectedX = List((g1, Set(0, 1, 2)), (g2, Set(3, 4)))

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX
      }

      /**
       * {{{
       *   [***]                                  | [0,10]  : a
       *    [********************]                | [3,50]  : b
       *            [***]                         | [20,30] : c
       *                             [****]       | [60,70] : d
       *                                  [***]   | [71,80] : e
       *   [*********************]                | [0,50]  : g1
       *                             [********]   | [60,80] : g2
       * --++--+----+---+--------+---+----+---+-- |
       *   0  10   20  30       50  60   70  80   |
       * }}}
       */
      "do not group adjacent into one interval if isAdjacent = false" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)

        val d = Interval.closed(60, 70)
        val e = Interval.closed(71, 80)

        val g1 = Interval.closed(0, 50)
        val g2 = Interval.closed(60, 80)

        val input = List(a, b, c, d, e)

        val actualX   = Interval.groupFind(input)
        val expectedX = List((g1, Set(0, 1, 2)), (g2, Set(3, 4)))

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX
      }
    }

    /**
     * {{{
     *   [***]                                  | [0,10]  : a
     *    [********************]                | [3,50]  : b
     *            [***]                         | [20,30] : c
     *                             [****]       | [60,70] : d
     *                               [******]   | [65,80] : e
     *   [*********************]                | [0,50]  : g1
     *                             [********]   | [60,80] : g2
     * --++--+----+---+--------+---+-+--+---+-- |
     *   0  10   20  30       50  60   70  80   |
     * }}}
     */
    "non-adjacent intervals" should {
      "be grouped" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)

        val d = Interval.closed(60, 70)
        val e = Interval.closed(65, 80)

        val g1 = Interval.closed(0, 50)
        val g2 = Interval.closed(60, 80)

        val input = List(a, b, c, d, e)

        val actualX   = Interval.groupFind(input)
        val expectedX = List((g1, Set(0, 1, 2)), (g2, Set(3, 4)))

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX
      }
    }

    /**
     * {{{
     *   [******]                               | [0,10]  : a
     *                 [******]                 | [20,30] : b
     *                               [******]   | [40,50] : c
     *   [******]                               | [0,10]  : g1
     *                 [******]                 | [20,30] : g2
     *                               [******]   | [40,50] : g3
     * --+------+------+------+------+------+-- |
     *   0     10     20     30     40     50   |
     * }}}
     */
    "non-intersecting intervals" should {
      "produce separate groups" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(20, 30)
        val c = Interval.closed(40, 50)

        val g1 = Interval.closed(0, 10)
        val g2 = Interval.closed(20, 30)
        val g3 = Interval.closed(40, 50)

        val input = List(a, b, c)

        val actualX   = Interval.groupFind(input)
        val expectedX = List((g1, Set(0)), (g2, Set(1)), (g3, Set(2)))

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX
      }
    }

    "group [doc]" should {

      /**
       * {{{
       *   [***]                                  | [0,10]  : a
       *    [********************]                | [3,50]  : b
       *            [***]                         | [20,30] : c
       *                             [****]       | [60,70] : d
       *                                  [***]   | [71,80] : e
       *   [*********************]                | [0,50]  : g1
       *                             [********]   | [60,80] : g2
       * --++--+----+---+--------+---+----+---+-- |
       *   0  10   20  30       50  60   70  80   |
       * }}}
       */
      "be expected if adjacent intervals are grouped" in {
        val a = Interval.closed(0, 10)  // [0, 10]
        val b = Interval.closed(3, 50)  // [3, 50]
        val c = Interval.closed(20, 30) // [20, 30]

        val d = Interval.closed(60, 70) // [60, 70]
        val e = Interval.closed(71, 80) // [71, 80]

        val input = List(a, b, c, d, e)

        val gs = Interval.group(input)     // [ [0, 50], [60, 80] ]
        val ts = Interval.groupFind(input) // [ ([0, 50], {0, 1, 2}), ([60, 80], {3, 4}) ]

        // ==
        val g1 = Interval.closed(0, 50)
        val g2 = Interval.closed(60, 80)

        val actualX   = Interval.groupFind(input)
        val expectedX = List((g1, Set(0, 1, 2)), (g2, Set(3, 4)))

        val actual   = Interval.group(input)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX

        gs shouldBe expected
        ts shouldBe expectedX
      }

      /**
       * {{{
       *   [***]                                  | [0,10]  : a
       *    [********************]                | [3,50]  : b
       *            [***]                         | [20,30] : c
       *   [*********************]                | [0,50]  : g1
       *                             [****]       | [60,70] : g2
       *                                  [***]   | [71,80] : g3
       * --++--+----+---+--------+---+----+---+-- |
       *   0  10   20  30       50  60   70  80   |
       * }}}
       */
      "be expected if adjacent intervals are not grouped" in {
        val a = Interval.closed(0, 10)  // [0, 10]
        val b = Interval.closed(3, 50)  // [3, 50]
        val c = Interval.closed(20, 30) // [20, 30]

        val d = Interval.closed(60, 70) // [60, 70]
        val e = Interval.closed(71, 80) // [71, 80]

        val input = List(a, b, c, d, e)

        val gs = Interval.group(input, isGroupAdjacent = false)     // [ [0, 50], [60, 70], [71, 80] ]
        val ts = Interval.groupFind(input, isGroupAdjacent = false) // [ ([0, 50], {0, 1, 2}), ([60, 70], {3}), ([71, 80], {4}) ]

        // ==
        val g1 = Interval.closed(0, 50)
        val g2 = Interval.closed(60, 70)
        val g3 = Interval.closed(71, 80)

        val actualX   = Interval.groupFind(input, isGroupAdjacent = false)
        val expectedX = List((g1, Set(0, 1, 2)), (g2, Set(3)), (g3, Set(4)))

        val actual   = Interval.group(input, isGroupAdjacent = false)
        val expected = expectedX.map(_._1)

        actual shouldBe expected
        actualX shouldBe expectedX

        gs shouldBe expected
        ts shouldBe expectedX
      }
    }
  }
