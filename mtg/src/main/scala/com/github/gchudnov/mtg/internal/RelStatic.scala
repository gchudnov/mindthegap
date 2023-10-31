package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.rel.*

/**
 * Static Interval Relations
 */
private[mtg] transparent trait RelStatic:

  /**
   * Relation: Before, Precedes (b)
   */
  final def before[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    BeforeAfter.before(a, b)

  /**
   * Relation: After, IsPrecededBy (B)
   */
  final def after[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    BeforeAfter.after(a, b)

  /**
   * Meets (m)
   */
  final def meets[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    MeetsIsMetBy.meets(a, b)

  /**
   * IsMetBy (M)
   */
  final def isMetBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    MeetsIsMetBy.isMetBy(a, b)

  /**
   * Overlaps (o)
   */
  final def overlaps[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    OverlapsIsOverlappedBy.overlaps(a, b)

  /**
   * IsOverlappedBy (O)
   */
  final def isOverlappedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    OverlapsIsOverlappedBy.isOverlappedBy(a, b)

  /**
   * During, ProperlyIncludedIn (d)
   */
  final def during[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    DuringContains.during(a, b)

  /**
   * Contains, ProperlyIncludes (D)
   */
  final def contains[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    DuringContains.contains(a, b)

  /**
   * Starts, Begins (s)
   */
  final def starts[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    StartsIsStartedBy.starts(a, b)

  /**
   * IsStartedBy (S)
   */
  final def isStartedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    StartsIsStartedBy.isStartedBy(a, b)

  /**
   * Finishes, Ends (f)
   */
  final def finishes[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    FinishesIsFinishedBy.finishes(a, b)

  /**
   * IsFinishedBy (F)
   */
  final def isFinishedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    FinishesIsFinishedBy.isFinishedBy(a, b)

  /**
   * Equals (e)
   */
  final def equalsTo[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    EqualsTo.equalsTo(a, b)

  /**
   * IsSubset
   */
  final def isSubset[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsSubset.isSubset(a, b)

  /**
   * IsSuperset
   */
  final def isSuperset[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsSuperset.isSuperset(a, b)

  /**
   * IsDisjoint
   */
  final def isDisjoint[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsDisjoint.isDisjoint(a, b)

  /**
   * IsAdjacent
   */
  final def isAdjacent[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsAdjacent.isAdjacent(a, b)

  /**
   * Intersects
   */
  final def intersects[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IntersectsIsIntersectedBy.intersects(a, b)

  /**
   * IsIntersectedBy
   */
  final def isIntersectedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IntersectsIsIntersectedBy.isIntersectedBy(a, b)

  /**
   * Merges
   */
  final def merges[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    MergesIsMergedBy.merges(a, b)

  /**
   * IsMergedBy
   */
  final def isMergedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    MergesIsMergedBy.isMergedBy(a, b)

  /**
   * IsLess
   */
  final def isLess[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsLessIsGreater.isLess(a, b)

  /**
   * IsGreater
   */
  final def isGreater[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsLessIsGreater.isGreater(a, b)
