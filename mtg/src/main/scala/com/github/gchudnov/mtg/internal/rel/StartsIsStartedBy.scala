package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain


private[mtg] object StartsIsStartedBy:

  /**
   * Starts, Begins (s)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   p = a-
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- = b-
   *   a- < b+
   *   a+ > b-
   *   a+ < b+
   *
   *   a- = b- < a+ < b+
   *
   *   Relation                  AAAAA
   *   starts(a,b)      s|S      BBBBBBBBB        |  a- = b- ; a+ < b+
   * }}}
   */
  final def starts[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.isProper && ordM.equiv(a.left, b.left) && ordM.lt(a.right, b.right)

  /**
   * IsStartedBy (S)
   */
  final def isStartedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    starts(b, a)
