package com.github.gchudnov.mtg.diagram.internal.formatters

import com.github.gchudnov.mtg.diagram.internal.InputDate

import java.time.*
import java.time.format.DateTimeFormatter

/**
 * InputDate for LocalDate
 */
private[internal] final class LocalDateInputDate extends InputDate[LocalDate]:
  private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("YYYY-MM-dd")

  override def pattern: String =
    "YYYY-MM-DD"

  override def format(value: LocalDate): String =
    value.format(formatter)
