package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.diagram.Viewport
import com.github.gchudnov.mtg.diagram.internal.translator.*

/**
 * Translator
 */
private[internal] trait AsciiTranslator[T]:
  def translate(i: Value[T]): Int

private[internal] object AsciiTranslator:
  def make[T: Domain](view: Viewport[T], canvas: AsciiCanvas): AsciiTranslator[T] =
    view match
      case Viewport.Finite(_, _) =>
        new FiniteAsciiTranslator[T](view.asInstanceOf[Viewport.Finite[T]], canvas)
      case Viewport.Infinite =>
        new InfiniteAsciiTranslator[T](canvas)
