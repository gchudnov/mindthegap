package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object MeetsIsMetBy:

  /**
   * Meets (m)
   *
   * {{{
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- < b-
   *   a- < b+
   *   a+ = b-
   *   a+ < b+
   *
   *   a- < a+ = b- < b+
   *
   *   Relation                  AAAAA
   *   meets(a,b)       m        :   BBBBBBBBB    |  a+ = b-
   * }}}
   */
  final def meets[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordE = summon[Domain[T]].ordEndpoint
    a.isProper && b.isProper && ordE.equiv(a.rightEndpoint, b.leftEndpoint)

  /**
   * IsMetBy (M)
   */
  final def isMetBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    meets(b, a)
