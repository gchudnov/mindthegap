package com.github.gchudnov.mtg.examples

import com.github.gchudnov.mtg.*

/**
 * Prints Intervals in a human-readable form.
 */
object PrintHumanReadable extends App:

  val intervals = List(
    Interval.closed(1, 3),
    Interval.closed(2, 6),
    Interval.closed(8, 10),
    Interval.closed(15, 18),
    Interval.open(1, 4),
    Interval.leftClosed(10),
    Interval.rightOpen(20),
    Interval.empty[Int],
    Interval.unbounded[Int]
  )

  /**
   * {{{
   *   [1,3]
   *   [2,6]
   *   [8,10]
   *   [15,18]
   *   (1,4)
   *   [10,+∞)
   *   (-∞,20)
   *   ∅
   *   (-∞,+∞)
   * }}}
   */
  intervals.foreach(it => println(it.asString))
