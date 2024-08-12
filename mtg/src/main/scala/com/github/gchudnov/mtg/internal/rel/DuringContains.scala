package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object DuringContains:

  /**
   * During, ProperlyIncludedIn (d)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   a- < p < a+
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- > b-
   *   a- < b+
   *   a+ > b-
   *   a+ < b+
   *
   *   b- < a- < a+ < b+
   *
   *   Relation                  AAAAA
   *   during(a,b)      d|D    BBBBBBBBB          |  a- > b- ; a+ < b+
   * }}}
   */
  final def during[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordE = summon[Domain[T]].ordEndpoint
    a.nonEmpty && b.isProper && ordE.lt(b.leftEndpoint, a.leftEndpoint) && ordE.lt(a.rightEndpoint, b.rightEndpoint)

  /**
   * Contains, ProperlyIncludes (D)
   */
  final def contains[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    during(b, a)
