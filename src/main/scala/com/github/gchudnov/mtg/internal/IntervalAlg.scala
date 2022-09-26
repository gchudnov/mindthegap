package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.BoundaryOrdering
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Degenerate
import com.github.gchudnov.mtg.Proper

trait IntervalAlg[+T: Ordering: Domain]:
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
   */
  final def intersection[T1 >: T](b: Interval[T1])(using bOrd: Ordering[Boundary[T1]]): Interval[T] =
    if a.isEmpty || b.isEmpty then Interval.empty[T]
    else
      ???
