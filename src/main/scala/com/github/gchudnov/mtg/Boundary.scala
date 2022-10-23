package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Domain

/**
 * Left or Right boundary of an interval
 *
 * {{{
 * left (a):
 *   (-inf,
 *   [0,
 *
 * right (b):
 *   +inf)
 *   0]
 * }}}
 *
 * @param value
 *   point value
 * @param isInclude
 *   whether the given point is included or not
 */
enum Boundary[T](val value: Option[T], val isInclude: Boolean):
  case Left(a: Option[T], includeA: Boolean)  extends Boundary(a, includeA)
  case Right(b: Option[T], includeB: Boolean) extends Boundary(b, includeB)

  def isLeft: Boolean =
    this.ordinal == 0

  def isRight: Boolean =
    !isLeft

  def isBounded: Boolean =
    value.nonEmpty

  def isUnbounded: Boolean =
    value.isEmpty

  def effectiveValue(using d: Domain[T]): Option[T] =
    if isInclude then value else value.map(x => if isLeft then d.succ(x) else d.pred(x))

  def canonical(using Domain[T]): Boundary[T] =
    this match
      case Left(a, includeA) =>
        Left(effectiveValue, true)
      case Right(b, includeB) =>
        Right(effectiveValue, true)

object Boundary:

  given boundaryOrdering[T: Ordering: Domain]: Ordering[Boundary[T]] =
    new BoundaryOrdering
