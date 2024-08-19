package com.github.gchudnov.mtg.diagram.internal

import java.time.*
import com.github.gchudnov.mtg.diagram.internal.formatters.*

/**
 * InputDate
 *
 * Represents an input date that can be used in the MermaidJS diagram.
 *
 * To allow Domain type T to be used with mermadjs, you need to provide an instance of InputDate[T].
 */
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

  given instantInputDate: InputDate[Instant] =
    new InstantInputDate

  given localDateTimeInputDate: InputDate[LocalDateTime] =
    new LocalDateTimeInputDate

  given localDateInputDate: InputDate[LocalDate] =
    new LocalDateInputDate
