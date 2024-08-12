package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.ordering.ValueOrdering
import com.github.gchudnov.mtg.ordering.EndpointOrdering
import internal.DomainLowPriority

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
    new ValueOrdering[T]()(using self)

  val ordEndpoint: Ordering[Endpoint[T]] =
    new EndpointOrdering[T]()(using this)

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

object Domain extends DomainLowPriority
