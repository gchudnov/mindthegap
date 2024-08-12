package com.github.gchudnov.mtg.internal.ordering

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Endpoint

/**
 * Ordering of the endpoints.
 */
private[mtg] final class EndpointOrdering[T: Domain] extends Ordering[Endpoint[T]]:

  override def compare(x: Endpoint[T], y: Endpoint[T]): Int =
    summon[Domain[T]].ordValue.compare(x.eval, y.eval)
