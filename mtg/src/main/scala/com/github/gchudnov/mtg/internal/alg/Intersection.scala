package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object Intersection:

  /**
   * Intersection
   *
   *   - A ∩ B
   *   - A & B
   *
   * An intersection of two intervals `a` and `b` is defined as the interval `c`, such that `c = a ∩ b := [max(a-, b-), min(a+, b+)]`.
   *
   * {{{
   *   A ∩ B := [max(a-, b-), min(a+, b+)]
   *
   *                   [******************]   | [5,10]
   *   [**********************]               | [1,7]
   *                   [******]               | [5,7]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   * }}}
   *
   * Laws:
   *   - Commutative: A & B = B & A
   *   - Associative: (A & B) & C = A & (B & C)
   */
  final def intersection[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    val ordM = summon[Domain[T]].ordEndpoint
    Interval.make(ordM.max(a.left, b.left), ordM.min(a.right, b.right))
