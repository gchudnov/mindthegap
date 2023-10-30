package com.github.gchudnov.mtg.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import com.github.gchudnov.mtg.examples.util.Output

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
object Insert extends App:

  /**
   * Example #1
   *
   * {{{
   *   input:    [[1,4],[7,9]]
   *   interval: [2,6]
   *   output:   [[1,6],[7,9]]
   *
   *     [************]                         | [1,4]
   *                               [********]   | [7,9]
   *         [*****************]                | [2,6]
   *     [*********************]                | [1,6]
   *                               [********]   | [7,9]
   *   --+---+--------+--------+---+--------+-- |
   *     1   2        4        6   7        9   |
   * }}}
   */
  val input1    = List(Interval.closed(1, 4), Interval.closed(7, 9))
  val toInsert1 = Interval.closed(2, 6)

  runExample(1, input1, toInsert1)

  /**
   * Example #2
   *
   * {{{
   *   input:    [[1,3],[4,6],[7,8],[9,11],[13,17]]
   *   interval: [5,9]
   *   output:   [[1,3],[4,11],[13,17]]
   *
   *     [***]                                  | [1,3]
   *            [***]                           | [4,6]
   *                  [*]                       | [7,8]
   *                       [***]                | [9,11]
   *                               [********]   | [13,17]
   *              [********]                    | [5,9]
   *     [***]                                  | [1,3]
   *            [**************]                | [4,11]
   *                               [********]   | [13,17]
   *   --+---+--+-+-+-+-+--+---+---+--------+-- |
   *     1   3  4 5 6 7 8  9  11  13       17   |
   * }}}
   */
  val input2 = List(
    Interval.closed(1, 3),
    Interval.closed(4, 6),
    Interval.closed(7, 8),
    Interval.closed(9, 11),
    Interval.closed(13, 17),
  )
  val toInsert2 = Interval.closed(5, 9)

  runExample(2, input2, toInsert2)

  /**
   * Run an example
   */
  private def runExample[T: Domain](n: Int, input: List[Interval[T]], toInsert: Interval[T], canvasWidth: Int = 40): Unit =
    val output = insertInterval(input, toInsert)
    println(s"Insert Interval -- Example #${n}")
    Output.printInOut(input, toInsert, output)
    println()
    Output.printDiagram((input :+ toInsert) ++ output, canvasWidth)
    println()

  /**
   * Insert an interval into the collection of intervals.
   */
  private def insertInterval[T: Domain](xs: List[Interval[T]], y: Interval[T]): List[Interval[T]] =
    val intervals = xs :+ y
    Interval.group(intervals, isGroupAdjacent = false)
