package com.github.gchudnov.mtg

/**
 * Generic Interval Representation
 */
trait Interval[A: Ordering]:
  def x1: Option[A]
  def x2: Option[A]
