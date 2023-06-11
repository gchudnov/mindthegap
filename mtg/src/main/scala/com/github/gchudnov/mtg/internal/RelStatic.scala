package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.rel.*

/**
 * Static Interval Relations
 */
private[mtg] transparent trait RelStatic:

  /**
   * Relation: Before, Precedes (b)
   */
  final def before[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    BeforeAfter.before(a, b)

  /**
   * Relation: After, IsPrecededBy (B)
   */
  final def after[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    BeforeAfter.after(a, b)
