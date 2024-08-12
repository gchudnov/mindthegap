package com.github.gchudnov.mtg.internal.domain

import java.time.temporal.TemporalUnit
import com.github.gchudnov.mtg.Domain
import java.time.OffsetTime

/**
 * OffsetTime Domain
 */
private[internal] final class OffsetTimeDomain(unit: TemporalUnit) extends AnyDomain[OffsetTime]:

  override def succ(x: OffsetTime): OffsetTime =
    x.plus(1, unit)

  override def pred(x: OffsetTime): OffsetTime =
    x.minus(1, unit)

  override def count(start: OffsetTime, end: OffsetTime): Long =
    start.until(end.plus(1, unit), unit)

  override def compare(x: OffsetTime, y: OffsetTime): Int =
    x.compareTo(y)
