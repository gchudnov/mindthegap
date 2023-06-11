package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object Span:

  /**
   * Span
   *
   *   - A # B
   *
   * A span of two intervals `a` and `b`: `a # b := [min(a-, b-), max(a+, b+)]`.
   *
   * {{{
   *   A # B := [min(a-, b-), max(a+, b+)]
   *
   * Example #1:
   *
   *                   [******************]   | [5,10]
   *   [**********************]               | [1,7]
   *   [**********************************]   | [1,10]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   *
   * Example #2: (disjoint Intervals):
   *
   *   [***************]                      | [1,5]
   *                          [***********]   | [7,10]
   *   [**********************************]   | [1,10]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   * }}}
   *
   * Laws:
   *   - Commutative: A # B = B # A
   *   - Associative: (A # B) # C = A # (B # C)
   */
  final def span[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    val ordM = summon[Domain[T]].ordMark
    Interval.make(ordM.min(a.left, b.left), ordM.max(a.right, b.right))

