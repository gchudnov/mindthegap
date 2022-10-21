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

private[mtg] object NumericDefaults:

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
