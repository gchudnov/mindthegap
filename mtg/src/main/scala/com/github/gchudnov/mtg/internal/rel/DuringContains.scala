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
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.isProper && ordM.lt(b.left, a.left) && ordM.lt(a.right, b.right)

  /**
   * Contains, ProperlyIncludes (D)
   */
  final def contains[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    during(b, a)
