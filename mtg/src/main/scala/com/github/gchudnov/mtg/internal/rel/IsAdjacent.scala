package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object IsAdjacent:

  /**
   * IsAdjacent
   *
   * Two intervals a and b are adjacent if:
   *
   * {{{
   *   succ(a+) = b- OR succ(b+) = a-
   *
   *   before | b
   *   after  | B
   * }}}
   */
  final def isAdjacent[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordEndpoint
    a.nonEmpty && b.nonEmpty && (ordM.equiv(a.rightEndpoint.succ, b.leftEndpoint) || ordM.equiv(b.rightEndpoint.succ, a.leftEndpoint))
