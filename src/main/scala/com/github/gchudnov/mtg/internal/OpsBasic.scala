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
   * An intersection of two intervals `a` and `b`: `| max(a-, b-), min(a+, b+) |`.
   *
   * {{{
   *   A ∩ B := [ max(a-, b-), min(a+, b+) ]
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

  /**
   * Span
   *
   *   - A # B
   *
   * A span of two intervals `a` and `b`: `a # b := | min(a-, b-), max(a+, b+) |`.
   *
   * {{{
   *   A # B := [ min(a-, b-), max(a+, b+) ]
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
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.min(a.left, b.left), ordT.max(a.right, b.right))

  /**
   * Union
   *
   * A union of two intervals `a` and `b`: `| min(a-,b-), max(a+,b+) |` if `merges(a, b)` and `∅` otherwise.
   *
   * {{{
   *   A union B := [ min(a-,b-), max(a+,b+) ] if merges(a, b) else ∅
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
    if a.merges(b) then Interval.make(ordT.min(a.left, b.left), ordT.max(a.right, b.right))
    else Interval.empty[T]

  /**
   * Gap (Complement)
   *
   *   - A || B
   *
   * A gap between two intervals `a` and `b`: `a || b := | min(a-, b-), max(a+, b+) |`.
   *
   * {{{
   *   A || B := | min(a+, b+), max(a-, b-) |
   * }}}
   *
   * {{{
   *   [**************]                       | [5,10]
   *                       [**************]   | [12,17]
   *                  [****]                  | [10,12]
   * --+--------------+----+--------------+-- |
   *   5             10   12             17   |
   * }}}
   */
  final def gap[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.min(a.right, b.right).left, ordT.max(a.left, b.left).right)

  /**
   * Minus
   * 
   * 
   * 
   * {{{
   * 
   * 
   * }}}
   */
  final def minus[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    ???
