package com.github.gchudnov.mtg.diagram.internal.formatters

import com.github.gchudnov.mtg.diagram.internal.InputDate
import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputDate for OffsetDateTime
 */
private[internal] final class OffsetDateTimeInputDate extends InputDate[OffsetDateTime]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS")

  override def pattern: String =
    "YYYY-MM-DD HH:mm:ss.SSS"

  override def format(value: OffsetDateTime): String =
    value.format(formatter)
