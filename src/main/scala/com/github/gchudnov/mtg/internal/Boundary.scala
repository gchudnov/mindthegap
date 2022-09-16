package com.github.gchudnov.mtg.internal

trait Boundary[T: Ordering]:
  def value: Option[T]
  def isInclude: Boolean

/**
 * Left Boundary of an Interval
 *
 * {{{
 *   (-inf,
 *   [0,
 * }}}
 *
 * @param value
 *   point value
 * @param isInclude
 *   specifies whether the given point is inclusive or exclusive
 */
final case class LeftBoundary[T: Ordering](value: Option[T], isInclude: Boolean) extends Boundary[T]

/**
 * Right Boundary of an Interval
 *
 * {{{
 *   , +inf)
 *   , 0]
 * }}}
 *
 * @param value
 *   point value
 * @param isInclude
 *   specifies whether the given point is inclusive or exclusive
 */
final case class RightBoundary[T: Ordering](value: Option[T], isInclude: Boolean) extends Boundary[T]
