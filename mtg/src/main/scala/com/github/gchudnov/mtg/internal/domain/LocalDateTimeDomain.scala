package com.github.gchudnov.mtg.internal.domain

import java.time.temporal.TemporalUnit
import com.github.gchudnov.mtg.Domain
import java.time.LocalDateTime

/**
 * LocalDateTime Domain
 */
private[internal] final class LocalDateTimeDomain(unit: TemporalUnit) extends AnyDomain[LocalDateTime]:

  override def succ(x: LocalDateTime): LocalDateTime =
    x.plus(1, unit)

  override def pred(x: LocalDateTime): LocalDateTime =
    x.minus(1, unit)

  override def count(start: LocalDateTime, end: LocalDateTime): Long =
    start.until(end.plus(1, unit), unit)

  override def compare(x: LocalDateTime, y: LocalDateTime): Int =
    x.compareTo(y)
