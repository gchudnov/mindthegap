package com.github.gchudnov.mtg.ordering

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Mark

final class MarkOrdering[T: Domain] extends Ordering[Mark[T]]:

  override def compare(x: Mark[T], y: Mark[T]): Int =
    summon[Domain[T]].ordValue.compare(x.eval, y.eval)
