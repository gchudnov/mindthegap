package com.github.gchudnov.mtg.diagram.internal.output

import com.github.gchudnov.mtg.diagram.OutputFormat

import java.time.*

/**
 * OutputFormat for LocalDate
 */
private[internal] final class LocalDateOutputFormat extends OutputFormat[LocalDate]:

  override def pattern: String =
    "%d.%m.%Y"
