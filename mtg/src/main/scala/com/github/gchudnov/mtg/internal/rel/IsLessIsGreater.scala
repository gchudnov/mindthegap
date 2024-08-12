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
    val ordM = summon[Domain[T]].ordEndpoint
    ordM.compare(a.left, b.left) match
      case -1 =>
        true
      case 0 =>
        ordM.lt(a.right, b.right)
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
