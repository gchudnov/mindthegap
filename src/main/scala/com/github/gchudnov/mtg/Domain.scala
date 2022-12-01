package com.github.gchudnov.mtg

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
trait Domain[T]:
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
    * Returns the number of points in interval defined by [start, end]. In other words, it returns the cardinality (length / duration of an interval).
    *
    * @param start
    * @param end
    * @return
    */
  def count(start: T, end: T): Long

object Domain extends DomainDefaults
