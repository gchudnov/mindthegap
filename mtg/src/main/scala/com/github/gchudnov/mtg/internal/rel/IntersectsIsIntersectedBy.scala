package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object IntersectsIsIntersectedBy:

  /**
   * Intersects
   *
   * Two intervals `a` and `b` are intersecting if:
   *
   * {{{
   *   a- <= b+
   *   b- <= a+
   *
   *   Relation                  AAAAA
   *   meets(a,b)       m|M      :   BBBBBBBBB    |  a+ = b-
   *   overlaps(a,b)    o|O      : BBBBBBBBB      |  a- < b- < a+ ; a+ < b+
   *   starts(a,b)      s|S      BBBBBBBBB        |  a- = b- ; a+ < b+
   *   during(a,b)      d|D    BBBBBBBBB          |  a- > b- ; a+ < b+
   *   finishes(a,b)    f|F  BBBBBBBBB            |  a+ = b+ ; a- > b-
   *   equals(a, b)     e        BBBBB            |  a- = b- ; a+ = b+
   * }}}
   */
  final def intersects[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordEndpoint
    a.nonEmpty && b.nonEmpty && ordM.lteq(a.leftEndpoint, b.rightEndpoint) && ordM.lteq(b.leftEndpoint, a.rightEndpoint)

  /**
   * IsIntersectedBy
   */
  final def isIntersectedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    intersects(b, a)
