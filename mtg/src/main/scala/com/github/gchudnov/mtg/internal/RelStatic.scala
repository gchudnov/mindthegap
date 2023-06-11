package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

/**
 * Static Interval Relations
 */
private[mtg] transparent trait RelStatic:

  /**
   * Relation: Before, Precedes (b)
   */
  final def before[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    a.before(b)
