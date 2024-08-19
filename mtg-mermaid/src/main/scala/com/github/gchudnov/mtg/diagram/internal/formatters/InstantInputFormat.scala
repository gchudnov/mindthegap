package com.github.gchudnov.mtg.diagram.internal.formatters

import com.github.gchudnov.mtg.diagram.internal.InputFormat

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputFormat for Instant
 */
private[internal] final class InstantInputFormat extends InputFormat[Instant]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss.SSS")

  override def pattern: String =
    "YYYY-MM-DD HH:mm:ss.SSS"

  override def format(value: Instant): String =
    val utcValue = value.atOffset(ZoneOffset.UTC)
    utcValue.format(formatter)
