package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object IsDisjoint:

  /**
   * IsDisjoint
   *
   * Checks if A and B are disjoint.
   *
   * A and B are disjoint if A does not intersect B.
   *
   * {{{
   *   a+ < b-
   *   a- > b+
   * }}}
   */
  final def isDisjoint[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordE = summon[Domain[T]].ordEndpoint
    a.nonEmpty && b.nonEmpty && (ordE.lt(a.rightEndpoint, b.leftEndpoint) || ordE.gt(a.leftEndpoint, b.rightEndpoint))
