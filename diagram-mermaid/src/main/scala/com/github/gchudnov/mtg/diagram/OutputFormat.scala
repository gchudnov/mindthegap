package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.diagram.internal.output.*
import com.github.gchudnov.mtg.diagram.internal.OutputFormatLowPriority

/**
 * Output Format for rendering intervals.
 *
 * see https://mermaid.js.org/syntax/gantt.html#output-date-format-on-the-axis
 */
trait OutputFormat[T]:
  def pattern: String

object OutputFormat extends OutputFormatLowPriority
