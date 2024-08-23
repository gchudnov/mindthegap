package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*

/**
 * Sorts Intervals using the default sorting order.
 *
 * {{{
 *   After Sorting:
 *   [ (-∞,20), (-∞,+∞), [1,3], (1,4), [2,6], [8,10], [10,+∞), [15,18], ∅ ]
 * }}}
 */
object Sort extends App:
  val a = Interval.closed(1, 3)
  val b = Interval.closed(2, 6)
  val c = Interval.closed(8, 10)
  val d = Interval.closed(15, 18)
  val e = Interval.open(1, 4)
  val f = Interval.leftClosed(10)
  val g = Interval.rightOpen(20)
  val h = Interval.empty[Int]
  val i = Interval.unbounded[Int]

  val intervals = List(a, b, c, d, e, f, g, h, i)

  val sorted = intervals.sorted

  println(sorted)
