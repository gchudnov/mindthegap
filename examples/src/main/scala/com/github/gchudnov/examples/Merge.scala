package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Merge Intervals
 *
 * Given:
 *   - A collection of intervals where some of them might be overlapping
 *
 * Then:
 *   - Merge the intervals so that there are no overlaps
 */
object Merge extends App:

  /**
   * Example #1
   *
   * {{{
   *   Merge Intervals -- Example #1
   *   input:    [[1,3],[2,6],[8,10],[15,18]]
   *   output:   [[1,6],[8,10],[15,18]]
   *
   *     [***]                                  | [1,3]
   *       [*******]                            | [2,6]
   *                   [****]                   | [8,10]
   *                                  [*****]   | [15,18]
   *     [*********]                            | [1,6]
   *                   [****]                   | [8,10]
   *                                  [*****]   | [15,18]
   *   --+-+-+-----+---+----+---------+-----+-- |
   *     1 2 3     6   8   10        15    18   |
   * }}}
   */
  val xs1 = List(
    Interval.closed(1, 3),
    Interval.closed(2, 6),
    Interval.closed(8, 10),
    Interval.closed(15, 18),
  )

  runExample(1, xs1)

  /**
   * Example #2
   *
   * {{{
   *   Merge Intervals -- Example #2
   *   input:    [[1,4],[4,5]]
   *   output:   [[1,5]]
   *
   *     [*************************]            | [1,4]
   *                               [********]   | [4,5]
   *     [**********************************]   | [1,5]
   *   --+-------------------------+--------+-- |
   *     1                         4        5   |
   * }}}
   */
  val xs2 = List(
    Interval.closed(1, 4),
    Interval.closed(4, 5),
  )

  runExample(2, xs2)

  /**
   * Run an example
   */
  private def runExample[T: Domain](n: Int, input: List[Interval[T]], canvasWidth: Int = 40): Unit =
    // val output = mergeIntervals(input)
    // println(s"Merge Intervals -- Example #${n}")
    // Output.printInOut(input, output)
    // println()
    // Output.printDiagram(input ++ output, canvasWidth)
    // println()
    ???

  /**
   * Merge Intervals
   *
   * @param xs
   *   intervals
   * @return
   *   merged intervals
   */
  def mergeIntervals[T: Domain](xs: List[Interval[T]]): List[Interval[T]] =
    Interval.group(xs, isGroupAdjacent = true)
