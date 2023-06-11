package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object FinishesIsFinishedBy:

  /**
   * Finishes, Ends (f)
   *
   * {{{
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   p = a+
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- > b-
   *   a- < b+
   *   a+ > b-
   *   a+ = b+
   *
   *   b- < a- < a+ = b+
   *
   *   Relation                  AAAAA
   *   finishes(a,b)    f|F  BBBBBBBBB            |  a+ = b+ ; a- > b-
   * }}}
   */
  final def finishes[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.isProper && ordM.equiv(a.right, b.right) && ordM.lt(b.left, a.left)

  /**
   * IsFinishedBy (F)
   */
  final def isFinishedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    finishes(b, a)
