package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Static Interval Operations
 */
private[mtg] transparent trait StaticOps:

  /**
   * Intersection
   */
  final def intersection[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Boundary[T]]): Interval[T] =
    a.intersection(b)

  /**
   * Span
   */
  final def span[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Boundary[T]]): Interval[T] =
    a.span(b)

  /**
   * Union
   */
  final def union[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Boundary[T]]): Interval[T] =
    a.union(b)

  /**
   * Gap
   */
  final def gap[T: Domain](a: Interval[T], b: Interval[T])(using ordT: Ordering[Boundary[T]]): Interval[T] =
    a.gap(b)
