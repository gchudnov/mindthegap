package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Domain

sealed trait Boundary[+T]:
  def value: Option[T]
  def isInclude: Boolean

  def effectiveValue: Option[T]

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
final case class LeftBoundary[+T: Domain](value: Option[T], isInclude: Boolean) extends Boundary[T]:

  override def effectiveValue: Option[T] =
    if isInclude then value else value.map(x => summon[Domain[T]].succ(x))

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
final case class RightBoundary[+T: Domain](value: Option[T], isInclude: Boolean) extends Boundary[T]:

  override def effectiveValue: Option[T] =
    if isInclude then value else value.map(x => summon[Domain[T]].pred(x))
