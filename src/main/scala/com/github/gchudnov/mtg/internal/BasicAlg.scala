package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval

/**
 * Basic Interval Algorithms
 */
private[mtg] transparent trait BasicAlg[+T]:
  a: Interval[T] =>

  /**
   * Intersection of two Intervals: A ∩ B
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
   * }}}
   */
  final def intersection[T1 >: T](b: Interval[T1])(using Ordering[Boundary[T1]]): Interval[T1] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else if a.before(b) || a.after(b) then Interval.empty[T]
    else if a.starts(b) || a.during(b) || a.finishes(b) || a.equalsTo(b) then Interval.make[T1](a.left, a.right)
    else if a.isOverlapedBy(b) || a.isMetBy(b) || a.isStartedBy(b) then Interval.make(a.left, b.right)
    else if a.meets(b) || a.overlaps(b) || a.isFinishedBy(b) then Interval.make(b.left, a.right)
    else if a.contains(b) then Interval.make(b.left, b.right)
    else sys.error(s"Invalid state: unexpected intersection between ${a} and ${b}")
