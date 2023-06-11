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
   * 
   * @see
   *   [[BeforeAfter.before]]
   */
  final def before[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    BeforeAfter.before(a, b)

  /**
   * Relation: After, IsPrecededBy (B)
   * 
   * @see
   *   [[BeforeAfter.after]]
   */
  final def after[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    BeforeAfter.after(a, b)

  /**
   * Meets (m)
   *
   * @see
   *   [[MeetsIsMetBy.meets]]
   */
  final def meets[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    MeetsIsMetBy.meets(a, b)

  /**
   * IsMetBy (M)
   * 
   * @see
   *   [[MeetsIsMetBy.meets]]
   */
  final def isMetBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    MeetsIsMetBy.isMetBy(a, b)
