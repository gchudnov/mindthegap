package com.github.gchudnov.mtg

import java.time.OffsetDateTime
import java.time.temporal.TemporalUnit

object Values:

  /**
   * Integral Value: Int, Long, ...
   */
  final class IntegralValue[T: Integral] extends Value[T]:

    override def succ(x: T): T =
      val intT = summon[Integral[T]]
      intT.plus(x, intT.one)

    override def pred(x: T): T =
      val intT = summon[Integral[T]]
      intT.minus(x, intT.one)

  /**
   * Double Value
   */
  final class DoubleValue(precision: Double) extends Value[Double]:

    override def succ(x: Double): Double =
      x + precision

    override def pred(x: Double): Double =
      x - precision

  /**
   * OffsetDateTime Value
   */
  final class OffsetDateTimeValue(unit: TemporalUnit) extends Value[OffsetDateTime]:

    override def succ(x: OffsetDateTime): OffsetDateTime =
      x.plus(1, unit)

    override def pred(x: OffsetDateTime): OffsetDateTime =
      x.minus(1, unit)
