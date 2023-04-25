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
      "produce only disjoint intervals" in {
        // TODO: impl
      }
    }

    "no intervals" should {
      "be split" in {
        val input = List.empty[Interval[Int]]

        val actual   = Interval.split(input)
        val expected = List.empty[Interval[Int]]

        val actualInfo   = Interval.splitFind(input)
        val expectedInfo = List.empty[(Interval[Int], Set[Int])]

        actual mustBe expected
        actualInfo mustBe expectedInfo
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
        val actual = Interval.splitFind(input)
        val expected = List((s0, g0), (s1, g1), (s2, g2), (s3, g3), (s4, g4), (s5, g5), (s6, g6), (s7, g7), (s8, g8))

        actual.size mustBe 9
        actual mustBe expected


        val actualIntervals = Interval.split(input)
        val expectedIntervals = expected.map(_._1)

        actualIntervals mustBe expectedIntervals
      }
    }
  }
