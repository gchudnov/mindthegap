package com.github.gchudnov.mtg

final class IntervalOrdering[T](using Ordering[Boundary[T]]) extends Ordering[Interval[T]]:

  override def compare(a: Interval[T], b: Interval[T]): Int =
    (a.isEmpty, b.isEmpty) match
      case (true, true) =>
        0
      case (false, true) =>
        1
      case (true, false) =>
        -1
      case (false, false) =>
        if a.isLess(b) then -1
        else if a.equalsTo(b) then 0
        else 1
