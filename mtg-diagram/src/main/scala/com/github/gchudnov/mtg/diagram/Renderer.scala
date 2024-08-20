package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain

/**
 * Renderer
 */
trait Renderer[T]:
  def render(d: Diagram[T]): Unit
