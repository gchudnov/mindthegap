package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import scala.collection.mutable.ListBuffer

private[mtg] object Minus:

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
   *
   * Example #3: a - b = [c1, c2]             | [a-, pred(b-)], [succ(b+), a+]
   *
   *   [**********************************]   | [1,15]  : a
   *             [************]               | [5,10]  : b
   *   [*******]                              | [1,4]   : c1
   *                            [*********]   | [11,15] : c2
   * --+-------+-+------------+-+---------+-- |
   *   1       4 5           10          15   |
   * }}}
   */
  final def minus[T: Domain](a: Interval[T], b: Interval[T]): List[Interval[T]] =
    if a.nonEmpty && b.isEmpty then List(a)
    else
      val ordE = summon[Domain[T]].ordEndpoint
      val rs   = new ListBuffer[Interval[T]]
      if ordE.lt(a.rightEndpoint, b.leftEndpoint) || ordE.lt(b.rightEndpoint, a.leftEndpoint) then
        // non-overlapping
        rs.addOne(a)
      else
        // overlapping, consider 2 cases
        if ordE.lt(a.leftEndpoint, b.leftEndpoint) then rs.addOne(Interval.make(a.leftEndpoint, b.leftEndpoint.pred))
        if ordE.lt(b.rightEndpoint, a.rightEndpoint) then rs.addOne(Interval.make(b.rightEndpoint.succ, a.rightEndpoint))
      rs.toList

  final def minusOne[T: Domain](a: Interval[T], b: Interval[T]): Interval[T] =
    val cs = minus(a, b)
    if cs.isEmpty then Interval.empty[T]
    else if cs.size == 1 then cs.head
    else throw new UnsupportedOperationException("a.minus(b) is not defined when a.contains(b); use Interval.minus(a, b) instead")
