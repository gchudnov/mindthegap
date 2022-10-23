package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval

/**
 * Basic Interval Operations:
 *   - Intersection
 */
private[mtg] transparent trait BasicOps[+T]:
  a: Interval[T] =>

  /**
   * Intersection of two Intervals: A ∩ B
   *
   * A & B
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
   *
   * {{{
   *          | ∅        if (A <b|B> B)       | b, B
   *          | [a-, a+] if (A <s|d|f|e> B)   | s, d, f, e
   * A ∩ B := | [a-, b+] if (A <O|M|S> B)     | O, M, S
   *          | [b-, a+] if (A <m|o|F> B)     | m, o, F
   *          | [b-, b+] if (A <D> B)         | D
   *
   * A ∩ B := | max(a−, b−), min(a+, b+) |
   * }}}
   */
  final def intersection[T1 >: T](b: Interval[T1])(using ordT: Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else Interval.make(ordT.max(a.left, b.left), ordT.min(a.right, b.right))
