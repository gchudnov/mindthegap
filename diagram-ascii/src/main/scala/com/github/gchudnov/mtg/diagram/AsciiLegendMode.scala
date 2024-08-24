package com.github.gchudnov.mtg.diagram

enum AsciiLegendMode(val hasLegend: Boolean, val hasAnnotations: Boolean):
  case None        extends AsciiLegendMode(false, false)
  case Legend      extends AsciiLegendMode(true, false)
  case Annotations extends AsciiLegendMode(false, true)
  case Both        extends AsciiLegendMode(true, true)
