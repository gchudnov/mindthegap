package com.github.gchudnov.mtg

/**
  * Generic Interval Representation
  */
trait Interval[A: PartialOrdering]:
  def x1: A
  def x2: A
