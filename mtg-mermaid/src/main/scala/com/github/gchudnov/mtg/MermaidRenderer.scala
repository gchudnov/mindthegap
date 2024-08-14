package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.diagram.Diagram
import com.github.gchudnov.mtg.diagram.Renderer

/**
 * Mermaid Renderer
 *
 * Renders a diagram as a Mermaid diagram.
 */
private[mtg] final class MermaidRenderer extends Renderer:
  override def render(d: Diagram): Unit =
    ???

object MermaidRenderer:
  def make(): MermaidRenderer =
    new MermaidRenderer()

// https://mermaid.js.org/syntax/gantt.html
