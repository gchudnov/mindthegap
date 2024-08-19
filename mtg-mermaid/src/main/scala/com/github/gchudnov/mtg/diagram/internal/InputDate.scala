package com.github.gchudnov.mtg.diagram.internal

import java.time.*
import com.github.gchudnov.mtg.diagram.internal.formatters.*

private[mtg] trait InputDate[T]:
  /**
   * The pattern of the date for MermaidJS. Used to represent the input date in the diagram.
   *
   * @return
   *   the pattern of the date
   */
  def pattern: String

  /**
   * Format the domain value to the input date string.
   *
   * @param value
   *   the date
   * @return
   *   the formatted date
   */
  def format(value: T): String

/**
 * InputDate Companion Object
 */
private[mtg] object InputDate extends InputDateLowPriority

/**
 * Formatters for InputDate
 */
private[mtg] trait InputDateLowPriority:

  given offsetDateTimeInputDate: InputDate[OffsetDateTime] =
    new OffsetDateTimeInputDate
