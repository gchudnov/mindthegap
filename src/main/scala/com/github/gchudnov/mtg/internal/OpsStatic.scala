package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Static Interval Operations
 */
private[mtg] transparent trait StaticOps:

  /**
   * Intersection
   */
  final def intersection[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Mark[T]]): Interval[T] =
    a.intersection(b)

  /**
   * Span
   */
  final def span[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Mark[T]]): Interval[T] =
    a.span(b)

  /**
   * Union
   */
  final def union[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Mark[T]]): Interval[T] =
    a.union(b)

  /**
   * Gap
   */
  final def gap[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Mark[T]]): Interval[T] =
    a.gap(b)

  /**
   * Minus
   *
   * Unlike `a.minus(b)` that returns an interval and not defined when `a.contains(b)`, this method returns a non-empty collection of intervals and can be used when `a.contains(b)`
   * is true.
   *
   * {{{
   *   a - b = [c1, c2]                       | [a-, pred(b-)], [succ(b+), a+]
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
    else List(a.minus(b))

  /**
   * Group
   *
   * Groups a series of intervals by executing union for the intersecting intervals.
   */
  final def group[T: Domain](xs: Iterable[Interval[T]]): List[Interval[T]] =
    ???
