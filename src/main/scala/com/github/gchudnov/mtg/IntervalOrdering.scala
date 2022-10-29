package com.github.gchudnov.mtg

final class IntervalOrdering[T](using Ordering[Mark[T]]) extends Ordering[Interval[T]]:

  override def compare(a: Interval[T], b: Interval[T]): Int =
    if a.isLess(b) then -1
    else if a.equalsTo(b) then 0
    else 1
