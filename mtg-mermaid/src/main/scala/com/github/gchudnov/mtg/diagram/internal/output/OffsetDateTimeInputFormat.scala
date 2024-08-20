package com.github.gchudnov.mtg.diagram.internal.output

import com.github.gchudnov.mtg.diagram.internal.OutputFormat

import java.time.*

/**
 * OutputFormat for OffsetDateTime
 */
private[internal] final class OffsetDateTimeOutputFormat extends OutputFormat[OffsetDateTime]:

  override def pattern: String =
    "%d.%m.%Y %H:%M:%S"
