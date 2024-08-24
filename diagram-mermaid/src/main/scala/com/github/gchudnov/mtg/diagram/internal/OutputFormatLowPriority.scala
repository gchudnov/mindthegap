package com.github.gchudnov.mtg.diagram.internal

import java.time.*
import com.github.gchudnov.mtg.diagram.OutputFormat
import com.github.gchudnov.mtg.diagram.internal.output.*

/**
 * Formatters for OutputFormat
 */
private[diagram] trait OutputFormatLowPriority:

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
