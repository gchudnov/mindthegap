package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.BoundaryOrdering
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Degenerate
import com.github.gchudnov.mtg.Proper

/**
 * Interval Algorithms
 */
trait IntervalAlg[+T]:
  a: Interval[T] =>

  /**
   * Intersection of two intervals
   * {{{
   * |           a-           a+
   * |           |------------|
   * |           .            .
   * | |------|  .            . |------|
   * |           .            .
   * |       |---+--|      |--+---|
   * |           .            .
   * |           .  |------|  .
   * |           .            .
   * }}}
   *
   * {{{
   *          | ∅        if (A <b|B> B)       | b, B
   *          | [a-, a+] if (A <s|d|f|e> B)   | s, d, f, e
   * A ∩ B := | [a-, b+] if r3 ∧ ¬r4                | O, M, S
   *          | [b-, a+] if ¬r3 ∧ r4                | m, o, F
   *          | [b-, b+] if ¬r3 ∧ ¬r4               | D
   * }}}
   */
  final def intersection[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Interval[T1] =
    ???
    // if a.isEmpty || b.isEmpty then Interval.empty[T]
    // else if a.before(b) || a.after(b) then Interval.empty[T]
    // else if a.starts(b) || a.during(b) || a.finishes(b) || a.equalsTo(b) then Interval.make[T1](a.left, a.right)
    // else
    //   ???

