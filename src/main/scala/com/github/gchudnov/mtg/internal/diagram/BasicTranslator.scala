package com.github.gchudnov.mtg.internal.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Translator
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.View
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
import scala.collection.mutable.ListBuffer

/**
  * Translates the interval to the canvas.
  */
private[mtg] final class BasicTranslator[T: Domain](view: View[T], canvas: Canvas)(using Ordering[Mark[T]]) extends Translator[T]:

  private val ok: Option[Double] =
    view.size.map(vsz => canvas.size.toDouble / vsz.toDouble)

  override def translate(i: Interval[T]): Span =
    if i.isEmpty then Span.empty
    else if i.isPoint then
      val p = translateValue(i.left.eval)
      Span(x0 = p, x1 = p, includeX0 = true, includeX1 = true)
    else Span.make(x0 = translateLeft(i.left), x1 = translateRight(i.right), includeX0 = includeLeft(i.left), includeX1 = includeRight(i.right))

  private def translateValue(value: Value[T]): Int =
    value match
      case Value.InfNeg =>
        canvas.left
      case Value.InfPos =>
        canvas.right
      case Value.Finite(x) =>
        val k    = ok.get        // NOTE: it is not possible to have k undefined if we have at least one Finite point
        val domT = summon[Domain[T]]
        val left = view.left.get // NOTE: .get is safe, otherwise `k` would be None
        val dx   = domT.count(left, x)
        val dd   = dx.toDouble
        Canvas.align((k * dd) + canvas.first)

  private def translateLeft(left: Mark[T]): Int =
    left match
      case Mark.At(x) =>
        translateValue(x)
      case Mark.Succ(xx) =>
        translateValue(xx.eval)
      case xx @ Mark.Pred(_) =>
        translateValue(xx.eval)

  private def translateRight(right: Mark[T]): Int =
    right match
      case Mark.At(y) =>
        translateValue(y)
      case yy @ Mark.Succ(_) =>
        translateValue(yy.eval)
      case Mark.Pred(yy) =>
        translateValue(yy.eval)

  private def includeLeft(left: Mark[T]): Boolean =
    left match
      case Mark.At(x) =>
        x.isFinite
      case Mark.Succ(_) =>
        false
      case xx @ Mark.Pred(_) =>
        xx.eval.isFinite

  private def includeRight(right: Mark[T]): Boolean =
    right match
      case Mark.At(y) =>
        y.isFinite
      case yy @ Mark.Succ(_) =>
        yy.eval.isFinite
      case Mark.Pred(_) =>
        false
