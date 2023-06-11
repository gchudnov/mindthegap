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
   * {{{
   *   PI (Point-Interval):
   *   {p}; {a-, a+}
   *   p = a+
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- > b-
   *   a- < b+
   *   a+ > b-
   *   a+ = b+
   *
   *   b- < a- < a+ = b+
   *
   *   Relation                  AAAAA
   *   finishes(a,b)    f|F  BBBBBBBBB            |  a+ = b+ ; a- > b-
   * }}}
   */
  final def finishes(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    a.nonEmpty && b.isProper && ordM.equiv(a.right, b.right) && ordM.lt(b.left, a.left)

  /**
   * IsFinishedBy (F)
   */
  final def isFinishedBy(b: Interval[T]): Boolean =
    b.finishes(a)

  /**
   * Equals (e)
   *
   * A = B
   *
   * {{{
   *   PP (Point-Point):
   *   {p}; {q}
   *   p = q
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- = b-
   *   a- < b+
   *   a+ > b-
   *   a+ = b+
   *
   *   a- = b- < a+ = b+
   *
   *   Relation                  AAAAA
   *   equalsTo(a, b)   e        BBBBB            |  a- = b- ; a+ = b+
   * }}}
   */
  final def equalsTo(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    ordM.equiv(a.left, b.left) && ordM.equiv(a.right, b.right)
