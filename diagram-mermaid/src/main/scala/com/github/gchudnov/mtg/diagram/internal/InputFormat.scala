package com.github.gchudnov.mtg.diagram.internal

import java.time.*
import com.github.gchudnov.mtg.diagram.internal.input.*

/**
 * InputFormat
 *
 * Represents an input date that can be used in the MermaidJS diagram.
 *
 * To allow Domain type T to be used with MermaidJs, you need to provide an instance of InputFormat[T].
 */
private[mtg] trait InputFormat[T]:
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
 * InputFormat Companion Object
 */
private[mtg] object InputFormat extends InputDateLowPriority

/**
 * Formatters for InputFormat
 */
private[mtg] trait InputDateLowPriority:

  given offsetDateTimeInputFormat: InputFormat[OffsetDateTime] =
    new OffsetDateTimeInputFormat

  given offsetTimeInputFormat: InputFormat[OffsetTime] =
    new OffsetTimeInputFormat

  given instantInputFormat: InputFormat[Instant] =
    new InstantInputFormat

  given localDateTimeInputFormat: InputFormat[LocalDateTime] =
    new LocalDateTimeInputFormat

  given localDateInputFormat: InputFormat[LocalDate] =
    new LocalDateInputFormat

  given localTimeInputFormat: InputFormat[LocalTime] =
    new LocalTimeInputFormat

  given zonedDateTimeInputFormat: InputFormat[ZonedDateTime] =
    new ZonedDateTimeInputFormat
