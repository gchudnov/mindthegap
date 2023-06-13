package com.github.gchudnov.mtg.examples

import com.github.gchudnov.mtg.*

/**
 * Insert Interval
 *
 * Given:
 *   - A collection of non-overlapping intervals sorted by their start positions
 *   - A single interval
 *
 * Task:
 *   - Insert the interval into the collection such that the collection remains sorted by their start positions and non-overlapping.
 *
 * }}}
 */
object InsertInterval extends App:

  /**
   * Example #1
   *
   * {{{
   *   input:    [[1,4],[7,9]]
   *   interval: [2,6]
   *   output:   [[1,6],[7,9]]
   *
   *   [************]                         | [1,4]
   *                             [********]   | [7,9]
   *       [*****************]                | [2,6]
   *   ---------------------------------------|------
   *   [*********************]                | [1,6]
   *                             [********]   | [7,9]
   * --+---+--------+--------+---+--------+-- |
   *   1   2        4        6   7        9   |
   * }}}
   */
  val input1    = List(Interval.closed(1, 4), Interval.closed(7, 9))
  val interval1 = Interval.closed(2, 6)
  val output1   = Interval.group(interval1 +: input1, isGroupAdjacent = false)

  printResult(input1, interval1, output1)

  println()

  /**
   * Example #2
   *
   * {{{
   *   input:    [[1,3],[4,6],[7,8],[9,11],[13,17]]
   *   interval: [5,9]
   *   output:   [[1,3],[4,11],[13,17]]
   *
   *     [*****]                                          | [1,3]
   *             [*****]                                  | [4,6]
   *                      [**]                            | [7,8]
   *                            [****]                    | [9,11]
   *                                       [**********]   | [13,17]
   *                [***********]                         | [5,9]
   * -----------------------------------------------------|--------
   *     [*****]                                          | [1,3]
   *             [*******************]                    | [4,11]
   *                                       [**********]   | [13,17]
   *   --+-----+-+--+--+--+--+--+----+-----+----------+-- |
   *     1     3 4  5  6  7  8  9   11    13         17   |
   * }}}
   */
  val input2 = List(
    Interval.closed(1, 3),
    Interval.closed(4, 6),
    Interval.closed(7, 8),
    Interval.closed(9, 11),
    Interval.closed(13, 17)
  )
  val interval2 = Interval.closed(5, 9)
  val output2   = Interval.group(interval2 +: input2, isGroupAdjacent = false)

  printResult(input2, interval2, output2)

  private def printResult(input: List[Interval[Int]], interval: Interval[Int], output: List[Interval[Int]]): Unit =
    println("input:    " + asString(input))
    println("interval: " + interval.asString)
    println("output:   " + asString(output))

  private def asString(intervals: List[Interval[Int]]): String =
    intervals.map(i => i.asString).mkString("[", ",", "]")
