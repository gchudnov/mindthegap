package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import java.time.temporal.ChronoUnit
import java.time.*

/**
 * ASCII Diagram with a custom theme.
 *
 * {{{
 *     ▐███████████▌                          | [1,5]   : a
 *           ▐█████████████▌                  | [3,8]   : b
 *                               ▐█████▌      | [10,12] : c
 *                                  ▐█████▌   | [11,13] : d
 *   --+-----+-----+-------+-----+--+--+--+-- |
 *     1     3     5       8    10 11 12 13   |
 * }}}
 */
object AsciiDiagramCustomTheme extends App:
  val a = Interval.closed(1, 5)
  val b = Interval.closed(3, 8)
  val c = Interval.closed(10, 12)
  val d = Interval.closed(11, 13)

  val intervals = List(a, b, c, d)
  val result    = intervals

  // custom theme
  val theme = AsciiTheme.default.copy(
    interval = AsciiTheme.IntervalTheme.default.copy(fill = '█', leftClosed = '▐', rightClosed = '▌')
  )

  val renderer = AsciiRenderer.make[Int](theme = theme)
  val diagram = Diagram
    .empty[Int]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('a' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
