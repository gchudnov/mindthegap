package com.github.gchudnov.mtg.diagram

/**
 * Renderer
 */
trait Renderer:
  def render(d: Diagram): Unit
