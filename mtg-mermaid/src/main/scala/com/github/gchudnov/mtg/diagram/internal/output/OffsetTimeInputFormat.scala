package com.github.gchudnov.mtg.diagram.internal.output

import com.github.gchudnov.mtg.diagram.internal.OutputFormat
import java.time.*

/**
 * OutputFormat for OffsetTime
 */
private[internal] final class OffsetTimeOutputFormat extends OutputFormat[OffsetTime]:
  override def pattern: String =
    "%H:%M:%S"
