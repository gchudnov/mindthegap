package com.github.gchudnov.mtg.orderings

import java.time.Instant

/**
 * Ordered Instant
 */
given InstantOrdering: Ordering[Instant] with
  override def compare(x: Instant, y: Instant): Int =
    x.compareTo(y)
