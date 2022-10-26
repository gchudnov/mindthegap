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

  /**
   * Gets the effective value of the boundary it points to
   */
  def effectiveValue(using d: Domain[T]): Option[T] =
    if isInclude then value else value.map(x => if isLeft then d.succ(x) else d.pred(x))

  /**
   * Returns a canonical representation of the Boundary, that is inclusive
   */
  def canonical(using Domain[T]): Boundary[T] =
    if isLeft then Left(effectiveValue, true)
    else Right(effectiveValue, true)

  /**
   * Moves boundary to the next successor value
   */
  def succ(using d: Domain[T]): Boundary[T] =
    if isLeft then Left(value.map(x => d.succ(x)), isInclude)
    else Right(value.map(x => d.succ(x)), isInclude)

  /**
   * Moves boundary to the previous predecessor value
   */
  def pred(using d: Domain[T]): Boundary[T] =
    if isLeft then Left(value.map(x => d.pred(x)), isInclude)
    else Right(value.map(x => d.pred(x)), isInclude)

  /**
   * Converts Right to the Left boundary
   */
  def asLeft(using Domain[T]): Boundary.Left[T] =
    if isLeft then this.asInstanceOf[Boundary.Left[T]]
    else Left(effectiveValue, true)

  /**
   * Converts Left to the Right boundary
   */
  def asRight(using Domain[T]): Boundary.Right[T] =
    if isRight then this.asInstanceOf[Boundary.Right[T]]
    else Right(effectiveValue, true)

object Boundary:

  given boundaryOrdering[T: Ordering: Domain]: Ordering[Boundary[T]] =
    new BoundaryOrdering
