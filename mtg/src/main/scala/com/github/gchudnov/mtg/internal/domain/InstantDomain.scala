package com.github.gchudnov.mtg.internal.domain

import java.time.temporal.TemporalUnit
import com.github.gchudnov.mtg.Domain
import java.time.Instant

/**
 * Instant Domain
 */
final class InstantDomain(unit: TemporalUnit) extends Domain[Instant]:

  override def succ(x: Instant): Instant =
    x.plus(1, unit)

  override def pred(x: Instant): Instant =
    x.minus(1, unit)

  override def count(start: Instant, end: Instant): Long =
    start.until(end.plus(1, unit), unit)

  override def compare(x: Instant, y: Instant): Int =
    x.compareTo(y)
