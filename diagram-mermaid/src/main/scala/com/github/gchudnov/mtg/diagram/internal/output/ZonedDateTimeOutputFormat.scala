package com.github.gchudnov.mtg.diagram.internal.output

import com.github.gchudnov.mtg.diagram.OutputFormat
import java.time.*

/**
 * OutputFormat for ZonedDateTime
 */
private[internal] final class ZonedDateTimeOutputFormat extends OutputFormat[ZonedDateTime]:

  override def pattern: String =
    "%d.%m.%Y %H:%M:%S"
