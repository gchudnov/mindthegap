package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*

/**
 * Prints Intervals
 *
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
object Print extends App:
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
  intervals.foreach(i => println(i))
