package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import java.time.temporal.ChronoUnit
import java.time.*

/**
  * Custom OffsetDateTime Domain
  * 
  * {{{
  *     [*************]                        | [2022-07-02T04:00Z,2022-07-04T10:00Z] : a
  *                  [*********************]   | [2022-07-04T08:00Z,2022-07-07T20:00Z] : b
  *   --+------------++--------------------+-- |
  *   2022-07-02T04:00Z      2022-07-07T20:00Z |
  * }}}
  */
object CustomOffsetDateTimeDomain extends App:
  given offsetDateTimeDomain: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.MINUTES)

  val t1 = OffsetDateTime.parse("2022-07-02T04:00Z")
  val t2 = OffsetDateTime.parse("2022-07-04T10:00Z")
  val t3 = OffsetDateTime.parse("2022-07-04T08:00Z")
  val t4 = OffsetDateTime.parse("2022-07-07T20:00Z")
  
  val a = Interval.closed(t1, t2)
  val b = Interval.closed(t3, t4)

  val intervals = List(a, b)
  val result = intervals

  val renderer = AsciiRenderer.make[OffsetDateTime]()
  val diagram = Diagram
    .empty[OffsetDateTime]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('a' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
