package com.github.gchudnov.mtg.diagram.internal.output

import com.github.gchudnov.mtg.diagram.internal.OutputFormat

import java.time.*

/**
 * OutputFormat for LocalTime
 */
private[internal] final class LocalTimeOutputFormat extends OutputFormat[LocalTime]:

  override def pattern: String =
    "%H:%M:%S"
