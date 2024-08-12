package com.github.gchudnov.mtg.internal.domain

import java.time.temporal.TemporalUnit
import com.github.gchudnov.mtg.Domain
import java.time.ZonedDateTime

/**
 * ZonedDateTime Domain
 */
final class ZonedDateTimeDomain(unit: TemporalUnit) extends Domain[ZonedDateTime]:

  override def succ(x: ZonedDateTime): ZonedDateTime =
    x.plus(1, unit)

  override def pred(x: ZonedDateTime): ZonedDateTime =
    x.minus(1, unit)

  override def count(start: ZonedDateTime, end: ZonedDateTime): Long =
    start.until(end.plus(1, unit), unit)

  override def compare(x: ZonedDateTime, y: ZonedDateTime): Int =
    x.compareTo(y)
