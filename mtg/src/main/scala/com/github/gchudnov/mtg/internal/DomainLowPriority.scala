package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.domain.*

import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 * Default Domains
 */
private[mtg] trait DomainLowPriority extends DomainFactory:

  given integralDomain[T: Integral]: Domain[T] =
    makeIntergral

  given offsetDateTimeDomain: Domain[OffsetDateTime] =
    makeOffsetDateTime(ChronoUnit.SECONDS)

  given offsetTimeDomain: Domain[OffsetTime] =
    makeOffsetTime(ChronoUnit.SECONDS)

  given localDateTimeDomain: Domain[LocalDateTime] =
    makeLocalDateTime(ChronoUnit.SECONDS)

  given localDateDomain: Domain[LocalDate] =
    makeLocalDate(ChronoUnit.DAYS)

  given localTimeDomain: Domain[LocalTime] =
    makeLocalTime(ChronoUnit.SECONDS)

  given zonedDateTimeDomain: Domain[ZonedDateTime] =
    makeZonedDateTime(ChronoUnit.SECONDS)

  given instantDomain: Domain[Instant] =
    makeInstant(ChronoUnit.SECONDS)
