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
    val ordM = summon[Domain[T]].ordMark
    (a.isEmpty || b.isEmpty) || (
      (ordM.lteq(a.left, b.right) && ordM.lteq(b.left, a.right)) || (ordM.equiv(a.right.succ, b.left) || ordM.equiv(b.right.succ, a.left))
    )

  /**
   * IsMergedBy
   */
  final def isMergedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    merges(b, a)
