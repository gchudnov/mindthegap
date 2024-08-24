package com.github.gchudnov.mtg.diagram.internal.input

import com.github.gchudnov.mtg.diagram.internal.InputFormat

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputFormat for LocalTime
 */
private[internal] final class LocalTimeInputFormat extends InputFormat[LocalTime]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

  override def pattern: String =
    "HH:mm:ss.SSS"

  override def format(value: LocalTime): String =
    value.format(formatter)
