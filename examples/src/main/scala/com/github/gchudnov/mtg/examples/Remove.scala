package com.github.gchudnov.mtg.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import com.github.gchudnov.mtg.examples.util.Output

/**
 * Remove Interval
 *
 * Given:
 *   - A collection sorted, disjoint intervals
 *   - A single interval
 *
 * Then:
 *   - Remove the interval from the collection
 *
 * }}}
 */
object Remove extends App:

  given domainDouble: Domain[Double] = Domain.makeFractional(0.1)

  /**
   * Example #1
   *
   * {{{
   *   input:     [[0.0,2.0),[3.0,4.0),[5.0,7.0)]
   *   to-remove: [1.0,6.0)
   *   output:    [[0.0,1.0),[6.0,7.0)]
   *
   *     [*********)                            | [0.0,2.0)
   *                    [****)                  | [3.0,4.0)
   *                              [*********)   | [5.0,7.0)
   *          [************************)        | [1.0,6.0)
   *     [****)                                 | [0.0,1.0)
   *                                   [****)   | [6.0,7.0)
   *   --+----+----+----+----+----+----+----+-- |
   *    0.0  1.0  2.0  3.0  4.0  5.0  6.0  7.0  |
   * }}}
   */
  val input1 = List(
    Interval.leftClosedRightOpen(0.0, 2.0),
    Interval.leftClosedRightOpen(3.0, 4.0),
    Interval.leftClosedRightOpen(5.0, 7.0),
  )
  val toRemove1 = Interval.leftClosedRightOpen(1.0, 6.0)

  runExample(1, input1, toRemove1)

  /**
   * Example #2
   *
   * {{{
   *   input:     [[0.0,5.0)]
   *   to-remove: [2.0,3.0)
   *   output:    [[0.0,2.0),[3.0,5.0)]
   *
   *     [**********************************)   | [0.0,5.0)
   *                   [******)                 | [2.0,3.0)
   *     [*************)                        | [0.0,2.0)
   *                          [*************)   | [3.0,5.0)
   *   --+-------------+------+-------------+-- |
   *    0.0           2.0    3.0           5.0  |
   * }}}
   */
  val input2 = List(
    Interval.leftClosedRightOpen(0.0, 5.0)
  )
  val toRemove2 = Interval.leftClosedRightOpen(2.0, 3.0)

  runExample(2, input2, toRemove2)

  /**
   * Example #3
   *
   * {{{
   *   input:    [[-5.0,-4.0),[-3.0,-2.0),[1.0,2.0),[3.0,5.0),[8.0,9.0)]
   *   interval: [-1.0,4.0)
   *   output:   [[-5.0,-4.0),[-3.0,-2.0),[4.0,5.0),[8.0,9.0)]
   *
   *     [****)                                                                         | [-5.0,-4.0)
   *                [****)                                                              | [-3.0,-2.0)
   *                                     [****)                                         | [1.0,2.0)
   *                                               [**********)                         | [3.0,5.0)
   *                                                                           [****)   | [8.0,9.0)
   *                          [**************************)                              | [-1.0,4.0)
   *     [****)                                                                         | [-5.0,-4.0)
   *                [****)                                                              | [-3.0,-2.0)
   *                                                     [****)                         | [4.0,5.0)
   *                                                                           [****)   | [8.0,9.0)
   *   --+----+-----+----+----+----------+----+----+-----+----+----------------+----+-- |
   *   -5.0 -4.0  -3.0 -2.0 -1.0        1.0  2.0  3.0   4.0  5.0              8.0  9.0  |
   * }}}
   */
  val input3 = List(
    Interval.leftClosedRightOpen(-5.0, -4.0),
    Interval.leftClosedRightOpen(-3.0, -2.0),
    Interval.leftClosedRightOpen(1.0, 2.0),
    Interval.leftClosedRightOpen(3.0, 5.0),
    Interval.leftClosedRightOpen(8.0, 9.0),
  )
  val toRemove3 = Interval.leftClosedRightOpen(-1.0, 4.0)

  runExample(3, input3, toRemove3, canvasWidth = 80)

  /**
   * Run an example
   */
  private def runExample[T: Domain](n: Int, input: List[Interval[T]], toRemove: Interval[T], canvasWidth: Int = 40): Unit =
    val output = removeInterval(input, toRemove)
    println(s"Remove Interval -- Example #${n}")
    Output.printInOut(input, toRemove, output)
    println()
    Output.printDiagram((input :+ toRemove) ++ output, canvasWidth)
    println()

  /**
   * Remove the Interval from the collection
   *
   * @param xs
   *   collection of intervals
   * @param y
   *   interval to remove
   * @return
   *   collection of intervals
   */
  private def removeInterval[T: Domain](xs: List[Interval[T]], y: Interval[T]): List[Interval[T]] =
    xs.flatMap { x =>
      if x.intersects(y) then Interval.minus(x, y)
      else List(x)
    }
