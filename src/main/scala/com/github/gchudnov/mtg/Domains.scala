package com.github.gchudnov.mtg

import java.time.OffsetDateTime
import java.time.temporal.TemporalUnit
import java.time.Instant

object Domains:

  private final class NothingDomain extends Domain[Nothing]:

    override def succ(x: Nothing): Nothing =
      x

    override def pred(x: Nothing): Nothing =
      x

  /**
   * Integral Domain: Int, Long, ...
   */
  private final class IntegralDomain[T: Integral] extends Domain[T]:

    override def succ(x: T): T =
      val intT = summon[Integral[T]]
      intT.plus(x, intT.one)

    override def pred(x: T): T =
      val intT = summon[Integral[T]]
      intT.minus(x, intT.one)

  /**
   * Fractional Domain: Double, Float
   */
  private final class FractionalDomain[T: Fractional](unit: T) extends Domain[T]:

    override def succ(x: T): T =
      val fracT = summon[Fractional[T]]
      fracT.plus(x, unit)

    override def pred(x: T): T =
      val fracT = summon[Fractional[T]]
      fracT.minus(x, unit)

  /**
   * OffsetDateTime Domain
   */
  private final class OffsetDateTimeDomain(unit: TemporalUnit) extends Domain[OffsetDateTime]:

    override def succ(x: OffsetDateTime): OffsetDateTime =
      x.plus(1, unit)

    override def pred(x: OffsetDateTime): OffsetDateTime =
      x.minus(1, unit)

  /**
   * Instant Domain
   */
  private final class InstantDomain(unit: TemporalUnit) extends Domain[Instant]:

    override def succ(x: Instant): Instant =
      x.plus(1, unit)

    override def pred(x: Instant): Instant =
      x.minus(1, unit)

  /**
   * Implicits
   */
  given nothingDomain: Domain[Nothing] =
    new NothingDomain

  given integralDomain[T: Integral]: Domain[T] =
    new IntegralDomain()

  def fractionalDomain[T: Fractional](unit: T): Domain[T] =
    new FractionalDomain(unit)

  def offsetDateTimeDomain(unit: TemporalUnit): Domain[OffsetDateTime] =
    new OffsetDateTimeDomain(unit)

  def instantDomain(unit: TemporalUnit): Domain[Instant] =
    new InstantDomain(unit)
