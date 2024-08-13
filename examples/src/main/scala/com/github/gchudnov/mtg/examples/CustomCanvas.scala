package com.github.gchudnov.mtg.examples

import com.github.gchudnov.mtg.*
import java.time.temporal.ChronoUnit
import java.time.OffsetDateTime
import com.github.gchudnov.mtg.diagram.Diagram
import com.github.gchudnov.mtg.diagram.Canvas
import com.github.gchudnov.mtg.diagram.View

/**
 * Custom Canvas
 *
 * The example demonstrates how to create a custom canvas.
 */
object CustomCanvas extends App:
  // TODO: recover
  // given offsetDateTimeDomain: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.MINUTES)

  // val a = Interval.closed(OffsetDateTime.parse("2022-07-02T04:00Z"), OffsetDateTime.parse("2022-07-04T10:00Z"))
  // val b = Interval.closed(OffsetDateTime.parse("2022-07-04T08:00Z"), OffsetDateTime.parse("2022-07-07T20:00Z"))
  // val c = Interval.closed(OffsetDateTime.parse("2022-07-07T03:00Z"), OffsetDateTime.parse("2022-07-09T11:00Z"))
  // val d = Interval.closed(OffsetDateTime.parse("2022-07-09T01:00Z"), OffsetDateTime.parse("2022-07-12T08:00Z"))

  // val canvas  = Canvas.make(100)
  // val view    = View.make(Some(OffsetDateTime.parse("2022-07-04T05:00Z")), Some(OffsetDateTime.parse("2022-07-05T01:00Z")))
  // val diagram = Diagram.make(List(a, b, c, d), view, canvas)
  // val ds      = Diagram.render(diagram)

  // /**
  //  * {{{
  //  *   **************************]                                                                          | [2022-07-02T04:00Z,2022-07-04T10:00Z] : a
  //  *                   [*********************************************************************************** | [2022-07-04T08:00Z,2022-07-07T20:00Z] : b
  //  *                                                                                                        | [2022-07-07T03:00Z,2022-07-09T11:00Z] : c
  //  *                                                                                                        | [2022-07-09T01:00Z,2022-07-12T08:00Z] : d
  //  *   --+-------------+---------+----------------------------------------------------------------------+-- |
  //  *   2022-07-04T05:00Z 2022-07-04T10:00Z                                                2022-07-05T01:00Z |
  //  * }}}
  //  */
  // ds.foreach(d => println(d))

  println("Done.")