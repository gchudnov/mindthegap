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
   */
  final def isSubset(b: Interval[T]): Boolean =
    IsSubset.isSubset(a, b)

  /**
   * IsSuperset
   */
  final def isSuperset(b: Interval[T]): Boolean =
    IsSuperset.isSuperset(a, b)

  /**
   * IsDisjoint
   */
  final def isDisjoint(b: Interval[T]): Boolean =
    IsDisjoint.isDisjoint(a, b)

  /**
   * IsAdjacent
   */
  final def isAdjacent(b: Interval[T]): Boolean =
    IsAdjacent.isAdjacent(a, b)

  /**
   * Intersects
   */
  final def intersects(b: Interval[T]): Boolean =
    IntersectsIsIntersectedBy.intersects(a, b)

  /**
   * IsIntersectedBy
   */
  final def isIntersectedBy(b: Interval[T]): Boolean =
    IntersectsIsIntersectedBy.isIntersectedBy(a, b)

  /**
   * Merges
   */
  final def merges(b: Interval[T]): Boolean =
    MergesIsMergedBy.merges(a, b)

  /**
   * IsMergedBy
   */
  final def isMergedBy(b: Interval[T]): Boolean =
    MergesIsMergedBy.isMergedBy(a, b)

  /**
   * IsLess
   */
  final def isLess(b: Interval[T]): Boolean =
    IsLessIsGreater.isLess(a, b)

  /**
   * IsGreater
   */
  final def isGreater(b: Interval[T]): Boolean =
    IsLessIsGreater.isGreater(a, b)
