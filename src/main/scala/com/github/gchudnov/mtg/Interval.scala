package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Domains.nothingDomain

/**
 * Generic Interval Representation
 *
 * Classification of Intervals:
 * {{{
 *   - Empty                            | [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
 *   - Degenerate                       | [a, a] = {a}
 *   - Proper and Bounded
 *     - Open                           | (a, b) = {x | a < x < b}
 *     - Closed                         | [a, b] = {x | a <= x <= b}
 *     - LeftClosedRightOpen            | [a, b) = {x | a <= x < b}
 *     - LeftOpenRightClosed            | (a, b] = {x | a < x <= b}
 *   - LeftBounded and RightUnbounded
 *     - LeftOpen                       | (a, +∞) = {x | x > a}
 *     - LeftClosed                     | [a, +∞) = {x | x >= a}
 *   - LeftUnbounded and RightBounded
 *     - RightOpen                      | (-∞, b) = {x | x < b}
 *     - RightClosed                    | (-∞, b] = {x | x < b}
 *   - Unbounded                        | (-∞, +∞) = R
 * }}}
 *
 * {{{
 *   Proper               - An interval that is neither Empty nor Degenerate is said to be Proper.
 *   LeftBounded          - An interval is left-bounded, if there is a value that is smaller than all its elements.
 *   RightBounded         - An interval is right-bounded, if there is s value that is larger than all its elements.
 *   Bounded              - An interval is Bounded, if it is both Left- and Right-bounded; and is said to be Unbounded otherwise.
 *                          Bounded intervals are also commonly known as finite intervals.
 *   LeftUnbounded        - (+inf, ...
 *   RightUnbounded       - ..., +inf)
 *   HalfOpen             - includes only one of its endpoints, e.g. (0, 1]. [0, 1).
 *   Empty                - [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅.
 *   Degenerate           - Consists of a single real number: [a, a] = {a}.
 *   Open                 - does not include its endpoints, and is indicated with parentheses, e.g. (0, 1); (a, b) = {x | a < x < b}
 *   Closed               - an interval which includes all its limit points, e.g. [0, 1]; [a, b] = {x | a <= x <= b}
 *   LeftClosedRightOpen  - [a, b) = {x | a <= x < b}
 *   LeftOpenRightClosed  - (a, b] = {x | a < x <= b}
 *   LeftOpen             - LeftBounded and RightUnbounded; (a, +∞) = {x | x > a}.
 *   LeftClosed           - LeftBounded and RightUnbounded; [a, +∞) = {x | x >= a}.
 *   RightOpen            - LeftUnbounded and RightBounded; (-∞, b) = {x | x < b}.
 *   RightClosed          - LeftUnbounded and RightBounded; (-∞, b] = {x | x < b}.
 *   Unbounded            - Unbounded at both ends; (-∞, +∞) = R
 * }}}
 */
sealed trait Interval[+T: Ordering: Domain]:
  def left: LeftBoundary[T]
  def right: RightBoundary[T]

  def isEmpty: Boolean
  def isDegenrate: Boolean
  def isProper: Boolean
  def isUnbounded: Boolean

  def nonEmpty: Boolean =
    !isEmpty

  def nonDegenerate: Boolean =
    !isDegenrate

  def nonProper: Boolean =
    !isProper

  def nonUnbounded: Boolean =
    !isUnbounded

/**
 * Empty Interval
 */
case object Empty extends Interval[Nothing]:
  override val left: LeftBoundary[Nothing] =
    LeftBoundary(Some(null.asInstanceOf[Nothing]), false)

  override val right: RightBoundary[Nothing] =
    RightBoundary(Some(null.asInstanceOf[Nothing]), false)

  override val isEmpty: Boolean     = true
  override val isDegenrate: Boolean = false
  override val isProper: Boolean    = false
  override val isUnbounded: Boolean = false

/**
 * Degenerate Interval
 */
final case class Degenerate[T: Ordering: Domain](a: T) extends Interval[T]:
  override def left: LeftBoundary[T] =
    LeftBoundary(Some(a), false)

  override def right: RightBoundary[T] =
    RightBoundary(Some(a), false)

  override val isEmpty: Boolean     = false
  override val isDegenrate: Boolean = true
  override val isProper: Boolean    = false
  override val isUnbounded: Boolean = false

/**
 * Proper Interval
 */
final case class Proper[T: Ordering: Domain](left: LeftBoundary[T], right: RightBoundary[T]) extends Interval[T]:

  // make sure that a < b, if provided
  require((for x <- left.value; y <- right.value yield summon[Ordering[T]].lt(x, y)).getOrElse(true))

  override val isEmpty: Boolean     = false
  override val isDegenrate: Boolean = false
  override val isProper: Boolean    = true
  override def isUnbounded: Boolean = left.value.isEmpty && right.value.isEmpty

object Interval:

  /**
   * ∅
   */
  def empty[T: Ordering: Domain]: Interval[T] =
    Empty

  /**
   * (-∞, +∞)
   */
  def unbounded[T: Ordering: Domain]: Interval[T] =
    Proper[T](LeftBoundary[T](None, false), RightBoundary[T](None, false))

  /**
   * [a, a] = {a}
   *
   * @param a
   *   point
   * @return
   *   a new interval
   */
  def degenerate[T: Ordering: Domain](a: T): Interval[T] =
    Degenerate(a)

  /**
   * {a, b}
   *
   * @param a
   *   left boundary
   * @param b
   *   right boundary
   * @param isIncludeA
   *   whether to include left boundary in the interval or not
   * @param isIncludeB
   *   whether to include right boundary in the interval or not
   * @return
   *   a new interval
   */
  def proper[T: Ordering: Domain](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean): Interval[T] =
    Proper(LeftBoundary(a, isIncludeA), RightBoundary(b, isIncludeB))

  /**
   * (a, b) = {x | a < x < b}
   *
   * @param a
   *   left boundary
   * @param b
   *   right boundary
   * @return
   *   a new interval
   */
  def open[T: Ordering: Domain](a: T, b: T): Interval[T] =
    Proper(LeftBoundary(Some(a), false), RightBoundary(Some(b), false))

  /**
   * [a, b] = {x | a <= x <= b}
   *
   * @param a
   *   left boundary
   * @param b
   *   right boundary
   * @return
   *   a new interval
   */
  def closed[T: Ordering: Domain](a: T, b: T): Interval[T] =
    Proper(LeftBoundary(Some(a), true), RightBoundary(Some(b), true))

  /**
   * (a, +∞) = {x | x > a}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftOpen[T: Ordering: Domain](a: T): Interval[T] =
    Proper(LeftBoundary(Some(a), false), RightBoundary[T](None, false))

  /**
   * [a, +∞) = {x | x >= a}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftClosed[T: Ordering: Domain](a: T): Interval[T] =
    Proper(LeftBoundary(Some(a), true), RightBoundary[T](None, false))

  /**
   * (-∞, b) = {x | x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightOpen[T: Ordering: Domain](b: T): Interval[T] =
    Proper(LeftBoundary[T](None, false), RightBoundary(Some(b), false))

  /**
   * (-∞, b] = {x | x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightClosed[T: Ordering: Domain](b: T): Interval[T] =
    Proper(LeftBoundary[T](None, false), RightBoundary(Some(b), true))

  /**
   * [a, b) = {x | a <= x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftClosedRightOpen[T: Ordering: Domain](a: T, b: T): Interval[T] =
    proper(Some(a), Some(b), isIncludeA = true, isIncludeB = false)

  /**
   * (a, b] = {x | a < x <= b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftOpenRightClosed[T: Ordering: Domain](a: T, b: T): Interval[T] =
    proper(Some(a), Some(b), isIncludeA = false, isIncludeB = true)

  /**
   * Make an arbitraty interval
   *
   * @param a
   *   left boundary
   * @param b
   *   right boundary
   * @param isIncludeA
   *   whether to include left boundary in the interval or not
   * @param isIncludeB
   *   whether to include right boundary in the interval or not
   * @return
   */
  def make[T: Ordering: Domain](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean): Interval[T] =
    val ord = summon[Ordering[T]]
    (a, b) match
      case (Some(x), Some(y)) =>
        if ord.equiv(x, y) && isIncludeA && isIncludeB then degenerate(x)
        else if ord.lt(x, y) then proper(a, b, isIncludeA, isIncludeB)
        else empty[T]
      case _ =>
        proper(a, b, isIncludeA, isIncludeB)
