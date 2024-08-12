package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object IsSuperset:

  /**
   * IsSuperset
   *
   * Checks whether A is a superset of B
   *
   * A ⊇ B
   *
   * {{{
   *   b- >= a-
   *   b+ <= a+
   *
   *   is-started-by  | S
   *   contains       | D
   *   is-finished-by | F
   *   equals         | e
   * }}}
   */
  final def isSuperset[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordEndpoint
    a.nonEmpty && b.nonEmpty && ordM.gteq(b.left, a.left) && ordM.lteq(b.right, a.right)
