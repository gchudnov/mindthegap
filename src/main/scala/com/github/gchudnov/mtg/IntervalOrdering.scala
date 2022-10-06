package com.github.gchudnov.mtg

private final class IntervalOrdering[T](using bOrd: Ordering[Boundary[T]]) extends Ordering[Interval[T]]:
  import IntervalOrdering.*

  override def compare(a: Interval[T], b: Interval[T]): Int =
    if isLess(a, b) then -1
    else if isEqual(a, b) then 0
    else 1

object IntervalOrdering:

  def intervalOrdering[T](using bOrd: Ordering[Boundary[T]]): Ordering[Interval[T]] =
    new IntervalOrdering[T]

  /**
   * Less
   *
   * Checks whether A less-than B (Order Relation)
   *
   * A < B
   *
   * {{{
   *   a- < b-
   *   a+ < b+
   * }}}
   *
   * {{{
   *   - before         | b
   *   - meets          | m
   *   - overlaps       | o
   * }}}
   */
  private def isLess[T](a: Interval[T], b: Interval[T])(using bOrd: Ordering[Boundary[T]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.lt(a.left, b.left) && bOrd.lt(a.right, b.right)

  /**
   * Equals (e)
   *
   * A = B
   *
   * {{{
   *   PP (Point-Point):
   *   {p}; {q}
   *   p = q
   *
   *   II (Interval-Interval):
   *   {a-, a+}; {b-; b+}
   *   a- = b-
   *   a- < b+
   *   a+ > b-
   *   a+ = b+
   *
   *   a- = b- < a+ = b+
   * }}}
   */
  private def isEqual[T](a: Interval[T], b: Interval[T])(using bOrd: Ordering[Boundary[T]]): Boolean =
    a.nonEmpty && b.nonEmpty && bOrd.equiv(a.left, b.left) && bOrd.equiv(a.right, b.right)
