package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Diagram
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.diagram.internal.AsciiRenderer

/**
 * Renderer
 */
trait Renderer:
  def render(d: Diagram, theme: Theme): List[String]

object Renderer:

  given defaultRenderer: Renderer =
    ascii

  def ascii: Renderer =
    new AsciiRenderer()
