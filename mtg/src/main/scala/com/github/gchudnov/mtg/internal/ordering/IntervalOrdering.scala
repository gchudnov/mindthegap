package com.github.gchudnov.mtg.internal.ordering

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

/**
 * Ordering of the intervals.
 *
 * Intervals are ordered:
 *
 * {{{
 *   - if a- < b+ then a < b
 *     if a- == b+ then
 *       - if a+ < b+ then a < b
 *         if a+ == b+ then a == b
 *         else a > b
 *     else a > b
 * }}}
 */
private[mtg] final class IntervalOrdering[T: Domain] extends Ordering[Interval[T]]:

  override def compare(a: Interval[T], b: Interval[T]): Int =
    val ordE = summon[Domain[T]].ordEndpoint
    ordE.compare(a.leftEndpoint, b.leftEndpoint) match
      case -1 =>
        -1
      case 0 =>
        ordE.compare(a.rightEndpoint, b.rightEndpoint)
      case _: Int =>
        1
