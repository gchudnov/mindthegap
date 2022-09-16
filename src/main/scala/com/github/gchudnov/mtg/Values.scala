package com.github.gchudnov.mtg

import java.time.OffsetDateTime
import java.time.temporal.TemporalUnit
import java.time.Instant

object Values:

  /**
   * Integral Value: Int, Long, ...
   */
  private final class IntegralValue[T: Integral] extends Value[T]:

    override def succ(x: T): T =
      val intT = summon[Integral[T]]
      intT.plus(x, intT.one)

    override def pred(x: T): T =
      val intT = summon[Integral[T]]
      intT.minus(x, intT.one)

  /**
   * Fractional Value: Double, Float
   */
  private final class FractionalValue[T: Fractional](precision: T) extends Value[T]:

    override def succ(x: T): T =
      val fracT = summon[Fractional[T]]
      fracT.plus(x, precision)

    override def pred(x: T): T =
      val fracT = summon[Fractional[T]]
      fracT.minus(x, precision)

  /**
   * OffsetDateTime Value
   */
  private final class OffsetDateTimeValue(unit: TemporalUnit) extends Value[OffsetDateTime]:

    override def succ(x: OffsetDateTime): OffsetDateTime =
      x.plus(1, unit)

    override def pred(x: OffsetDateTime): OffsetDateTime =
      x.minus(1, unit)

  /**
   * Instant Value
   */
  private final class InstantValue(unit: TemporalUnit) extends Value[Instant]:

    override def succ(x: Instant): Instant =
      x.plus(1, unit)

    override def pred(x: Instant): Instant =
      x.minus(1, unit)

  /**
   * Implicits
   */
  given integralValue[T: Integral]: Value[T] =
    new IntegralValue()

  def fractionalValue[T: Fractional](precision: T): Value[T] =
    new FractionalValue(precision)

  def offsetDateTimeValue(unit: TemporalUnit): Value[OffsetDateTime] =
    new OffsetDateTimeValue(unit)

  def instantValue(unit: TemporalUnit): Value[Instant] =
    new InstantValue(unit)
