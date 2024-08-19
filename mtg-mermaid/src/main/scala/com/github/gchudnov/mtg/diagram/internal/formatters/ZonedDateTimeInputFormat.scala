package com.github.gchudnov.mtg.diagram.internal.formatters

import com.github.gchudnov.mtg.diagram.internal.InputFormat

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputFormat for ZonedDateTime
 */
private[internal] final class ZonedDateTimeInputFormat extends InputFormat[ZonedDateTime]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS")

  override def pattern: String =
    "YYYY-MM-dd HH:mm:ss.SSS"

  override def format(value: ZonedDateTime): String =
    val utcValue = value.withZoneSameInstant(ZoneOffset.UTC)
    utcValue.format(formatter)
