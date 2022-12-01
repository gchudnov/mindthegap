package com.github.gchudnov.mtg

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 * Default Domains
 */
private[mtg] trait DomainDefaults:
  import DomainDefaults.*

  given nothingDomain: Domain[Nothing] =
    new NothingDomain

  given integralDomain[T: Integral]: Domain[T] =
    new IntegralDomain()

  given offsetDateTimeDomain: Domain[OffsetDateTime] =
    new OffsetDateTimeDomain(ChronoUnit.NANOS)

  given instantDomain: Domain[Instant] =
    new InstantDomain(ChronoUnit.NANOS)

  def makeOffsetDateTime(unit: TemporalUnit): Domain[OffsetDateTime] =
    new DomainDefaults.OffsetDateTimeDomain(unit)

  def makeInstant(unit: TemporalUnit): Domain[Instant] =
    new DomainDefaults.InstantDomain(unit)

  def makeFractional[T: Fractional](unit: T): Domain[T] =
    new DomainDefaults.FractionalDomain(unit)

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

  /**
   * Integral Domain: Int, Long, ...
   */
  final class IntegralDomain[T: Integral] extends Domain[T]:

    override def succ(x: T): T =
      val intT = summon[Integral[T]]
      intT.plus(x, intT.one)

    override def pred(x: T): T =
      val intT = summon[Integral[T]]
      intT.minus(x, intT.one)

    override def count(start: T, end: T): Long =
      val intT = summon[Integral[T]]
      intT.toLong(intT.minus(end, start))

  /**
   * Fractional Domain: Double, Float with step size
   */
  final class FractionalDomain[T: Fractional](unit: T) extends Domain[T]:

    override def succ(x: T): T =
      val fracT = summon[Fractional[T]]
      fracT.plus(x, unit)

    override def pred(x: T): T =
      val fracT = summon[Fractional[T]]
      fracT.minus(x, unit)

    override def count(start: T, end: T): Long =
      val fracT = summon[Fractional[T]]
      fracT.toLong(fracT.div(fracT.minus(end, start), unit))

  /**
   * OffsetDateTime Domain
   */
  final class OffsetDateTimeDomain(unit: TemporalUnit) extends Domain[OffsetDateTime]:

    override def succ(x: OffsetDateTime): OffsetDateTime =
      x.plus(1, unit)

    override def pred(x: OffsetDateTime): OffsetDateTime =
      x.minus(1, unit)

    override def count(start: OffsetDateTime, end: OffsetDateTime): Long =
      start.until(end, unit)

  /**
   * Instant Domain
   */
  final class InstantDomain(unit: TemporalUnit) extends Domain[Instant]:

    override def succ(x: Instant): Instant =
      x.plus(1, unit)

    override def pred(x: Instant): Instant =
      x.minus(1, unit)

    override def count(start: Instant, end: Instant): Long =
      start.until(end, unit)
