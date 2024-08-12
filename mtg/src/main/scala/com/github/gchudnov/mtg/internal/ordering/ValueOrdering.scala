package com.github.gchudnov.mtg.internal.ordering

import com.github.gchudnov.mtg.Value

/**
  * Ordering of the values.
  */
private[mtg] final class ValueOrdering[T: Ordering] extends Ordering[Value[T]]:

  override def compare(x: Value[T], y: Value[T]): Int =
    (x, y) match
      case (Value.Finite(u), Value.Finite(v)) =>
        summon[Ordering[T]].compare(u, v)
      case (Value.Finite(_), Value.InfNeg) =>
        1
      case (Value.Finite(_), Value.InfPos) =>
        -1
      case (Value.InfNeg, Value.Finite(_)) =>
        -1
      case (Value.InfNeg, Value.InfNeg) =>
        0
      case (Value.InfNeg, Value.InfPos) =>
        -1
      case (Value.InfPos, Value.Finite(_)) =>
        1
      case (Value.InfPos, Value.InfNeg) =>
        1
      case (Value.InfPos, Value.InfPos) =>
        0
