package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import java.time.temporal.ChronoUnit
import java.time.*

/**
 * Mermaid Diagram
 *
 * {{{
 *   gantt
 *     title       Mermaid Diagram
 *     dateFormat  HH:mm:ss.SSS
 *     axisFormat  %H:%M:%S
 *   
 *     section My Section
 *     a  :04:00:00.000, 10:00:00.000
 *     b  :08:00:00.000, 20:00:00.000
 * }}}
 */
object MermaidDiagram extends App:
  val t1 = LocalTime.parse("04:00")
  val t2 = LocalTime.parse("10:00")
  val t3 = LocalTime.parse("08:00")
  val t4 = LocalTime.parse("20:00")

  val a = Interval.closed(t1, t2)
  val b = Interval.closed(t3, t4)

  val intervals = List(a, b)
  val result    = intervals

  val renderer = MermaidRenderer.make[LocalTime]
  val diagram = Diagram
    .empty[LocalTime]
    .withTitle("Mermaid Diagram")
    .withSection { s =>
      val s0 = s.withTitle("My Section")
      result.zipWithIndex.foldLeft(s0) { case (s, (i, k)) =>
        s.addInterval(i, s"${('a' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
