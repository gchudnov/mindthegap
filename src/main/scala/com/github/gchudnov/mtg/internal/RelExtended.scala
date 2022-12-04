package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Extended Interval Relations
 */
private[mtg] transparent trait ExtendedRel[T]:
  a: Interval[T] =>

  /**
   * IsSubset
   *
   * Checks whether A is a subset of B
   *
   * A ⊆ B
   *
   * {{{
   *   a- >= b-
   *   a+ <= b+
   *
   *   starts   | s
   *   during   | d
   *   finishes | f
   *   equals   | e
   * }}}
   */
  final def isSubset(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.nonEmpty && ordM.gteq(a.left, b.left) && ordM.lteq(a.right, b.right)

  /**
   * IsSuperset
   *
   * Checks whether A is a superset of B
   *
   * A ⊇ B
   *
   * {{{
   *   b- >= a-
   *   b+ <= a+
   *
   *   is-started-by  | S
   *   contains       | D
   *   is-finished-by | F
   *   equals         | e
   * }}}
   */
  final def isSuperset(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.nonEmpty && ordM.gteq(b.left, a.left) && ordM.lteq(b.right, a.right)

  /**
   * IsDisjoint
   *
   * Checks if there A and B are disjoint.
   *
   * A and B are disjoint if A does not intersect B.
   *
   * {{{
   *   a+ < b-
   *   a- > b+
   * }}}
   *
   * before | b after | B }}}
   */
  final def isDisjoint(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.nonEmpty && (ordM.lt(a.right, b.left) || ordM.gt(a.left, b.right))

  /**
   * IsAdjacent
   *
   * Two intervals a and b are adjacent if:
   *
   * {{{
   *   succ(a+) = b- OR succ(b+) = a-
   *
   *   before | b
   *   after  | B
   * }}}
   */
  final def isAdjacent[T1 >: T: Domain](b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.nonEmpty && (ordM.equiv(a.right.succ, b.left) || ordM.equiv(b.right.succ, a.left))

  /**
   * Intersects
   *
   * Two intervals `a` and `b` are intersecting if:
   *
   * {{{
   *   a- <= b+
   *   b- <= a+
   *
   *   Relation                  AAAAA
   *   meets(a,b)       m|M      :   BBBBBBBBB    |  a+ = b-
   *   overlaps(a,b)    o|O      : BBBBBBBBB      |  a- < b- < a+ ; a+ < b+
   *   starts(a,b)      s|S      BBBBBBBBB        |  a- = b- ; a+ < b+
   *   during(a,b)      d|D    BBBBBBBBB          |  a- > b- ; a+ < b+
   *   finishes(a,b)    f|F  BBBBBBBBB            |  a+ = b+ ; a- > b-
   *   equals(a, b)     e        BBBBB            |  a- = b- ; a+ = b+
   * }}}
   */
  final def intersects(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    a.nonEmpty && b.nonEmpty && ordM.lteq(a.left, b.right) && ordM.lteq(b.left, a.right)

  /**
   * IsIntersectedBy
   */
  final def isIntersectedBy(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    b.intersects(a)

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
   *   intersects(a,b)
   *   isAdjacent(a,b)
   * }}}
   */
  final def merges[T1 >: T: Domain](b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    (a.isEmpty || b.isEmpty) || ((ordM.lteq(a.left, b.right) && ordM.lteq(b.left, a.right)) || (ordM.equiv(a.right.succ, b.left) || ordM.equiv(b.right.succ, a.left)))

  /**
   * IsMergedBy
   */
  final def isMergedBy[T1 >: T: Domain](b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    b.merges(a)

  /**
   * IsLess
   *
   * Checks whether A is less-than B (Order Relation)
   *
   * A < B
   *
   * {{{
   *   a- < b-
   * }}}
   */
  final def isLess(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.lt(a.left, b.left)

  /**
   * IsGreater
   *
   * Checks whether A is greater-than B (Order Relation)
   *
   * A > B
   *
   * {{{
   *   b- < a-
   * }}}
   */
  final def isGreater(b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    b.isLess(a)
