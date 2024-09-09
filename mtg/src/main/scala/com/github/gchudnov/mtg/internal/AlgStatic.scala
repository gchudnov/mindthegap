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
   */
  final def intersection[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    Intersection.intersection(a, b)

  /**
   * Span
   */
  final def span[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    Span.span(a, b)

  /**
   * Union
   */
  final def union[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    Union.union(a, b)

  /**
   * Gap
   */
  final def gap[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    Gap.gap(a, b)

  /**
   * Difference
   */
  final def difference[T: Domain](a: Interval[T], b: Interval[T]): List[Interval[T]] =
    Subtraction.difference(a, b)

  /**
   * Difference Symmetric
   */
  final def differenceSymmetric[T: Domain](a: Interval[T], b: Interval[T]): List[Interval[T]] =
    Subtraction.differenceSymmetric(a, b)

  /**
   * Group
   */
  final def group[T: Domain](xs: Iterable[Interval[T]], isGroupAdjacent: Boolean = true): List[Interval[T]] =
    Group.group(xs, isGroupAdjacent)

  /**
   * GroupFind
   */
  final def groupFind[T: Domain](xs: Iterable[Interval[T]], isGroupAdjacent: Boolean = true): List[(Interval[T], Set[Int])] =
    Group.groupFind(xs, isGroupAdjacent).map(it => (it.interval, it.members))

  /**
   * Complement
   */
  final def complement[T: Domain](xs: Iterable[Interval[T]]): List[Interval[T]] =
    Complement.complement(xs)

  /**
   * Split
   */
  final def split[T: Domain](xs: Iterable[Interval[T]]): List[Interval[T]] =
    Split.split(xs)

  /**
   * SplitFind
   */
  final def splitFind[T: Domain](xs: Iterable[Interval[T]]): List[(Interval[T], Set[Int])] =
    Split.splitFind(xs).map(it => (it.interval, it.members))
