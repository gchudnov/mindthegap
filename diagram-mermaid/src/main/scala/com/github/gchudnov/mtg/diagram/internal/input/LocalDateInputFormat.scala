package com.github.gchudnov.mtg.diagram.internal.input

import com.github.gchudnov.mtg.diagram.internal.InputFormat

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputFormat for LocalDate
 */
private[internal] final class LocalDateInputFormat extends InputFormat[LocalDate]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("YYYY-MM-dd")

  override def pattern: String =
    "YYYY-MM-DD"

  override def format(value: LocalDate): String =
    value.format(formatter)
