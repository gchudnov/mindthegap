package com.github.gchudnov.mtg

/**
 * Trait to get a previous and the next value.
 */
trait Domain[T]:
  /**
   * Next value
   *
   * @param x
   *   current value
   * @return
   *   x + epsilon
   */
  def succ(x: T): T

  /**
   * Previous value
   *
   * @param x
   *   current value
   * @return
   *   x - epsilon
   */
  def pred(x: T): T
