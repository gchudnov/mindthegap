package com.github.gchudnov.mtg

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.TemporalUnit
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.OffsetTime
import java.time.LocalTime
import java.time.LocalDate

/**
 * Default Domains
 */
private[mtg] trait DomainDefaults:
  import DomainDefaults.*

  given nothingDomain: Domain[Nothing] =
    new NothingDomain

  given integralDomain[T: Integral]: Domain[T] =
    new IntegralDomain()

  def makeFractional[T: Fractional](unit: T): Domain[T] =
    new DomainDefaults.FractionalDomain(unit)

  def makeOffsetDateTime(unit: TemporalUnit): Domain[OffsetDateTime] =
    new DomainDefaults.OffsetDateTimeDomain(unit)

  def makeOffsetTime(unit: TemporalUnit): Domain[OffsetTime] =
    new DomainDefaults.OffsetTimeDomain(unit)

  def makeLocalDateTime(unit: TemporalUnit): Domain[LocalDateTime] =
    new DomainDefaults.LocalDateTimeDomain(unit)

  def makeLocalDate(unit: TemporalUnit): Domain[LocalDate] =
    new DomainDefaults.LocalDateDomain(unit)

  def makeLocalTime(unit: TemporalUnit): Domain[LocalTime] =
    new DomainDefaults.LocalTimeDomain(unit)

  def makeZonedDateTime(unit: TemporalUnit): Domain[ZonedDateTime] =
    new DomainDefaults.ZonedDateTimeDomain(unit)

  def makeInstant(unit: TemporalUnit): Domain[Instant] =
    new DomainDefaults.InstantDomain(unit)

/**
 * Domain Factories
 */
private[mtg] object DomainDefaults:

  final class NothingDomain extends Domain[Nothing]:

    override def succ(x: Nothing): Nothing =
      x

    override def pred(x: Nothing): Nothing =
      x

    override def count(start: Nothing, end: Nothing): Long =
      0

    override def compare(x: Nothing, y: Nothing): Int =
      0

  /**
   * Integral Domain: Int, Long, ...
   */
  final class IntegralDomain[T: Integral] extends Domain[T]:
    val intT = summon[Integral[T]]

    override def succ(x: T): T =
      intT.plus(x, intT.one)

    override def pred(x: T): T =
      intT.minus(x, intT.one)

    override def count(start: T, end: T): Long =
      intT.toLong(intT.plus(intT.minus(end, start), intT.one))

    override def compare(x: T, y: T): Int =
      intT.compare(x, y)

  /**
   * Fractional Domain: Double, Float with step size
   */
  final class FractionalDomain[T: Fractional](unit: T) extends Domain[T]:
    val fracT = summon[Fractional[T]]

    override def succ(x: T): T =
      fracT.plus(x, unit)

    override def pred(x: T): T =
      fracT.minus(x, unit)

    override def count(start: T, end: T): Long =
      fracT.toLong(fracT.div(fracT.plus(fracT.minus(end, start), unit), unit))

    override def compare(x: T, y: T): Int =
      fracT.compare(x, y)

  /**
   * OffsetDateTime Domain
   */
  final class OffsetDateTimeDomain(unit: TemporalUnit) extends Domain[OffsetDateTime]:

    override def succ(x: OffsetDateTime): OffsetDateTime =
      x.plus(1, unit)

    override def pred(x: OffsetDateTime): OffsetDateTime =
      x.minus(1, unit)

    override def count(start: OffsetDateTime, end: OffsetDateTime): Long =
      start.until(end.plus(1, unit), unit)

    override def compare(x: OffsetDateTime, y: OffsetDateTime): Int =
      x.compareTo(y)

  /**
   * OffsetTime Domain
   */
  final class OffsetTimeDomain(unit: TemporalUnit) extends Domain[OffsetTime]:

    override def succ(x: OffsetTime): OffsetTime =
      x.plus(1, unit)

    override def pred(x: OffsetTime): OffsetTime =
      x.minus(1, unit)

    override def count(start: OffsetTime, end: OffsetTime): Long =
      start.until(end.plus(1, unit), unit)

    override def compare(x: OffsetTime, y: OffsetTime): Int =
      x.compareTo(y)

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

  /**
   * LocalDateTime Domain
   */
  final class LocalDateTimeDomain(unit: TemporalUnit) extends Domain[LocalDateTime]:

    override def succ(x: LocalDateTime): LocalDateTime =
      x.plus(1, unit)

    override def pred(x: LocalDateTime): LocalDateTime =
      x.minus(1, unit)

    override def count(start: LocalDateTime, end: LocalDateTime): Long =
      start.until(end.plus(1, unit), unit)

    override def compare(x: LocalDateTime, y: LocalDateTime): Int =
      x.compareTo(y)

  /**
   * LocalDate Domain
   */
  final class LocalDateDomain(unit: TemporalUnit) extends Domain[LocalDate]:

    override def succ(x: LocalDate): LocalDate =
      x.plus(1, unit)

    override def pred(x: LocalDate): LocalDate =
      x.minus(1, unit)

    override def count(start: LocalDate, end: LocalDate): Long =
      start.until(end.plus(1, unit), unit)

    override def compare(x: LocalDate, y: LocalDate): Int =
      x.compareTo(y)

  /**
   * LocalTime Domain
   */
  final class LocalTimeDomain(unit: TemporalUnit) extends Domain[LocalTime]:

    override def succ(x: LocalTime): LocalTime =
      x.plus(1, unit)

    override def pred(x: LocalTime): LocalTime =
      x.minus(1, unit)

    override def count(start: LocalTime, end: LocalTime): Long =
      start.until(end.plus(1, unit), unit)

    override def compare(x: LocalTime, y: LocalTime): Int =
      x.compareTo(y)

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
