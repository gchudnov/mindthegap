package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object Union:

  /**
   * Union
   *
   *   - A ∪ B
   *
   * A union of two intervals `a` and `b`: `[min(a-,b-), max(a+,b+)]` if `merges(a, b)` and undefined otherwise.
   *
   * NOTE: `merges` means that the intervals are `adjacent` or `intersecting`.
   *
   * {{{
   *   A union B := [min(a-,b-), max(a+,b+)] if merges(a, b) else ∅
   *
   * Example #1 (NOTE: two intervals are adjacent):
   *
   *   [***************]                      | [1,5]
   *                      [***************]   | [6,10]
   *   [**********************************]   | [1,10]
   * --+---------------+--+---------------+-- |
   *   1               5  6              10   |
   *
   * Example #2: (disjoint and non-adjacent Intervals):
   *
   *   [***********]                          | [1,4]
   *                      [***************]   | [6,10]
   *                                          | ∅
   * --+-----------+------+---------------+-- |
   *   1           4      6              10   |
   * }}}
   *
   * Laws:
   *   - Commutative: A ∪ B = B ∪ A
   */
  final def union[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    if a.merges(b) then
      if a.isEmpty && b.isEmpty then a.span(b)
      else if a.isEmpty then b
      else if b.isEmpty then a
      else a.span(b)
    else Interval.empty[T]
