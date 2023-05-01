package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Diagram.Label
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Show

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

  val first: Int = left + padding  // first offset for non-inf value
  val last: Int  = right - padding // last offset for non-inf value

  val size: Int = last - first

  /**
   * Make a label so that it is visible on the canvas.
   */
  def label(x: Int, text: String): Label =
    val p = Canvas.align(x.toDouble - (text.size.toDouble / 2.0))
    val q = p + text.size

    // if label just slightly out of the bounds, make it visible
    val x1 =
      if p < 0 && isIn(x) then 0
      else if q >= width && isIn(x) then width - text.size
      else p

    Label(x1, text)

  def labels[T: Domain](i: Interval[T], span: Span)(using Ordering[Mark[T]]): List[Label] =
    if i.isEmpty then List.empty[Label]
    else if i.isPoint then List(label(span.x0, Show.str(i.left.eval)))
    else
      List(
        label(span.x0, toLeftLabel(i.left)),
        label(span.x1, toRightLabel(i.right))
      )

  private def toLeftLabel[T: Domain](left: Mark[T]): String =
    left match
      case Mark.At(x) =>
        Show.str(x)
      case Mark.Succ(xx) =>
        Show.str(xx.eval)
      case xx @ Mark.Pred(_) =>
        Show.str(xx.eval)

  private def toRightLabel[T: Domain](right: Mark[T]): String =
    right match
      case Mark.At(y) =>
        Show.str(y)
      case yy @ Mark.Succ(_) =>
        Show.str(yy.eval)
      case Mark.Pred(yy) =>
        Show.str(yy.eval)

  private def isIn(x: Int): Boolean =
    (x >= 0 && x < width)

object Canvas:
  
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

  /**
   * Align value to the grid
   */
  private[mtg] def align(value: Double): Int =
    value.round.toInt
