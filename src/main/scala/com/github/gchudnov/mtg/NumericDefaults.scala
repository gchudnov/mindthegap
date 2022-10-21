package com.github.gchudnov.mtg

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.time.ZoneOffset
import scala.util.control.Exception.nonFatalCatch

private[mtg] trait NumericDefaults:
  import NumericDefaults.*

  given offsetDateTimeNumeric: Numeric[OffsetDateTime] =
    new OffsetDateTimeNumeric(ChronoUnit.NANOS)

  given instantNumeric: Numeric[Instant] =
    new InstantNumeric(ChronoUnit.NANOS)

  def makeOffsetDateTimeNumeric(unit: TemporalUnit): Numeric[OffsetDateTime] =
    new OffsetDateTimeNumeric(unit)

  def makeInstantNumeric(unit: TemporalUnit): Numeric[Instant] =
    new InstantNumeric(unit)

private[mtg] object NumericDefaults:

  /**
   * OffsetDateTime Numeric
   */
  final class OffsetDateTimeNumeric(unit: TemporalUnit) extends Numeric[OffsetDateTime]:

    private def fromLong(x: Long): OffsetDateTime =
      OffsetDateTime.ofInstant(Instant.ofEpochSecond(x), ZoneOffset.UTC).truncatedTo(unit)

    override def fromInt(x: Int): OffsetDateTime =
      fromLong(x)

    override def compare(x: OffsetDateTime, y: OffsetDateTime): Int =
      x.compareTo(y)

    override def toDouble(x: OffsetDateTime): Double =
      x.toEpochSecond().toDouble

    override def toFloat(x: OffsetDateTime): Float =
      x.toEpochSecond().toFloat

    override def toInt(x: OffsetDateTime): Int =
      x.toEpochSecond().toInt

    override def times(x: OffsetDateTime, y: OffsetDateTime): OffsetDateTime =
      fromLong(x.toEpochSecond() * y.toEpochSecond())

    override def toLong(x: OffsetDateTime): Long =
      x.toEpochSecond()

    override def minus(x: OffsetDateTime, y: OffsetDateTime): OffsetDateTime =
      fromLong(x.toEpochSecond() - y.toEpochSecond())

    override def negate(x: OffsetDateTime): OffsetDateTime =
      throw new NoSuchElementException("Numeric[OffsetDateTime].negate")

    override def plus(x: OffsetDateTime, y: OffsetDateTime): OffsetDateTime =
      fromLong(x.toEpochSecond() + y.toEpochSecond())

    override def parseString(str: String): Option[OffsetDateTime] =
      nonFatalCatch.opt(OffsetDateTime.parse(str))

  /**
   * Instant Numeric
   */
  final class InstantNumeric(unit: TemporalUnit) extends Numeric[Instant]:

    private def fromLong(x: Long): Instant =
      Instant.ofEpochSecond(x).truncatedTo(unit)

    override def fromInt(x: Int): Instant =
      fromLong(x)

    override def compare(x: Instant, y: Instant): Int =
      x.compareTo(y)

    override def toDouble(x: Instant): Double =
      x.getEpochSecond().toDouble

    override def toFloat(x: Instant): Float =
      x.getEpochSecond().toFloat

    override def toInt(x: Instant): Int =
      x.getEpochSecond().toInt

    override def times(x: Instant, y: Instant): Instant =
      fromLong(x.getEpochSecond() * y.getEpochSecond())

    override def toLong(x: Instant): Long =
      x.getEpochSecond()

    override def minus(x: Instant, y: Instant): Instant =
      fromLong(x.getEpochSecond() - y.getEpochSecond())

    override def negate(x: Instant): Instant =
      throw new NoSuchElementException("Numeric[Instant].negate")

    override def plus(x: Instant, y: Instant): Instant =
      fromLong(x.getEpochSecond() + y.getEpochSecond())

    override def parseString(str: String): Option[Instant] =
      nonFatalCatch.opt(Instant.parse(str))
