package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Group Intervals
 *
 * {{{
 * Given:
 *     [************]                         | [1,4] : a
 *                               [********]   | [7,9] : b
 *                                            |
 * And:                                       |
 *         [*****************]                | [2,6] : c
 *                                            |
 * When Grouped                               |
 *                                            |
 * Then:                                      |
 *     [*********************]                | [1,6] : d
 *                               [********]   | [7,9] : e
 *   --+---+--------+--------+---+--------+-- |
 *     1   2        4        6   7        9   |
 * }}}
 */
object Group1 extends App:
  val a = Interval.closed(1, 4)
  val b = Interval.closed(7, 9)
  val c = Interval.closed(2, 6)

  val intervals = List(a, b, c)
  val result    = Interval.group(intervals, isGroupAdjacent = false)

  val renderer = AsciiRenderer.make[Int]()
  val diagram = Diagram
    .empty[Int]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('d' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
