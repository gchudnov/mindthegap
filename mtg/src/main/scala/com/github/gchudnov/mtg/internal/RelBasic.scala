package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.rel.*

/**
 * Basic Interval Relations
 *
 * {{{
 *   Relation         Symbol          AAAAA
 *                                    :   :
 *   a.before(b)         b            :   : BBBBBBBBB  | a+ < b-
 *   a.meets(b)          m            :   BBBBBBBBB    | a+ = b-
 *   a.overlaps(b)       o            : BBBBBBBBB      | a- < b- < a+ < b+
 *   a.starts(b)         s            BBBBBBBBB        | a- = b- ; a+ < b+
 *   a.during(b)         d          BBBBBBBBB          | a- > b- ; a+ < b+
 *   a.finishes(b)       f        BBBBBBBBB            | a+ = b+ ; a- > b-
 *   a.after(b)          B  BBBBBBBBB :   :            | a- > b+
 *   a.isMetBy(b)        M    BBBBBBBBB   :            | a- = b+
 *   a.isOverlappedBy(b) O      BBBBBBBBB :            | b- < a- < b+ < a+
 *   a.isStartedBy(b)    S            BBB :            | a- = b- ; b+ < a+
 *   a.contains(b)       D            : B :            | a- < b- ; b+ < a+
 *   a.isFinishedBy(b)   F            : BBB            | a+ = b+ ; a- < b-
 *   a.equalsTo(b)       e            BBBBB            | a- = b- ; a+ = b+
 * }}}
 */
private[mtg] transparent trait RelBasic[T: Domain]:
  a: Interval[T] =>

  /**
   * Before, Precedes (b)
   *
   * @see
   *   [[BeforeAfter.before]]
   */
  final def before(b: Interval[T]): Boolean =
    BeforeAfter.before(a, b)

  /**
   * After, IsPrecededBy (B)
   *
   * @see
   *   [[BeforeAfter.after]]
   */
  final def after(b: Interval[T]): Boolean =
    BeforeAfter.after(a, b)

  /**
   * Meets (m)
   *
   * @see
   *   [[MeetsIsMetBy.meets]]
   */
  final def meets(b: Interval[T]): Boolean =
    MeetsIsMetBy.meets(a, b)

  /**
   * IsMetBy (M)
   *
   * @see
   *   [[MeetsIsMetBy.meets]]
   */
  final def isMetBy(b: Interval[T]): Boolean =
    MeetsIsMetBy.isMetBy(a, b)

  /**
   * Overlaps (o)
   *
   * @see
   *   [[OverlapsIsOverlappedBy.overlaps]]
   */
  final def overlaps(b: Interval[T]): Boolean =
    OverlapsIsOverlappedBy.overlaps(a, b)

  /**
   * IsOverlappedBy (O)
   *
   * @see
   *   [[OverlapsIsOverlappedBy.isOverlappedBy]]
   */
  final def isOverlappedBy(b: Interval[T]): Boolean =
    OverlapsIsOverlappedBy.isOverlappedBy(a, b)

  /**
   * During, ProperlyIncludedIn (d)
   *
   * @see
   *   [[DuringContains.during]]
   */
  final def during(b: Interval[T]): Boolean =
    DuringContains.during(a, b)

  /**
   * Contains, ProperlyIncludes (D)
   *
   * @see
   *   [[DuringContains.contains]]
   */
  final def contains(b: Interval[T]): Boolean =
    DuringContains.contains(a, b)

  /**
   * Starts, Begins (s)
   *
   * @see
   *   [[StartsIsStartedBy.starts]]
   */
  final def starts(b: Interval[T]): Boolean =
    StartsIsStartedBy.starts(a, b)

  /**
   * IsStartedBy (S)
   *
   * @see
   *   [[StartsIsStartedBy.isStartedBy]]
   */
  final def isStartedBy(b: Interval[T]): Boolean =
    StartsIsStartedBy.isStartedBy(a, b)

  /**
   * Finishes, Ends (f)
   *
   * @see
   *   [[FinishesIsFinishedBy.finishes]]
   */
  final def finishes(b: Interval[T]): Boolean =
    FinishesIsFinishedBy.finishes(a, b)

  /**
   * IsFinishedBy (F)
   *
   * @see
   *   [[FinishesIsFinishedBy.isFinishedBy]]
   */
  final def isFinishedBy(b: Interval[T]): Boolean =
    FinishesIsFinishedBy.isFinishedBy(a, b)

  /**
   * Equals (e)
   *
   * @see
   *   [[EqualsTo.equalsTo]]
   */
  final def equalsTo(b: Interval[T]): Boolean =
    EqualsTo.equalsTo(a, b)
