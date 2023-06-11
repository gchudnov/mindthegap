package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object Gap:

  /**
   * Gap (Complement)
   *
   *   - A ∥ B
   *
   * A gap between two intervals `a` and `b`: `a ∥ b := [succ(min(a+, b+)), pred(max(a-, b-))]`.
   *
   * {{{
   *   A ∥ B := [succ(min(a+, b+)), pred(max(a-, b-))]
   *
   *   [***********]                          | [5,10]
   *                          [***********]   | [15,20]
   *                 [******]                 | [11,14]
   * --+-----------+-+------+-+-----------+-- |
   *   5          10 11    14 15         20   |
   * }}}
   *
   * Laws:
   *   - Commutative: A ∥ B = B ∥ A
   */
  final def gap[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    val ordM = summon[Domain[T]].ordMark
    Interval.make(ordM.min(a.right, b.right), ordM.max(a.left, b.left)).deflate
