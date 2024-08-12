package com.github.gchudnov.mtg.internal.domain

import java.time.temporal.TemporalUnit
import com.github.gchudnov.mtg.Domain
import java.time.LocalTime

/**
 * LocalTime Domain
 */
private[internal] final class LocalTimeDomain(unit: TemporalUnit) extends AnyDomain[LocalTime]:

  override def succ(x: LocalTime): LocalTime =
    x.plus(1, unit)

  override def pred(x: LocalTime): LocalTime =
    x.minus(1, unit)

  override def count(start: LocalTime, end: LocalTime): Long =
    start.until(end.plus(1, unit), unit)

  override def compare(x: LocalTime, y: LocalTime): Int =
    x.compareTo(y)
