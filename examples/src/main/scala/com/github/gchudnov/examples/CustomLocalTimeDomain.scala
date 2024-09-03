package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import java.time.temporal.ChronoUnit
import java.time.*

/**
 * Custom LocalTime Domain
 *
 * {{{
 *     [************]                         | [04:00,10:00] : a
 *              [*************************]   | [08:00,20:00] : b
 *   --+--------+---+---------------------+-- |
 *   04:00    08:00                     20:00 |
 * }}}
 */
object CustomLocalTimeDomain extends App:
  // custom domain with a resolution of 1 minute
  given customDomain: Domain[LocalTime] = Domain.makeLocalTime(ChronoUnit.MINUTES)

  val t1 = LocalTime.parse("04:00")
  val t2 = LocalTime.parse("10:00")
  val t3 = LocalTime.parse("08:00")
  val t4 = LocalTime.parse("20:00")

  val a = Interval.closed(t1, t2)
  val b = Interval.closed(t3, t4)

  val intervals = List(a, b)
  val result    = intervals

  val renderer = AsciiRenderer.make[LocalTime]()
  val diagram = Diagram
    .empty[LocalTime]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('a' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
