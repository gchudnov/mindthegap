package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Value

sealed trait Boundary[T: Value]:
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
final case class LeftBoundary[T: Value](value: Option[T], isInclude: Boolean) extends Boundary[T]:

  override def effectiveValue: Option[T] =
    if isInclude then value else value.map(x => summon[Value[T]].succ(x))

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
final case class RightBoundary[T: Value](value: Option[T], isInclude: Boolean) extends Boundary[T]:

  override def effectiveValue: Option[T] =
    if isInclude then value else value.map(x => summon[Value[T]].pred(x))
