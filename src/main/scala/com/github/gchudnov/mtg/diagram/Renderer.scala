package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Diagram
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.diagram.internal.BasicRenderer

/**
 * Renderer
 */
trait Renderer:
  def render(d: Diagram): List[String]

object Renderer:
  def make(theme: Theme): Renderer =
    new BasicRenderer(theme)
