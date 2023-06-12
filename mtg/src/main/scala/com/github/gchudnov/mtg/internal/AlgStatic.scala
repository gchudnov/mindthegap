package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.alg.*

/**
 * Static Interval Operations
 */
private[mtg] transparent trait AlgStatic:

  /**
   * Intersection
   *
   * @see
   *   [[Intersection.intersection]]
   */
  final def intersection[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    Intersection.intersection(a, b)

  /**
   * Span
   *
   * @see
   *   [[Span.span]]
   */
  final def span[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    Span.span(a, b)

  /**
   * Union
   *
   * @see
   *   [[Union.union]]
   */
  final def union[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    Union.union(a, b)

  /**
   * Gap
   *
   * @see
   *   [[Gap.gap]]
   */
  final def gap[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    Gap.gap(a, b)

  /**
   * Minus
   *
   * @see
   *   [[Minus.minus]]
   */
  final def minus[T: Domain](a: Interval[T], b: Interval[T]): List[Interval[T]] =
    Minus.minus(a, b)

  /**
   * Group
   *
   * @see
   *   [[Group.group]]
   */
  final def group[T: Domain](xs: Seq[Interval[T]], isGroupAdjacent: Boolean = true): List[Interval[T]] =
    Group.group(xs, isGroupAdjacent)

  /**
   * GroupFind
   *
   * @see
   *   [[Group.groupFind]]
   */
  final def groupFind[T: Domain](xs: Seq[Interval[T]], isGroupAdjacent: Boolean = true): List[(Interval[T], Set[Int])] =
    Group.groupFind(xs, isGroupAdjacent).map(it => (it.interval, it.members))

  /**
   * Complement
   *
   * @see
   *   [[Complement.complement]]
   */
  final def complement[T: Domain](xs: Seq[Interval[T]]): List[Interval[T]] =
    Complement.complement(xs)

  /**
   * Split
   *
   * @see
   *   [[Split.split]]
   */
  final def split[T: Domain](xs: Seq[Interval[T]]): List[Interval[T]] =
    Split.split(xs)

  /**
   * SplitFind
   *
   * @see
   *   [[Split.splitFind]]
   */
  final def splitFind[T: Domain](xs: Seq[Interval[T]]): List[(Interval[T], Set[Int])] =
    Split.splitFind(xs).map(it => (it.interval, it.members))
