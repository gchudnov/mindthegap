package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Group Intervals #4 (adjacent)
 *
 * {{{
 * Given:
 *     [*************************]            | [1,4] : a
 *                               [********]   | [4,5] : b
 *                                            |
 * When Grouped                               |
 *                                            |
 * Then                                       |
 *     [**********************************]   | [1,5] : c
 *   --+-------------------------+--------+-- |
 *     1                         4        5   |
 * }}}
 */
object Group4 extends App:
  val a = Interval.closed(1, 4)
  val b = Interval.closed(4, 5)

  val intervals = List(a, b)
  val result    = Interval.group(intervals, isGroupAdjacent = true)

  val renderer = AsciiRenderer.make[Int]()
  val diagram = Diagram
    .empty[Int]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('c' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
