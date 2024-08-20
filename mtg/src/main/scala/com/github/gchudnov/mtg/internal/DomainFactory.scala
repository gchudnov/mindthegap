package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.domain.*

import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

private[internal] trait DomainFactory:

  def makeIntergral[T: Integral]: Domain[T] =
    new IntegralDomain()

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
