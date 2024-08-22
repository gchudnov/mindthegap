package com.github.gchudnov.mtg.diagram.internal.translator

import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.diagram.internal.AsciiTranslator
import com.github.gchudnov.mtg.diagram.internal.AsciiCanvas

// TODO: AsciiTranslator
private[internal] final class InfiniteAsciiTranslator[T](canvas: AsciiCanvas) extends AsciiTranslator[T]:

  override def translate(value: Value[T]): Int =
    value match
      case Value.InfNeg =>
        canvas.left
      case Value.InfPos =>
        canvas.right
      case Value.Finite(_) =>
        sys.error(s"Unexpected value on an Infinite View: ${value}")
