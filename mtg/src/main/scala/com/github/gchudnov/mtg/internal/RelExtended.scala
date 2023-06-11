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
   * Two intervals `a` and `b` can be merged, if they are adjacent or intersect.
   *
   * {{{
   *   a- <= b+
   *   b- <= a+
   *   OR
   *   succ(a+) = b- OR succ(b+) = a-
   *
   *   intersects(a,b) OR isAdjacent(a,b)
   * }}}
   */
  final def merges[T1 >: T: Domain](b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    (a.isEmpty || b.isEmpty) || ((ordM.lteq(a.left, b.right) && ordM.lteq(b.left, a.right)) || (ordM.equiv(a.right.succ, b.left) || ordM.equiv(b.right.succ, a.left)))

  /**
   * IsMergedBy
   */
  final def isMergedBy[T1 >: T: Domain](b: Interval[T]): Boolean =
    b.merges(a)

  /**
   * IsLess
   *
   * Checks whether A is less-than B (Order Relation)
   *
   * A < B
   *
   * {{{
   *   (a- < b-) OR ((a- == b-) AND (a+ < b-))
   * }}}
   */
  final def isLess(b: Interval[T]): Boolean =
    val ordM = summon[Domain[T]].ordMark
    ordM.compare(a.left, b.left) match
      case -1 =>
        true
      case 0 =>
        ordM.lt(a.right, b.right)
      case _ =>
        false

  /**
   * IsGreater
   *
   * Checks whether A is greater-than B (Order Relation)
   *
   * A > B
   *
   * {{{
   *   (a- > b-) OR ((a- == b-) AND (a+ > b-))
   * }}}
   */
  final def isGreater(b: Interval[T]): Boolean =
    b.isLess(a)
