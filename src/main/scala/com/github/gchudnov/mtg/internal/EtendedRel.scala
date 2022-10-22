package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval

/**
 * Extended Interval Relations
 *
 *   - IsSubset
 *   - IsSuperset
 *   - IsDisjoint
 *   - IsLess
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
   * }}}
   *
   * {{{
   *   - A starts B   | s
   *   - A during B   | d
   *   - A finishes B | f
   *   - A equals B   | e
   * }}}
   */
  final def isSubset[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.gteq(a.left, b.left) && bOrd.lteq(a.right, b.right)

  /**
   * IsSuperset
   *
   * Checks whether A is a superset of B
   *
   * {{{
   *   b- >= a-
   *   b+ <= a+
   * }}}
   *
   * {{{
   *   - A is-started-by B  | S
   *   - A contains B       | D
   *   - A is-finished-by B | F
   *   - A equals B         | e
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
   * {{{
   *    before | b
   *    after  | B
   * }}}
   */
  final def isDisjoint[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && (bOrd.lt(a.right, b.left) || bOrd.gt(a.left, b.right))

  /**
   * IsLess
   *
   * Checks whether A less-than B (Order Relation)
   *
   * A < B
   *
   * {{{
   *   a- < b-
   *   a+ < b+
   * }}}
   *
   * {{{
   *   - before         | b
   *   - meets          | m
   *   - overlaps       | o
   * }}}
   */
  final def isLess[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.lt(a.left, b.left) && bOrd.lt(a.right, b.right)
