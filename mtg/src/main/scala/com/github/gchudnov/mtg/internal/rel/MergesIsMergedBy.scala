package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object MergesIsMergedBy:

  /**
   * Merges
   *
   * Two intervals `a` and `b` can be merged, if they are adjacent or intersect.
   *
   * {{{
   *   a- <= b+
   *   b- <= a+
   *   OR
   *   succ(a+) = b- OR succ(b+) = a-
   *
   *   intersects(a,b) OR isAdjacent(a,b)
   * }}}
   */
  final def merges[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordE = summon[Domain[T]].ordEndpoint
    (a.isEmpty || b.isEmpty) || (
      (ordE.lteq(a.leftEndpoint, b.rightEndpoint) && ordE.lteq(b.leftEndpoint, a.rightEndpoint)) || (ordE.equiv(a.rightEndpoint.succ, b.leftEndpoint) || ordE.equiv(b.rightEndpoint.succ, a.leftEndpoint))
    )

  /**
   * IsMergedBy
   */
  final def isMergedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    merges(b, a)
