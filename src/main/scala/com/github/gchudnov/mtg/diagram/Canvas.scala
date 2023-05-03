package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Value

// TODO: should canvas be a trait instead???

/**
 * Canvas
 *
 * Specifies the width of the text buffer to draw a diagram on.
 */
final case class Canvas(
  width: Int,
  padding: Int
):
  val left: Int  = 0         // start of the canvas, inclusive
  val right: Int = width - 1 // end of the canvas, inclusive

  // TODO: instead of first, last, use paddedLeft, passedRight or something similar ???

  val first: Int = left + padding  // first offset for non-inf value
  val last: Int  = right - padding // last offset for non-inf value

  val size: Int = last - first + 1

  println(("left, right, first, last, size", left, right, first, last, size))

  // TODO: rename to `contains` or `includes` ???
  def isIn(x: Int): Boolean =
    (x >= 0 && x < width)

object Canvas:

  // TODO: define default width and passing, use them here

  val default: Canvas =
    Canvas(
      width = 40,
      padding = 2
    )

  /**
   * Make a new Canvas
   *
   * @param width
   *   width of the canvas
   * @param padding
   *   left and right padding between -inf and +inf and value on a canvas
   * @return
   *   canvas
   */
  def make(width: Int, padding: Int = 2): Canvas =
    Canvas(width = width, padding = padding)

  // TODO: move the function vvv to the place it is used 

  /**
   * Align value to the grid
   */
  private[mtg] def align(value: Double): Int =
    value.round.toInt
