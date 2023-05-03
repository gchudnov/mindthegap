package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.diagram.Translator
import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram.View

/**
 * Translates the interval to the canvas.
 */
private[mtg] final class BasicTranslator[T: Domain](view: View[T], canvas: Canvas)(using Ordering[Mark[T]]) extends Translator[T]:

  /**
    * Answers the question: How many units of the canvas is one unit of the view?
    */
  private val ok: Option[Double] =
    view.size.map(vsz => (canvas.size - 1).toDouble / (vsz - 1).toDouble)

  println(("view.size", view.size))
  println(("canvas.size", canvas.size))
  println(("ok", ok))

  // TODO: if the translator is used for finite intervals, ok, view are always defined
  //       it might be worth to create several translators for different cases

  override def translate(value: Value[T]): Int =
    println(("translateValue", value))
    value match
      case Value.InfNeg =>
        canvas.left
      case Value.InfPos =>
        canvas.right
      case Value.Finite(x) =>
        val k    = ok.get        // NOTE: it is not possible to have k undefined if we have at least one Finite point
        val domT = summon[Domain[T]]
        val left = view.left.get // NOTE: .get is safe, otherwise `k` would be None
        val dx   = domT.count(left, x) - 1
        val dd   = dx.toDouble
        Canvas.align((k * dd) + canvas.first)
