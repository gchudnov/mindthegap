package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.alg.*

/**
 * Basic Interval Operations
 */
private[mtg] transparent trait AlgBasic[T: Domain]:
  a: Interval[T] =>

  /**
   * Intersection of two intervals
   *
   * @see
   *   [[Intersection.intersection]]
   */
  final def intersection(b: Interval[T]): Interval[T] =
    Intersection.intersection(a, b)

  /**
   * Span of two intervals
   *
   * @see
   *   [[Span.span]]
   */
  final def span(b: Interval[T]): Interval[T] =
    Span.span(a, b)

  /**
   * Union of two intervals
   *
   * @see
   *   [[Union.union]]
   */
  def union(b: Interval[T]): Interval[T] =
    Union.union(a, b)

  /**
   * Gap (Complement) of two intervals
   *
   * @see
   *   [[Gap.gap]]
   */
  final def gap(b: Interval[T]): Interval[T] =
    Gap.gap(a, b)

  /**
   * Minus of two intervals
   *
   * @see
   *   [[Minus.minus]]
   */
  final def minus(b: Interval[T]): Interval[T] =
    Minus.minusNotContaining(a, b)
