package com.github.gchudnov.mtg.ordering

/**
 * Int Ordering
 */
given IntOrdering: Ordering[Int] with
  override def compare(x: Int, y: Int): Int =
    x.compare(y)

/**
 * Int Partial Ordering
 */
given IntPartialOrdering: PartialOrdering[Int] with
  override def tryCompare(x: Int, y: Int): Option[Int] =
    Some(x.compare(y))

  override def lteq(x: Int, y: Int): Boolean =
    x < y
