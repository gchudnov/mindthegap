package com.github.gchudnov.mtg.diagram.internal.formatters

import com.github.gchudnov.mtg.diagram.internal.InputFormat

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputFormat for LocalDateTime
 */
private[internal] final class LocalDateTimeInputFormat extends InputFormat[LocalDateTime]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS")

  override def pattern: String =
    "YYYY-MM-DD HH:mm:ss.SSS"

  override def format(value: LocalDateTime): String =
    value.format(formatter)
