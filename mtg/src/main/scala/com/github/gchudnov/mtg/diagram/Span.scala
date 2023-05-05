package com.github.gchudnov.mtg.diagram

/**
 * Span
 *
 * Used to draw an interval on the canvas.
 *
 *   - NOTE: Use negative span for an empty interval
 *   - NOTE: The span is inclusive on both ends: [x0, x1].
 *   - NOTE: includeX0 and includeX1 are used to draw begin and end of the span and do not affect the size of the span.
 */
final case class Span(x0: Int, x1: Int, includeX0: Boolean, includeX1: Boolean):

  def isEmpty: Boolean =
    x1 < x0

  def nonEmpty: Boolean =
    !isEmpty

  def size: Int =
    x1 - x0 + 1

object Span:
  lazy val empty: Span =
    make(1, -1, true, true)

  def make(x0: Int, x1: Int, includeX0: Boolean, includeX1: Boolean): Span =
    Span(x0, x1, includeX0, includeX1)
