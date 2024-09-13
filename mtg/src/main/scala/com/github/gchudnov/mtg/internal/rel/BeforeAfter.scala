package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object BeforeAfter:

  /**
   * Before, Precedes (b)
   *
   * {{{
   *   PP (Point-Point):
   *   {p}; {q}
   *   p < q
   *
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   p < a-
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-, b+}
   *   a- < b-
   *   a- < b+
   *   a+ < b-
   *   a+ < b+
   *
   *   a- < a+ < b- < b+
   *
   *   Relation                  AAAAA
   *   before(a,b)      b        :   : BBBBBBBBB  |  a+ < b-
   * }}}
   *
   * @param a
   *   interval
   * @param b
   *   interval
   * @return
   *   true if 'a' is before 'b'
   */
  final def before[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordE = summon[Domain[T]].ordEndpoint
    a.nonEmpty && b.nonEmpty && ordE.lt(a.rightEndpoint, b.leftEndpoint)

  /**
   * After, Follows (a)
   *
   * @param a
   *   interval
   * @param b
   *   interval
   * @return
   *   true if 'a' is after 'b'
   */
  final def after[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    before(b, a)
