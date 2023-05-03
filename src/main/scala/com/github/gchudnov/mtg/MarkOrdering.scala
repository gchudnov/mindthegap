package com.github.gchudnov.mtg

// TODO: extract all ordering classes to Ordering package OR delete this class


final class MarkOrdering[T: Domain] extends Ordering[Mark[T]]:

  override def compare(x: Mark[T], y: Mark[T]): Int =
    summon[Domain[T]].ordValue.compare(x.eval, y.eval)
