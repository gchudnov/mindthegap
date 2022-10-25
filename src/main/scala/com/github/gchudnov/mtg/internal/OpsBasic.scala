package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Basic Interval Operations
 */
private[mtg] transparent trait BasicOps[+T]:
  a: Interval[T] =>

  /**
   * Intersection
   *
   *   - A ∩ B
   *   - A & B
   *
   * NOTE:
   *   - a and b intersect if (a- <= b+) AND (b- <= a+)
   *   - empty interval do not intersect with any of the intervals.
   *   - unbounded interval intersects with all non-empty intervals.
   *
   * {{{
   *   A ∩ B := | max(a-, b-), min(a+, b+) |
   * }}}
   *
   * {{{
   *                   [******************]   | [5,10]
   *   [**********************]               | [1,7]
   *                   [******]               | [5,7]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   * }}}
   */
  final def intersection[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.max(a.left, b.left), ordT.min(a.right, b.right))

  /**
   * Span
   *
   *   - A # B
   *
   * {{{
   *   A # B := | min(a-, b-), max(a+, b+) |
   * }}}
   *
   * {{{
   *                   [******************]   | [5,10]
   *   [**********************]               | [1,7]
   *   [**********************************]   | [1,10]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   * }}}
   */
  final def span[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.min(a.left, b.left), ordT.max(a.right, b.right))

  /**
   * Union
   *
   * Union of two intervals `a` and `b` returns `[min(a-,b-), max(a+,b+)]` if `merges(a, b)` and `∅` otherwise.
   *
   * {{{
   *   A union B := | min(a-,b-), max(a+,b+) | if merges(a, b) else ∅
   * }}}
   *
   * {{{
   * }}}
   */
  def union[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.merges(b) then Interval.make(ordT.min(a.left, b.left), ordT.max(a.right, b.right))
    else Interval.empty[T]

  /**
   * Gap (Complement)
   *
   *   - A || B
   *
   * {{{
   *   A || B := | min(a+, b+), max(a-, b-) |
   * }}}
   *
   * {{{
   *   [**************]                       | [5,10]
   *                       [**************]   | [12,17]
   *                  [****]                  | [10,12]
   * --+--------------+----+--------------+-- |
   *   5             10   12             17   |
   * }}}
   */
  final def gap[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.min(a.right, b.right).left, ordT.max(a.left, b.left).right)
