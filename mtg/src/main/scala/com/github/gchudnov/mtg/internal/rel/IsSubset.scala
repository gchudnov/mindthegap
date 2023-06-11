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
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.nonEmpty && ordM.gteq(a.left, b.left) && ordM.lteq(a.right, b.right)
