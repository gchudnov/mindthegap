package com.github.gchudnov.mtg.diagram

/**
 * ASCII Canvas
 *
 * Specifies the width of the text buffer to draw a diagram on.
 */
final case class AsciiCanvas(
  width: Int,
  padding: Int,
):
  val left: Int  = 0         // start of the canvas, inclusive
  val right: Int = width - 1 // end of the canvas, inclusive

  val paddedLeft: Int  = left + padding  // first offset for non-inf value
  val paddedRight: Int = right - padding // last offset for non-inf value

  val size: Int = paddedRight - paddedLeft + 1

  def contains(x: Int): Boolean =
    (x >= 0 && x < width)

object AsciiCanvas:

  private val defaultWidth: Int   = 40
  private val defaultPadding: Int = 2

  lazy val default: AsciiCanvas =
    AsciiCanvas(
      width = defaultWidth,
      padding = defaultPadding,
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
  def make(width: Int, padding: Int = defaultPadding): AsciiCanvas =
    require(width > 0, "width should be positive")
    require(padding >= 0, "padding should be non-negative")
    require(padding < width / 2, "padding should be less than half of the width")

    AsciiCanvas(
      width = width,
      padding = padding,
    )

  /**
   * Align value to the grid
   */
  private[mtg] def align(value: Double): Int =
    value.round.toInt
