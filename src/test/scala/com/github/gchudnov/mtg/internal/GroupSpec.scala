package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
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

              a.intersects(b) mustBe (false)
            )
        }
      }
    }

    "no intervals" should {
      "be grouped" in {
        val input = List.empty[Interval[Int]]

        val actual   = Interval.group(input)
        val expected = List.empty[Interval[Int]]

        val actualInfo   = Interval.groupFind(input)
        val expectedInfo = List.empty[(Interval[Int], Set[Int])]

        actual mustBe expected
        actualInfo mustBe expectedInfo
      }
    }

    "one interval" should {
      "be grouped" in {
        val a = Interval.closed(0, 10)

        val input = List(a)

        val actual   = Interval.group(input)
        val expected = List(a)

        val actualInfo   = Interval.groupFind(input)
        val expectedInfo = List((a, Set(0)))

        actual mustBe expected
        actualInfo mustBe expectedInfo
      }
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
    "intervals where some of them are adjacent" should {
      "group adjacent into one interval" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)

        val d = Interval.closed(60, 70)
        val e = Interval.closed(71, 80)

        val g1 = Interval.closed(0, 50)
        val g2 = Interval.closed(60, 80)

        val input = List(a, b, c, d, e)

        val actual   = Interval.group(input)
        val expected = List(g1, g2)

        val actualInfo   = Interval.groupFind(input)
        val expectedInfo = List((g1, Set(0, 1, 2)), (g2, Set(3, 4)))

        actual mustBe expected
        actualInfo mustBe expectedInfo
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

        val actual   = Interval.group(input)
        val expected = List(g1, g2)

        val actualInfo   = Interval.groupFind(input)
        val expectedInfo = List((g1, Set(0, 1, 2)), (g2, Set(3, 4)))

        actual mustBe expected
        actualInfo mustBe expectedInfo
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

        val actual   = Interval.group(input)
        val expected = List(g1, g2, g3)

        val actualInfo   = Interval.groupFind(input)
        val expectedInfo = List((g1, Set(0)), (g2, Set(1)), (g3, Set(2)))

        actual mustBe expected
        actualInfo mustBe expectedInfo
      }
    }
  }