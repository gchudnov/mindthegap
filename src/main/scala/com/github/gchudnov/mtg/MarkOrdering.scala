package com.github.gchudnov.mtg

final class MarkOrdering[T: Domain](using ordT: Ordering[Value[T]]) extends Ordering[Mark[T]]:

  override def compare(x: Mark[T], y: Mark[T]): Int =
    ordT.compare(x.eval, y.eval)
