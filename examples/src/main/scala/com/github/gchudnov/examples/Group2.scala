package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Group Intervals #2 (non-adjacent)
 *
 * {{{
 * Given 
 *     [***]                                  | [1,3]   : a
 *            [***]                           | [4,6]   : b
 *                  [*]                       | [7,8]   : c
 *                       [***]                | [9,11]  : d
 *                               [********]   | [13,17] : e
 *              [********]                    | [5,9]   : f
 *                                            |
 * When Grouped                               |
 *                                            |
 * Then                                       |
 *     [***]                                  | [1,3]   : g
 *            [**************]                | [4,11]  : h
 *                               [********]   | [13,17] : i
 *   --+---+--+-+-+-+-+--+---+---+--------+-- |
 *     1   3  4 5 6 7 8  9  11  13       17   |
 * }}}
 */
object Group2 extends App:
  val a = Interval.closed(1, 3)
  val b = Interval.closed(4, 6)
  val c = Interval.closed(7, 8)
  val d = Interval.closed(9, 11)
  val e = Interval.closed(13, 17)
  val f = Interval.closed(5, 9)

  val intervals = List(a, b, c, d, e, f)
  val result    = Interval.group(intervals, isGroupAdjacent = false)

  val renderer = AsciiRenderer.make[Int]()
  val diagram = Diagram
    .empty[Int]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('g' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
