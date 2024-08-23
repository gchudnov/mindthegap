package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import java.time.temporal.ChronoUnit
import java.time.*

/**
 * ASCII Rendering Custom Canvas
 *
 * {{{
 *     [********************]                                                                             | [2022-07-02T04:00Z,2022-07-04T10:00Z] : a
 *                         [********************************]                                             | [2022-07-04T08:00Z,2022-07-07T20:00Z] : b
 *                                                   [*********************]                              | [2022-07-07T03:00Z,2022-07-09T11:00Z] : c
 *                                                                     [******************************]   | [2022-07-09T01:00Z,2022-07-12T08:00Z] : d
 *   --+-------------------++------------------------+------+----------+---+--------------------------+-- |
 *   2022-07-02T04:00Z                       2022-07-07T03:00Z 2022-07-09T01:00Z        2022-07-12T08:00Z |
 * }}}
 */
object AsciiRenderCustomCanvas extends App:
  val t1 = OffsetDateTime.parse("2022-07-02T04:00Z")
  val t2 = OffsetDateTime.parse("2022-07-04T10:00Z")
  val t3 = OffsetDateTime.parse("2022-07-04T08:00Z")
  val t4 = OffsetDateTime.parse("2022-07-07T20:00Z")
  val t5 = OffsetDateTime.parse("2022-07-07T03:00Z")
  val t6 = OffsetDateTime.parse("2022-07-09T11:00Z")
  val t7 = OffsetDateTime.parse("2022-07-09T01:00Z")
  val t8 = OffsetDateTime.parse("2022-07-12T08:00Z")

  val a = Interval.closed(t1, t2)
  val b = Interval.closed(t3, t4)
  val c = Interval.closed(t5, t6)
  val d = Interval.closed(t7, t8)

  val intervals = List(a, b, c, d)
  val result    = intervals

  val canvas = AsciiCanvas.make(100)

  val renderer = AsciiRenderer.make[OffsetDateTime](theme = AsciiTheme.default, canvas = canvas)
  val diagram = Diagram
    .empty[OffsetDateTime]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('a' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
