package com.github.gchudnov.mtg.internal.translator

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.diagram.Translator
import com.github.gchudnov.mtg.internal.AsciiCanvas
import com.github.gchudnov.mtg.diagram.View

/**
 * Translates the interval to the canvas.
 */
private[mtg] final class RangeTranslator[T: Domain](view: View.Range[T], canvas: AsciiCanvas) extends Translator[T]:

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
        AsciiCanvas.align((k * dd) + canvas.paddedLeft)
