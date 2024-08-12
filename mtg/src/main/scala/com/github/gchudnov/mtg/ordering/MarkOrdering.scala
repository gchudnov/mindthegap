package com.github.gchudnov.mtg.ordering

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Endpoint

final class MarkOrdering[T: Domain] extends Ordering[Endpoint[T]]:

  override def compare(x: Endpoint[T], y: Endpoint[T]): Int =
    summon[Domain[T]].ordValue.compare(x.eval, y.eval)
