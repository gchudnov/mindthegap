package com.github.gchudnov.mtg.diagram.internal.formatters

import com.github.gchudnov.mtg.diagram.internal.InputFormat

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputFormat for OffsetDateTime
 */
private[internal] final class OffsetDateTimeInputFormat extends InputFormat[OffsetDateTime]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS")

  override def pattern: String =
    "YYYY-MM-DD HH:mm:ss.SSS"

  override def format(value: OffsetDateTime): String =
    val utcValue = value.withOffsetSameInstant(ZoneOffset.UTC)
    utcValue.format(formatter)
