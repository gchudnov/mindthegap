package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Static operations to work with collection of intervals.
 */
private[mtg] transparent trait StaticsOps:

  /**
   * Minus
   *
   * Unlike `Interval.minus`, this method returns a non-empty collection of intervals and can handle the case when a.contains(b).
   *
   * {{{
   *   a - b = [c1, c2]
   *
   *   [**********************************]   | [1,15]  : a
   *             [************]               | [5,10]  : b
   *   [*******]                              | [1,4]   : c1
   *                            [*********]   | [11,15] : c2
   * --+-------+-+------------+-+---------+-- |
   *   1       4 5           10          15   |
   * }}}
   */
  final def minus[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Mark[T]]): List[Interval[T]] =
    if a.contains(b) then List(Interval.make(a.left, b.left.pred), Interval.make(b.right.succ, a.right))
    else List(Interval.minus(a, b))
