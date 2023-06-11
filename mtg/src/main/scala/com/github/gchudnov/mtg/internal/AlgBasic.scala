package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.alg.Intersection

/**
 * Basic Interval Operations
 */
private[mtg] transparent trait AlgBasic[T: Domain]:
  a: Interval[T] =>

  /**
   * Intersection
   *
   *   - A ∩ B
   *   - A & B
   *
   * An intersection of two intervals `a` and `b` is defined as the interval `c`, such that `c = a ∩ b := [max(a-, b-), min(a+, b+)]`.
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
   *
   * Laws:
   *   - Commutative: A & B = B & A
   *   - Associative: (A & B) & C = A & (B & C)
   */
  final def intersection(b: Interval[T]): Interval[T] =
    Intersection.intersection(a, b)

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
   * Example #2: (disjoint Intervals):
   *
   *   [***************]                      | [1,5]
   *                          [***********]   | [7,10]
   *   [**********************************]   | [1,10]
   * --+---------------+------+-----------+-- |
   *   1               5      7          10   |
   * }}}
   *
   * Laws:
   *   - Commutative: A # B = B # A
   *   - Associative: (A # B) # C = A # (B # C)
   */
  final def span(b: Interval[T]): Interval[T] =
    val ordM = summon[Domain[T]].ordMark
    Interval.make(ordM.min(a.left, b.left), ordM.max(a.right, b.right))

  /**
   * Union
   *
   *   - A ∪ B
   *
   * A union of two intervals `a` and `b`: `[min(a-,b-), max(a+,b+)]` if `merges(a, b)` and undefined otherwise.
   *
   * NOTE: `merges` means that the intervals are `adjacent` or `intersecting`.
   *
   * {{{
   *   A union B := [min(a-,b-), max(a+,b+)] if merges(a, b) else ∅
   *
   * Example #1 (NOTE: two intervals are adjacent):
   *
   *   [***************]                      | [1,5]
   *                      [***************]   | [6,10]
   *   [**********************************]   | [1,10]
   * --+---------------+--+---------------+-- |
   *   1               5  6              10   |
   *
   * Example #2: (disjoint and non-adjacent Intervals):
   *
   *   [***********]                          | [1,4]
   *                      [***************]   | [6,10]
   *                                          | ∅
   * --+-----------+------+---------------+-- |
   *   1           4      6              10   |
   * }}}
   *
   * Laws:
   *   - Commutative: A ∪ B = B ∪ A
   */
  def union(b: Interval[T]): Interval[T] =
    if a.merges(b) then
      if a.isEmpty && b.isEmpty then a.span(b)
      else if a.isEmpty then b
      else if b.isEmpty then a
      else a.span(b)
    else Interval.empty[T]

  /**
   * Gap (Complement)
   *
   *   - A ∥ B
   *
   * A gap between two intervals `a` and `b`: `a ∥ b := [succ(min(a+, b+)), pred(max(a-, b-))]`.
   *
   * {{{
   *   A ∥ B := [succ(min(a+, b+)), pred(max(a-, b-))]
   *
   *   [***********]                          | [5,10]
   *                          [***********]   | [15,20]
   *                 [******]                 | [11,14]
   * --+-----------+-+------+-+-----------+-- |
   *   5          10 11    14 15         20   |
   * }}}
   *
   * Laws:
   *   - Commutative: A ∥ B = B ∥ A
   */
  final def gap(b: Interval[T]): Interval[T] =
    val ordM = summon[Domain[T]].ordMark
    Interval.make(ordM.min(a.right, b.right), ordM.max(a.left, b.left)).deflate

  /**
   * Minus
   *
   * Subtraction of two intervals, `a` minus `b` returns:
   *
   *   - `[a-, min(pred(b-), a+)]` if (a- < b-) and (a+ <= b+)
   *   - `[max(succ(b+), a-), a+]` if (a- >= b-) and (a+ > b+)
   *
   * NOTE: `a.minus(b)` is defined if and only if:
   *   - (a) `a` and `b` are disjoint;
   *   - (b) `a` contains either `b-` or `b+` but not both;
   *   - (c) either b.starts(a) or b.finishes(a) is true;
   *
   * NOTE: `a.minus(b)` is undefined if:
   *   - either `a.starts(b)` or `a.finishes(b)`;
   *   - either `a` or `b` is properly included in the other;
   *
   * {{{
   * Example #1: ((a- < b-) AND (a+ <= b+))   | [a-, min(pred(b-), a+)]
   *
   *   [**********************]               | [1,10]
   *             [************************]   | [5,15]
   *   [*******]                              | [1,4]
   * --+-------+-+------------+-----------+-- |
   *   1       4 5           10          15   |
   *
   * Example #2: ((a- >= b-) AND (a+ > b+))   | [max(succ(b+), a-), a+]
   *
   *             [************************]   | [5,15]
   *   [**********************]               | [1,10]
   *                            [*********]   | [11,15]
   * --+---------+------------+-+---------+-- |
   *   1         5           10          15   |
   * }}}
   */
  final def minus(b: Interval[T]): Interval[T] =
    val ordM = summon[Domain[T]].ordMark
    if a.isEmpty then Interval.empty[T]
    else if b.isEmpty then a
    else if ordM.lt(a.left, b.left) && ordM.lteq(a.right, b.right) then Interval.make(a.left, ordM.min(b.left.pred, a.right))
    else if ordM.gteq(a.left, b.left) && ordM.gt(a.right, b.right) then Interval.make(ordM.max(b.right.succ, a.left), a.right)
    else if a.contains(b) then throw new UnsupportedOperationException("a.minus(b) is not defined when a.contains(b) is true; use Intervals.minus(a, b) instead")
    else Interval.empty[T]
