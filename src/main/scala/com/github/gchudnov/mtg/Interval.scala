package com.github.gchudnov.mtg

/**
 * Generic Interval Representation.
 * 
 * TODO: need to encode open/close ; half-open / half-closed intervals somehow
 */
trait Interval[A: Ordering]:
  def x1: Option[A]
  def x2: Option[A]
