package com.github.gchudnov.mtg.examples

import com.github.gchudnov.mtg.*

/**
 * Sorts Intervals using the default sorting order.
 */
object SortIntervals extends App:

  val intervals = List(
    Interval.closed(1, 3),
    Interval.closed(2, 6),
    Interval.closed(8, 10),
    Interval.closed(15, 18),
    Interval.open(1, 4),
    Interval.leftClosed(10),
    Interval.rightOpen(20),
    Interval.empty[Int],
    Interval.unbounded[Int],
  )

  val sorted = intervals.sorted

  /**
   * {{{
   *   List(
   *     Interval(At(InfNeg),Pred(At(Finite(20)))),
   *     Interval(At(InfNeg),At(InfPos)),
   *     Interval(At(Finite(1)),At(Finite(3))),
   *     Interval(Succ(At(Finite(1))),Pred(At(Finite(4)))),
   *     Interval(At(Finite(2)),At(Finite(6))),
   *     Interval(At(Finite(8)),At(Finite(10))),
   *     Interval(At(Finite(10)),At(InfPos)),
   *     Interval(At(Finite(15)),At(Finite(18))),
   *     Interval(At(InfPos),At(InfNeg))
   *   )
   * }}}
   */
  println(sorted)

  // NOTE: th print intervals in a human-readable form, use `.asString`
