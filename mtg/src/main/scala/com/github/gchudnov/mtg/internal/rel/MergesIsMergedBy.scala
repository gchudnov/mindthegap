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
    val ordM = summon[Domain[T]].ordEndpoint
    (a.isEmpty || b.isEmpty) || (
      (ordM.lteq(a.leftEndpoint, b.rightEndpoint) && ordM.lteq(b.leftEndpoint, a.rightEndpoint)) || (ordM.equiv(a.rightEndpoint.succ, b.leftEndpoint) || ordM.equiv(b.rightEndpoint.succ, a.leftEndpoint))
    )

  /**
   * IsMergedBy
   */
  final def isMergedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    merges(b, a)
