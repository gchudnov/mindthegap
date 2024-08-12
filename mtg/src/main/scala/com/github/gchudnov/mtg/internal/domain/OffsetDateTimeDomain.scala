package com.github.gchudnov.mtg.internal.domain

import java.time.temporal.TemporalUnit
import com.github.gchudnov.mtg.Domain
import java.time.OffsetDateTime

/**
 * OffsetDateTime Domain
 */
private[internal] final class OffsetDateTimeDomain(unit: TemporalUnit) extends AnyDomain[OffsetDateTime]:

  override def succ(x: OffsetDateTime): OffsetDateTime =
    x.plus(1, unit)

  override def pred(x: OffsetDateTime): OffsetDateTime =
    x.minus(1, unit)

  override def count(start: OffsetDateTime, end: OffsetDateTime): Long =
    start.until(end.plus(1, unit), unit)

  override def compare(x: OffsetDateTime, y: OffsetDateTime): Int =
    x.compareTo(y)
