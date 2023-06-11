package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
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
   * Groups a series of intervals by executing union for the intersecting intervals.
   */
  final def group[T: Domain](xs: Seq[Interval[T]]): List[Interval[T]] =
    Group.group(xs)

  /**
   * GroupFind
   *
   * Groups a series of intervals by executing union for the intersecting intervals and provide group information about membership.
   */
  final def groupFind[T: Domain](xs: Seq[Interval[T]]): List[(Interval[T], Set[Int])] =
    Group.groupFind(xs).map(it => (it.interval, it.members))

  /**
   * Complement
   *
   * Produces a complement of the given collection of intervals.
   */
  final def complement[T: Domain](xs: Seq[Interval[T]]): List[Interval[T]] =
    Complement.complement(xs)

  /**
   * Split
   *
   * Split intervals into a collection of non-overlapping intervals (splits).
   */
  final def split[T: Domain](xs: Seq[Interval[T]]): List[Interval[T]] =
    Split.split(xs)

  /**
   * SplitFind
   *
   * Split intervals into a collection of non-overlapping intervals (splits).
   */
  final def splitFind[T: Domain](xs: Seq[Interval[T]]): List[(Interval[T], Set[Int])] =
    Split.splitFind(xs).map(it => (it.interval, it.members))
