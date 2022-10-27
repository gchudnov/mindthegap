package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Extended Interval Relations
 *
 *   - IsSubset
 *   - IsSuperset
 *   - IsDisjoint
 *   - IsAdjacent
 *   - Merges
 *   - IsLess
 *   - IsGreater
 */
private[mtg] transparent trait ExtendedRel[+T]:
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
  final def isSubset[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.gteq(a.left, b.left) && bOrd.lteq(a.right, b.right)

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
  final def isSuperset[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.gteq(b.left, a.left) && bOrd.lteq(b.right, a.right)

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
  final def isDisjoint[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && (bOrd.lt(a.right, b.left) || bOrd.gt(a.left, b.right))

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
  final def isAdjacent[T1 >: T: Domain](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && (bOrd.equiv(a.right.succ, b.left) || bOrd.equiv(b.right.succ, a.left))

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
  final def intersects[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.lteq(a.left, b.right) && bOrd.lteq(b.left, a.right)

  /**
   * IsIntersectedBy
   */
  final def isIntersectedBy[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
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
  final def merges[T1 >: T: Domain](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    (a.isEmpty || b.isEmpty) || ((bOrd.lteq(a.left, b.right) && bOrd.lteq(b.left, a.right)) || (bOrd.equiv(a.right.succ, b.left) || bOrd.equiv(b.right.succ, a.left)))

  /**
   * IsMergedBy
   */
  final def isMergedBy[T1 >: T: Domain](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
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
   *   a+ < b+
   *
   *   before         | b
   *   meets          | m
   *   overlaps       | o
   * }}}
   */
  final def isLess[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.lt(a.left, b.left) && bOrd.lt(a.right, b.right)

  /**
   * IsGreater
   *
   * Checks whether A is greater-than B (Order Relation)
   *
   * A > B
   *
   * {{{
   *   a- > b-
   *   a+ > b+
   *
   *   after          | B
   *   isMetBy        | M
   *   isOverlappedBy | O
   * }}}
   */
  final def isGreater[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.gt(a.left, b.left) && bOrd.gt(a.right, b.right)
