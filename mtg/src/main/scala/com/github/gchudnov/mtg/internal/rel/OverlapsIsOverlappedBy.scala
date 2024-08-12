package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object OverlapsIsOverlappedBy:

  /**
   * Overlaps (o)
   *
   * {{{
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- < b-
   *   a- < b+
   *   a+ > b-
   *   a+ < b+
   *
   *   a- < b- < a+ < b+
   *
   *   Relation                  AAAAA
   *   overlaps(a,b)    o        : BBBBBBBBB      |  a- < b- < a+ ; a+ < b+
   * }}}
   */
  final def overlaps[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordEndpoint
    a.isProper && b.isProper && ordM.lt(a.leftEndpoint, b.leftEndpoint) && ordM.lt(b.leftEndpoint, a.rightEndpoint) && ordM.lt(a.rightEndpoint, b.rightEndpoint)

  /**
   * IsOverlappedBy (O)
   */
  final def isOverlappedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    overlaps(b, a)
