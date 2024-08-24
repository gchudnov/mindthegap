package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain

/**
 * Renderer
 */
trait Renderer[T]:
  def render(d: Diagram[T], v: Viewport[T]): Unit

  def render(d: Diagram[T]): Unit =
    render(d, Viewport.Infinite)
