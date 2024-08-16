package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.internal.translator.RangeTranslator
import com.github.gchudnov.mtg.internal.Canvas

/**
 * Translator
 */
trait Translator[T]:
  def translate(i: Value[T]): Int

object Translator:
  def make[T: Domain](view: View[T], canvas: Canvas): Translator[T] =
    view match
      case View.Finite(_, _) =>
        new RangeTranslator[T](view.asInstanceOf[View.Finite[T]], canvas)
      case View.Infinite =>
        new InfiniteTranslator[T](canvas)
