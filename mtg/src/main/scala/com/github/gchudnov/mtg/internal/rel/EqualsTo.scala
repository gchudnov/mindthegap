package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object EqualsTo:

  /**
   * Equals (e)
   *
   * A = B
   *
   * {{{
   *   PP (Point-Point):
   *   {p}; {q}
   *   p = q
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- = b-
   *   a- < b+
   *   a+ > b-
   *   a+ = b+
   *
   *   a- = b- < a+ = b+
   *
   *   Relation                  AAAAA
   *   equalsTo(a, b)   e        BBBBB            |  a- = b- ; a+ = b+
   * }}}
   */
  final def equalsTo[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    ordM.equiv(a.left, b.left) && ordM.equiv(a.right, b.right)
