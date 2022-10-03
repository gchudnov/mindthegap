package com.github.gchudnov.mtg

sealed trait Boundary[+T]:
  def value: Option[T]
  def isInclude: Boolean

  def isBounded: Boolean =
    value.nonEmpty

  def isUnbounded: Boolean =
    value.isEmpty

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
final case class LeftBoundary[+T](value: Option[T], isInclude: Boolean) extends Boundary[T]

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
final case class RightBoundary[+T](value: Option[T], isInclude: Boolean) extends Boundary[T]
