package com.github.gchudnov.mtg.ordering

import java.time.Instant

/**
 * Instant Ordering
 */
given InstantOrdering: Ordering[Instant] with
  override def compare(x: Instant, y: Instant): Int =
    x.compareTo(y)
