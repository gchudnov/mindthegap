package com.github.gchudnov.mtg.diagram

/**
 * Renderer
 */
trait Renderer:
  def render(d: Diagram, theme: Theme): List[String]
