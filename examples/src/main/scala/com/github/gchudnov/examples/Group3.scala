package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Group Intervals #3 (adjacent)
 *
 * {{{
 * Given:
 *     [***]                                  | [1,3]   : a
 *       [*******]                            | [2,6]   : b
 *                   [****]                   | [8,10]  : c
 *                                  [*****]   | [15,18] : d
 *                                            |
 * When Grouped                               |
 *                                            |
 * Then                                       |
 *     [*********]                            | [1,6]    : e
 *                   [****]                   | [8,10]   : f
 *                                  [*****]   | [15,18]  : g
 *   --+-+-+-----+---+----+---------+-----+-- |
 *     1 2 3     6   8   10        15    18   |
 * }}}
 */
object Group3 extends App:
  val a = Interval.closed(1, 3)
  val b = Interval.closed(2, 6)
  val c = Interval.closed(8, 10)
  val d = Interval.closed(15, 18)

  val intervals = List(a, b, c, d)
  val result    = Interval.group(intervals, isGroupAdjacent = true)

  val renderer = AsciiRenderer.make[Int]()
  val diagram = Diagram
    .empty[Int]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('e' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
