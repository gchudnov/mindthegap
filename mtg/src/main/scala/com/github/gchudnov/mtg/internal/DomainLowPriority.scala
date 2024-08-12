package com.github.gchudnov.mtg.internal

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.TemporalUnit
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.OffsetTime
import java.time.LocalTime
import java.time.LocalDate
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.domain.*

/**
 * Default Domains
 */
private[mtg] trait DomainLowPriority:

  given nothingDomain: Domain[Nothing] =
    new NothingDomain

  given integralDomain[T: Integral]: Domain[T] =
    new IntegralDomain()

  // TODO: add with default precision


  def makeFractional[T: Fractional](unit: T): Domain[T] =
    new FractionalDomain(unit)

  def makeOffsetDateTime(unit: TemporalUnit): Domain[OffsetDateTime] =
    new OffsetDateTimeDomain(unit)

  def makeOffsetTime(unit: TemporalUnit): Domain[OffsetTime] =
    new OffsetTimeDomain(unit)

  def makeLocalDateTime(unit: TemporalUnit): Domain[LocalDateTime] =
    new LocalDateTimeDomain(unit)

  def makeLocalDate(unit: TemporalUnit): Domain[LocalDate] =
    new LocalDateDomain(unit)

  def makeLocalTime(unit: TemporalUnit): Domain[LocalTime] =
    new LocalTimeDomain(unit)

  def makeZonedDateTime(unit: TemporalUnit): Domain[ZonedDateTime] =
    new ZonedDateTimeDomain(unit)

  def makeInstant(unit: TemporalUnit): Domain[Instant] =
    new InstantDomain(unit)

