package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object IsLessIsGreater:

  /**
   * IsLess
   *
   * Checks whether A is less-than B (Order Relation)
   *
   * A < B
   *
   * {{{
   *   (a- < b-) OR ((a- == b-) AND (a+ < b-))
   * }}}
   */
  final def isLess[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordE = summon[Domain[T]].ordEndpoint
    ordE.compare(a.leftEndpoint, b.leftEndpoint) match
      case -1 =>
        true
      case 0 =>
        ordE.lt(a.rightEndpoint, b.rightEndpoint)
      case _ =>
        false

  /**
   * IsGreater
   *
   * Checks whether A is greater-than B (Order Relation)
   *
   * A > B
   *
   * {{{
   *   (a- > b-) OR ((a- == b-) AND (a+ > b-))
   * }}}
   */
  final def isGreater[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    isLess(b, a)
