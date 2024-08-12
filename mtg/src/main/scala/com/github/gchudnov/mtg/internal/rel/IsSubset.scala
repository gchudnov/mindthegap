package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object IsSubset:

  /**
   * IsSubset
   *
   * Checks whether A is a subset of B
   *
   * A ⊆ B
   *
   * {{{
   *   a- >= b-
   *   a+ <= b+
   *
   *   starts   | s
   *   during   | d
   *   finishes | f
   *   equals   | e
   * }}}
   */
  final def isSubset[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordE = summon[Domain[T]].ordEndpoint
    a.nonEmpty && b.nonEmpty && ordE.gteq(a.leftEndpoint, b.leftEndpoint) && ordE.lteq(a.rightEndpoint, b.rightEndpoint)
