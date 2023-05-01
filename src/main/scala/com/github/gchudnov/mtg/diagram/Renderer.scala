package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Diagram
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.diagram.internal.BasicRenderer

/**
 * Renderer
 */
trait Renderer:
  def render(d: Diagram, theme: Theme): List[String]

object Renderer:

  given defaultRenderer: Renderer = 
    make

  def make: Renderer =
    new BasicRenderer()
