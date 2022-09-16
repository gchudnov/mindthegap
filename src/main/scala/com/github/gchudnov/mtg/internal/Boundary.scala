package com.github.gchudnov.mtg.internal

trait Boundary[T: Ordering]:
  def value: T
  def isInclude: Boolean

/**
 * Left Boundary of an Interval
 * 
 * {{{
 *   (-inf,
 *   [0,
 * }}}
 */
final case class LeftBoundary[T: Ordering](value: T, isInclude: Boolean) extends Boundary[T]

// TODO: right boundary