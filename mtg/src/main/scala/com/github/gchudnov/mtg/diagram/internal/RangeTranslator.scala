package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.diagram.Translator
import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.diagram.View

/**
 * Translates the interval to the canvas.
 */
private[mtg] final class RangeTranslator[T: Domain](view: View.Range[T], canvas: Canvas) extends Translator[T]:

  private val k: Double = (canvas.size - 1).toDouble / (view.size - 1).toDouble

  override def translate(value: Value[T]): Int =
    value match
      case Value.InfNeg =>
        canvas.left
      case Value.InfPos =>
        canvas.right
      case Value.Finite(x) =>
        val dx = summon[Domain[T]].count(view.left, x) - 1
        val dd = dx.toDouble
        Canvas.align((k * dd) + canvas.first)
