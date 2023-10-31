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
   */
  final def before(b: Interval[T]): Boolean =
    BeforeAfter.before(a, b)

  /**
   * After, IsPrecededBy (B)
   */
  final def after(b: Interval[T]): Boolean =
    BeforeAfter.after(a, b)

  /**
   * Meets (m)
   */
  final def meets(b: Interval[T]): Boolean =
    MeetsIsMetBy.meets(a, b)

  /**
   * IsMetBy (M)
   */
  final def isMetBy(b: Interval[T]): Boolean =
    MeetsIsMetBy.isMetBy(a, b)

  /**
   * Overlaps (o)
   */
  final def overlaps(b: Interval[T]): Boolean =
    OverlapsIsOverlappedBy.overlaps(a, b)

  /**
   * IsOverlappedBy (O)
   */
  final def isOverlappedBy(b: Interval[T]): Boolean =
    OverlapsIsOverlappedBy.isOverlappedBy(a, b)

  /**
   * During, ProperlyIncludedIn (d)
   */
  final def during(b: Interval[T]): Boolean =
    DuringContains.during(a, b)

  /**
   * Contains, ProperlyIncludes (D)
   */
  final def contains(b: Interval[T]): Boolean =
    DuringContains.contains(a, b)

  /**
   * Starts, Begins (s)
   */
  final def starts(b: Interval[T]): Boolean =
    StartsIsStartedBy.starts(a, b)

  /**
   * IsStartedBy (S)
   */
  final def isStartedBy(b: Interval[T]): Boolean =
    StartsIsStartedBy.isStartedBy(a, b)

  /**
   * Finishes, Ends (f)
   */
  final def finishes(b: Interval[T]): Boolean =
    FinishesIsFinishedBy.finishes(a, b)

  /**
   * IsFinishedBy (F)
   */
  final def isFinishedBy(b: Interval[T]): Boolean =
    FinishesIsFinishedBy.isFinishedBy(a, b)

  /**
   * Equals (e)
   */
  final def equalsTo(b: Interval[T]): Boolean =
    EqualsTo.equalsTo(a, b)
