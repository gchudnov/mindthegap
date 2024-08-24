package com.github.gchudnov.mtg.diagram.internal.input

import com.github.gchudnov.mtg.diagram.internal.InputFormat

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputFormat for OffsetTime
 */
private[internal] final class OffsetTimeInputFormat extends InputFormat[OffsetTime]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

  override def pattern: String =
    "HH:mm:ss.SSS"

  override def format(value: OffsetTime): String =
    value.format(formatter)
