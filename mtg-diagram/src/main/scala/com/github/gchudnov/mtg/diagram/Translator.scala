package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Value

/**
 * Translator
 */
trait Translator[T]:
  def translate(i: Value[T]): Int

object Translator:
  def make[T: Domain](view: View[T], canvas: Canvas): Translator[T] =
    view match
      case View.Finite(_, _) =>
        new FiniteTranslator[T](view.asInstanceOf[View.Finite[T]], canvas)
      case View.Infinite =>
        new InfiniteTranslator[T](canvas)
