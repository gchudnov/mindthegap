package com.github.gchudnov.mtg.internal.domain

import java.time.temporal.TemporalUnit
import com.github.gchudnov.mtg.Domain
import java.time.LocalDate

/**
 * LocalDate Domain
 */
private[internal] final class LocalDateDomain(unit: TemporalUnit) extends AnyDomain[LocalDate]:

  override def succ(x: LocalDate): LocalDate =
    x.plus(1, unit)

  override def pred(x: LocalDate): LocalDate =
    x.minus(1, unit)

  override def count(start: LocalDate, end: LocalDate): Long =
    start.until(end.plus(1, unit), unit)

  override def compare(x: LocalDate, y: LocalDate): Int =
    x.compareTo(y)
