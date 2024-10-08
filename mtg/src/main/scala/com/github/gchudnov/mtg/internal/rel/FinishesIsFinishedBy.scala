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
    val ordE = summon[Domain[T]].ordEndpoint
    a.nonEmpty && b.isProper && ordE.equiv(a.rightEndpoint, b.rightEndpoint) && ordE.lt(b.leftEndpoint, a.leftEndpoint)

  /**
   * IsFinishedBy (F)
   */
  final def isFinishedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    finishes(b, a)
