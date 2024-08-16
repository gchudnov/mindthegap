package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain

/**
 * Renderer
 */
trait Renderer:
  def render[T: Domain](d: Diagram[T]): Unit
