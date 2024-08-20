package com.github.gchudnov.mtg.diagram.internal

import java.time.*
import com.github.gchudnov.mtg.diagram.internal.output.*

trait OutputFormat[T] {
  def pattern: String
}

object OutputFormat extends OutputFormatLowPriority

/**
 * Formatters for OutputFormat
 */
private[mtg] trait OutputFormatLowPriority:

  given offsetDateTimeOutputFormat: OutputFormat[OffsetDateTime] =
    new OffsetDateTimeOutputFormat

  given offsetTimeOutputFormat: OutputFormat[OffsetTime] =
    new OffsetTimeOutputFormat

  given instantOutputFormat: OutputFormat[Instant] =
    new InstantOutputFormat

  given localDateTimeOutputFormat: OutputFormat[LocalDateTime] =
    new LocalDateTimeOutputFormat

  given localDateOutputFormat: OutputFormat[LocalDate] =
    new LocalDateOutputFormat

  given localTimeOutputFormat: OutputFormat[LocalTime] =
    new LocalTimeOutputFormat

  given zonedDateTimeOutputFormat: OutputFormat[ZonedDateTime] =
    new ZonedDateTimeOutputFormat
