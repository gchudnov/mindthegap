package com.github.gchudnov.mtg.diagram

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

  // TODO: ^^^ what kind of pos is that? is that posX, posY ???? clarify in the name

  def size: Int =
    value.size

object Label:
  lazy val empty: Label =
    Label(0, "")

  /**
   * Make a label so that it is visible on the canvas.
   */
  def make(c: Canvas, x: Int, text: String): Label =
    val p = Canvas.align(x.toDouble - (text.size.toDouble / 2.0))
    val q = p + text.size

    // if label just slightly out of the bounds, make it visible
    val x1 =
      if p < 0 && c.isIn(x) then 0
      else if q >= c.width && c.isIn(x) then c.width - text.size
      else p

    Label(x1, text)
