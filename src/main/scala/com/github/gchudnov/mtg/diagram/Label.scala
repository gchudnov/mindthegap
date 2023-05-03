package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Diagram.Label
import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Show

/**
 * Label
 */
final case class Label(pos: Int, value: String):
  def size: Int =
    value.size

object Label:
  lazy val empty: Label =
    Label(0, "")

  /**
   * Make a label so that it is visible on the canvas.
   */
  private def make(c: Canvas, x: Int, text: String): Label =
    val p = Canvas.align(x.toDouble - (text.size.toDouble / 2.0))
    val q = p + text.size

    // if label just slightly out of the bounds, make it visible
    val x1 =
      if p < 0 && c.isIn(x) then 0
      else if q >= c.width && c.isIn(x) then c.width - text.size
      else p

    Label(x1, text)

  // TODO: extract this function to Domain, it is not specific to Label but has some knowledge about Intervals and Spans

  def make[T: Domain](c: Canvas, i: Interval[T], span: Span)(using Ordering[Mark[T]]): List[Label] =
    if i.isEmpty then List.empty[Label]
    else if i.isPoint then List(make(c, span.x0, Show.str(i.left.eval)))
    else
      List(
        make(c, span.x0, toLeftLabel(i.left)),
        make(c, span.x1, toRightLabel(i.right))
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
