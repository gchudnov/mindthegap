package com.github.gchudnov.mtg.internal.translator

import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.diagram.Translator
import com.github.gchudnov.mtg.internal.AsciiCanvas

private[mtg] final class InfiniteTranslator[T](canvas: AsciiCanvas) extends Translator[T]:

  override def translate(value: Value[T]): Int =
    value match
      case Value.InfNeg =>
        canvas.left
      case Value.InfPos =>
        canvas.right
      case Value.Finite(_) =>
        sys.error(s"Unexpected value on an Infinite View: ${value}")
