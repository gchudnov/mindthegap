package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.ordering.ValueOrdering
import com.github.gchudnov.mtg.ordering.MarkOrdering

// TODO: allow this trait to use for Value, Mark to avoid extra implicits

/**
 * Trait to get a successor and the predecessor value.
 *
 * Having the operators to get successor and predecessor, we can define:
 *
 * {{{
 *   (x, y] = [succ(x), y]
 *   [x, y) = [x, pred(y)]
 *   (x, y) = [succ(x), pred(y)].
 * }}}
 */
trait Domain[T] extends Ordering[T]:
  self: Ordering[T] =>

  val ordValue: Ordering[Value[T]] = 
    new ValueOrdering[T]()(self)

  val ordMark: Ordering[Mark[T]] =
    new MarkOrdering[T]()(this)

  /**
   * Successor value
   *
   * @param x
   *   current value
   * @return
   *   x + epsilon
   */
  def succ(x: T): T

  /**
   * Predecessor value
   *
   * @param x
   *   current value
   * @return
   *   x - epsilon
   */
  def pred(x: T): T

  /**
   * Count
   *
   * Returns the number of points (cardinality / duration of an interval) in interval defined by [start, end].
   *
   * @param start
   *   inclusive value
   * @param end
   *   inclusive value
   * @return
   *   number of points in interval
   */
  def count(start: T, end: T): Long

object Domain extends DomainDefaults
