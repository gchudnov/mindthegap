package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Basic Interval Operations
 */
private[mtg] transparent trait BasicOps[+T]:
  a: Interval[T] =>

  /**
   * Intersection
   *
   *   - A ∩ B
   *   - A & B
   *
   * An intersection of two intervals `a` and `b`: `[max(a-, b-), min(a+, b+)]`.
   *
   * {{{
   *   A ∩ B := [max(a-, b-), min(a+, b+)]
   *
   *                   [******************]   | [5,10]
   *   [**********************]               | [1,7]
   *                   [******]               | [5,7]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   * }}}
   */
  final def intersection[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.max(a.left, b.left), ordT.min(a.right, b.right))

  /*
    if (a.nonEmpty && b.nonEmpty) && (ordT.lteq(a.left, b.right) && ordT.lteq(b.left, a.right)) then Interval.make(ordT.max(a.left, b.left), ordT.min(a.right, b.right))
    else Interval.empty[T]
   */

  /**
   * Span
   *
   *   - A # B
   *
   * A span of two intervals `a` and `b`: `a # b := [min(a-, b-), max(a+, b+)]`.
   *
   * {{{
   *   A # B := [min(a-, b-), max(a+, b+)]
   *
   * Example #1:
   *
   *                   [******************]   | [5,10]
   *   [**********************]               | [1,7]
   *   [**********************************]   | [1,10]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   *
   * Example #2 (disjoint Intervals):
   *
   *   [***************]                      | [1,5]
   *                          [***********]   | [7,10]
   *   [**********************************]   | [1,10]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   * }}}
   */
  final def span[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty then b
    else if b.isEmpty then a
    else Interval.make(ordT.min(a.left, b.left), ordT.max(a.right, b.right))

  /**
   * Union
   *
   *   - A ∪ B
   *
   * A union of two intervals `a` and `b`: `[min(a-,b-), max(a+,b+)]` if `merges(a, b)` and `∅` otherwise.
   *
   * {{{
   *   A union B := [min(a-,b-), max(a+,b+)] if merges(a, b) else ∅
   *
   * Example #1:
   *
   *   [***************]                      | [1,5]
   *                      [***************]   | [6,10]
   *   [**********************************]   | [1,10]
   * --+---------------+--+---------------+-- |
   *   1               5  6              10   |
   *
   * Example #1 (disjoint and non-adjacent Intervals):
   *
   *   [***********]                          | [1,4]
   *                      [***************]   | [6,10]
   *                                          | ∅
   * --+-----------+------+---------------+-- |
   *   1           4      6              10   |
   * }}}
   */
  def union[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty then b
    else if b.isEmpty then a
    else if a.merges(b) then Interval.make(ordT.min(a.left, b.left), ordT.max(a.right, b.right))
    else Interval.empty[T]

  /**
   * Gap (Complement)
   *
   *   - A ∥ B
   *
   * A gap between two intervals `a` and `b`: `a ∥ b := [min(a-, b-), max(a+, b+)]`.
   *
   * {{{
   *   A ∥ B := [min(a+, b+), max(a-, b-)]
   *
   *   [***********]                          | [5,10]
   *                          [***********]   | [15,20]
   *                 [******]                 | [11,14]
   * --+-----------+-+------+-+-----------+-- |
   *   5          10 11    14 15         20   |
   * }}}
   */
  final def gap[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if (a.nonEmpty && b.nonEmpty) && (ordT.lt(b.right, a.left) || ordT.lt(a.right, b.left)) then
      Interval.make(ordT.min(a.right, b.right).succ.asLeft, ordT.max(a.left, b.left).pred.asRight)
    else Interval.empty[T]

  /**
   * Minus
   *
   * Subtraction of two intervals, `a` minus `b` returns:
   *
   *   - `[a-, min(pred(b-), a+)]` if (a- < b-) and (a+ <= b+)
   *   - `[max(succ(b+), a-), a+]` if (a- >= b-) and (a+ > b+)
   *
   * NOTE: a.minus(b) is defined only if and only if:
   *   - (a) `a` and `b` are disjoint;
   *   - (b) `a` contains either `b-` or `b+` but not both;
   *   - (c) either b.starts(a) or b.finishes(a) is true;
   *
   * NOTE: a.minus(b) is undefined if:
   *   - either a.starts(b) or a.finishes(b);
   *   - either `a` or `b` is properly included in the other;
   *
   * {{{
   *   Example #1 ((a- < b-) AND (a+ <= b+)):
   *
   *   [**********************]               | [1,10]
   *             [************************]   | [5,15]
   *   [*******]                              | [1,4]
   * --+-------+-+------------+-----------+-- |
   *   1       4 5           10          15   |
   *
   *   Example #2 ((a- >= b-) AND (a+ > b+)):
   *
   *             [************************]   | [5,15]
   *   [**********************]               | [1,10]
   *                            [*********]   | [11,15]
   * --+---------+------------+-+---------+-- |
   *   1         5           10          15   |
   * }}}
   */
  final def minus[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty then Interval.empty[T]
    else if b.isEmpty then a
    else if (ordT.lt(a.left, b.left) && ordT.lteq(a.right, b.right)) then Interval.make(a.left, ordT.min(b.left.pred, a.right).asRight)
    else if (ordT.gteq(a.left, b.left) && ordT.gt(a.right, b.right)) then Interval.make(ordT.max(b.right.succ, a.left).asLeft, a.right)
    else if a.contains(b) then throw new UnsupportedOperationException("a.minus(b) is not defined when a.contains(b) is true; use Intervals.minus(a, b) instead")
    else Interval.empty[T]
