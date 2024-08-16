package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.diagram.Diagram
import com.github.gchudnov.mtg.diagram.Renderer

/**
 * Mermaid Renderer
 *
 * Renders a diagram as a Mermaid diagram.
 */
private[mtg] final class MermaidRenderer extends Renderer:
  override def render[T: Domain](d: Diagram[T]): Unit =
    ???

object MermaidRenderer:
  def make(): MermaidRenderer =
    new MermaidRenderer()

// https://mermaid.js.org/syntax/gantt.html

/**
TODO: add diagrams for mermaid -- start, check what we need generalize with ascii renderer
      Canvas ???
*/
