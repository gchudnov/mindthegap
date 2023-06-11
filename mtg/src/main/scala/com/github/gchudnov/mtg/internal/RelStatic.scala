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
   *
   * @see
   *   [[BeforeAfter.before]]
   */
  final def before[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    BeforeAfter.before(a, b)

  /**
   * Relation: After, IsPrecededBy (B)
   *
   * @see
   *   [[BeforeAfter.after]]
   */
  final def after[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    BeforeAfter.after(a, b)

  /**
   * Meets (m)
   *
   * @see
   *   [[MeetsIsMetBy.meets]]
   */
  final def meets[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    MeetsIsMetBy.meets(a, b)

  /**
   * IsMetBy (M)
   *
   * @see
   *   [[MeetsIsMetBy.meets]]
   */
  final def isMetBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    MeetsIsMetBy.isMetBy(a, b)

  /**
   * Overlaps (o)
   *
   * @see
   *   [[OverlapsIsOverlappedBy.overlaps]]
   */
  final def overlaps[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    OverlapsIsOverlappedBy.overlaps(a, b)

  /**
   * IsOverlappedBy (O)
   *
   * @see
   *   [[OverlapsIsOverlappedBy.isOverlappedBy]]
   */
  final def isOverlappedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    OverlapsIsOverlappedBy.isOverlappedBy(a, b)

  /**
   * During, ProperlyIncludedIn (d)
   *
   * @see
   *   [[DuringContains.during]]
   */
  final def during[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    DuringContains.during(a, b)

  /**
   * Contains, ProperlyIncludes (D)
   * 
   * @see
   *   [[DuringContains.contains]]
   */
  final def contains[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    DuringContains.contains(a, b)

  /**
   * Starts, Begins (s)
   *
   * @see
   *   [[StartsIsStartedBy.starts]]
   */
  final def starts[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    StartsIsStartedBy.starts(a, b)

  /**
   * IsStartedBy (S)
   * 
   * @see
   *   [[StartsIsStartedBy.isStartedBy]]
   */
  final def isStartedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    StartsIsStartedBy.isStartedBy(a, b)

  /**
   * Finishes, Ends (f)
   *
   * @see
   *   [[FinishesIsFinishedBy.finishes]]
   */
  final def finishes[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    FinishesIsFinishedBy.finishes(a, b)

  /**
   * IsFinishedBy (F)
   * 
   * @see
   *   [[FinishesIsFinishedBy.isFinishedBy]]
   */
  final def isFinishedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
   FinishesIsFinishedBy.isFinishedBy(a, b)

  /**
   * Equals (e)
   *
   * @see
   *   [[EqualsTo.equalsTo]]
   */
  final def equalsTo[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    EqualsTo.equalsTo(a, b)

  /**
   * IsSubset
   *
   * @see
   *   [[IsSubset.isSubset]]
   */
  final def isSubset[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsSubset.isSubset(a, b)

  /**
   * IsSuperset
   *
   * @see
   *   [[IsSuperset.isSuperset]]
   */
  final def isSuperset[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsSuperset.isSuperset(a, b)

  /**
   * IsDisjoint
   *
   * @see
   *   [[IsDisjoint.isDisjoint]]
   */
  final def isDisjoint[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsDisjoint.isDisjoint(a, b)

  /**
   * IsAdjacent
   *
   * @see
   *   [[IsAdjacent.isAdjacent]]
   */
  final def isAdjacent[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IsAdjacent.isAdjacent(a, b)

  /**
   * Intersects
   *
   * @see
   *   [[IntersectsIsIntersectedBy.intersects]]
   */
  final def intersects[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IntersectsIsIntersectedBy.intersects(a, b)

  /**
   * IsIntersectedBy
   * 
   * @see
   *   [[IntersectsIsIntersectedBy.isIntersectedBy]]
   */
  final def isIntersectedBy[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    IntersectsIsIntersectedBy.isIntersectedBy(a, b)
