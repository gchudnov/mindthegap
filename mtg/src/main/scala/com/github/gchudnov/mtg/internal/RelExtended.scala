package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.rel.*

/**
 * Extended Interval Relations
 */
private[mtg] transparent trait RelExtended[T: Domain]:
  a: Interval[T] =>

  /**
   * IsSubset
   *
   * @see
   *   [[IsSubset.isSubset]]
   */
  final def isSubset(b: Interval[T]): Boolean =
    IsSubset.isSubset(a, b)

  /**
   * IsSuperset
   *
   * @see
   *   [[IsSuperset.isSuperset]]
   */
  final def isSuperset(b: Interval[T]): Boolean =
    IsSuperset.isSuperset(a, b)

  /**
   * IsDisjoint
   *
   * @see
   *   [[IsDisjoint.isDisjoint]]
   */
  final def isDisjoint(b: Interval[T]): Boolean =
    IsDisjoint.isDisjoint(a, b)

  /**
   * IsAdjacent
   *
   * @see
   *   [[IsAdjacent.isAdjacent]]
   */
  final def isAdjacent(b: Interval[T]): Boolean =
    IsAdjacent.isAdjacent(a, b)

  /**
   * Intersects
   *
   * @see
   *   [[IntersectsIsIntersectedBy.intersects]]
   */
  final def intersects(b: Interval[T]): Boolean =
    IntersectsIsIntersectedBy.intersects(a, b)

  /**
   * IsIntersectedBy
   *
   * @see
   *   [[IntersectsIsIntersectedBy.isIntersectedBy]]
   */
  final def isIntersectedBy(b: Interval[T]): Boolean =
    IntersectsIsIntersectedBy.isIntersectedBy(a, b)

  /**
   * Merges
   *
   * @see
   *   [[MergesIsMergedBy.merges]]
   */
  final def merges(b: Interval[T]): Boolean =
    MergesIsMergedBy.merges(a, b)

  /**
   * IsMergedBy
   *
   * @see
   *   [[MergesIsMergedBy.isMergedBy]]
   */
  final def isMergedBy(b: Interval[T]): Boolean =
    MergesIsMergedBy.isMergedBy(a, b)

  /**
   * IsLess
   *
   * @see
   *   [[IsLessIsGreater.isLess]]
   */
  final def isLess(b: Interval[T]): Boolean =
    IsLessIsGreater.isLess(a, b)

  /**
   * IsGreater
   *
   * @see
   *   [[IsLessIsGreater.isGreater]]
   */
  final def isGreater(b: Interval[T]): Boolean =
    IsLessIsGreater.isGreater(a, b)
