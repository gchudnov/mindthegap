package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.diagram.internal.RangeTranslator
import com.github.gchudnov.mtg.diagram.internal.InfiniteTranslator

/**
 * Translator
 */
trait Translator[T]:
  def translate(i: Value[T]): Int

object Translator:
  def make[T: Domain](view: View[T], canvas: Canvas): Translator[T] =
    view match
      case View.Range(_, _) =>
        new RangeTranslator[T](view.asInstanceOf[View.Range[T]], canvas)
      case View.Infinite =>
        new InfiniteTranslator[T](canvas)
