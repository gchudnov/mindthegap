package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Basic Interval Operations
 *
 * {{{
 *             b-           b+
 * |           |------------|
 * |           .            .
 * | |------|  .            .  |------|
 * |           .            .
 * |    |------|            |------|
 * |           .            .
 * |       |---+--|      |--+---|
 * |           .            .
 * |           |------|     .
 * |           .            .
 * |           .  |------|  .
 * |           .            .
 * |           .     |------|
 * |           .            .
 * |           .            .
 * }}}
 */
private[mtg] transparent trait BasicOps[+T]:
  a: Interval[T] =>

  /**
   * Intersection of two intervals
   *
   *   - A ∩ B
   *   - A & B
   *
   * NOTE:
   *   - a and b intersect if (a- <= b+) AND (b- <= a+)
   *   - empty interval do not intersect with any of the intervals.
   *   - unbounded interval intersects with all non-empty intervals.
   *
   * {{{
   *          | ∅        if (A <b|B> B)       | b, B
   *          | [a-, a+] if (A <s|d|f|e> B)   | s, d, f, e
   * A ∩ B := | [a-, b+] if (A <O|M|S> B)     | O, M, S
   *          | [b-, a+] if (A <m|o|F> B)     | m, o, F
   *          | [b-, b+] if (A <D> B)         | D
   *
   * A ∩ B := | max(a-, b-), min(a+, b+) |
   * }}}
   */
  final def intersection[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.max(a.left, b.left), ordT.min(a.right, b.right))

  /**
   * Span of two intervals
   *
   *   - A # B
   *
   * {{{
   * A # B := | min(a-, b-), max(a+, b+) |
   * }}}
   */
  final def span[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.min(a.left, b.left), ordT.max(a.right, b.right))

  /**
   * Gap of two intervals
   *
   *   - A || B
   *
   * {{{
   * A || B := | min(a+, b+), max(a-, b-) |
   * }}}
   */
  final def gap[T1 >: T: Domain](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.min(a.right, b.right).left, ordT.max(a.left, b.left).right)
