package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Intersections
 *
 * Given:
 *   - Two collections of intervals, where each collection is sorted and disjoint
 *
 * Then:
 *   - Find the intersection of the two collections
 */
object Intersections extends App:
  /**
   * Example #1
   *
   * {{{
   *   Intervals Intersections -- Example #1
   *   input:    [[0,2],[5,10],[13,23],[24,25]]
   *   output:   [[1,5],[8,12],[15,24],[25,26]]
   *
   *     [*****]                                                                        | [0,2]
   *                   [**************]                                                 | [5,10]
   *                                           [***************************]            | [13,23]
   *                                                                          [**]      | [24,25]
   *        [**********]                                                                | [1,5]
   *                            [***********]                                           | [8,12]
   *                                                [*************************]         | [15,24]
   *                                                                             [**]   | [25,26]
   *        [**]                                                                        | [1,2]
   *                   *                                                                | {5}
   *                            [*****]                                                 | [8,10]
   *                                                [**********************]            | [15,23]
   *                                                                          *         | {24}
   *                                                                             *      | {25}
   *   --+--+--+-------+--------+-----+-----+--+----+----------------------+--+--+--+-- |
   *     0  1  2       5        8    10    12 13   15                     23 24 25 26   |
   * }}}
   */
  val xs1 = List(
    Interval.closed(0, 2),
    Interval.closed(5, 10),
    Interval.closed(13, 23),
    Interval.closed(24, 25),
  )
  val ys1 = List(
    Interval.closed(1, 5),
    Interval.closed(8, 12),
    Interval.closed(15, 24),
    Interval.closed(25, 26),
  )

  runExample(1, xs1, ys1, canvasWidth = 80)

  /**
   * Example #2
   *
   * {{{
   *   Intervals Intersections -- Example #2
   *   input:    [[1,3],[5,9]]
   *   output:   []
   *
   *     [********]                             | [1,3]
   *                       [****************]   | [5,9]
   *   --+--------+--------+----------------+-- |
   *     1        3        5                9   |
   * }}}
   */
  val xs2 = List(
    Interval.closed(1, 3),
    Interval.closed(5, 9),
  )
  val ys2 = List.empty[Interval[Int]]

  runExample(2, xs2, ys2)

  /**
   * Run an example
   */
  private def runExample[T: Domain](n: Int, xs: List[Interval[T]], ys: List[Interval[T]], canvasWidth: Int = 40): Unit =
    // val zs = intersections(xs, ys)
    // println(s"Intervals Intersections -- Example #${n}")
    // Output.printInOut(xs, ys)
    // println()
    // Output.printDiagram(xs ++ ys ++ zs, canvasWidth)
    // println()
    ???

  /**
   * Calculate intersections
   *
   * @param xs
   * @param ys
   * @return
   */
  private def intersections[T: Domain](xs: List[Interval[T]], ys: List[Interval[T]]): List[Interval[T]] =
    val domT: Domain[T] = summon[Domain[T]]

    final case class Acc(i: Int, j: Int, rs: Vector[Interval[T]])

    object Acc:
      lazy val empty: Acc =
        Acc(0, 0, Vector.empty[Interval[T]])

    def iterate(acc: Acc): List[Interval[T]] =
      // if acc.i >= xs.length || acc.j >= ys.length then acc.rs.toList
      // else
      //   val x = xs(acc.i)
      //   val y = ys(acc.j)

      //   val rs =
      //     if x.intersects(y) then
      //       val z = x.intersection(y)
      //       acc.rs :+ z
      //     else acc.rs

      //   if domT.ordEndpoint.lt(x.rightEndpoint, y.rightEndpoint) then iterate(Acc(acc.i + 1, acc.j, rs))
      //   else iterate(Acc(acc.i, acc.j + 1, rs))
      ??? // TODO: fix it

    iterate(Acc.empty)
