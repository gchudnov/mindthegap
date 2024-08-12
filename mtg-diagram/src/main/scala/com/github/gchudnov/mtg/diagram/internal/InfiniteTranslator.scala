package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.diagram.Translator
import com.github.gchudnov.mtg.diagram.{Canvas}

private[mtg] final class InfiniteTranslator[T: Domain](canvas: Canvas) extends Translator[T]:

  override def translate(value: Value[T]): Int =
    value match
      case Value.InfNeg =>
        canvas.left
      case Value.InfPos =>
        canvas.right
      case Value.Finite(_) =>
        sys.error(s"Unexpected value on an Infinite View: ${value}")
