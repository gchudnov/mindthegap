package com.github.gchudnov.mtg.diagram.internal.output

import com.github.gchudnov.mtg.diagram.OutputFormat

import java.time.*

/**
 * OutputFormat for LocalDateTime
 */
private[internal] final class LocalDateTimeOutputFormat extends OutputFormat[LocalDateTime]:

  override def pattern: String =
    "%d.%m.%Y %H:%M:%S"
