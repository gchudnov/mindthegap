package com.github.gchudnov.mtg

/**
 * An Interval
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
 * NOTE: Degenerate -- other names are Point, Singleton.
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
sealed trait Interval[+T]:
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
  override final def left: LeftBoundary[Nothing] =
    throw new NoSuchElementException("Empty.left")

  override final def right: RightBoundary[Nothing] =
    throw new NoSuchElementException("Empty.right")

  override final def isEmpty: Boolean     = true
  override final def isDegenrate: Boolean = false
  override final def isProper: Boolean    = false
  override final def isUnbounded: Boolean = false

/**
 * Degenerate Interval
 */
final case class Degenerate[T](a: T) extends Interval[T]:
  override final def left: LeftBoundary[T] =
    LeftBoundary(Some(a), true)

  override final def right: RightBoundary[T] =
    RightBoundary(Some(a), true)

  override val isEmpty: Boolean     = false
  override val isDegenrate: Boolean = true
  override val isProper: Boolean    = false
  override val isUnbounded: Boolean = false

/**
 * Proper Interval
 */
final case class Proper[T](left: LeftBoundary[T], right: RightBoundary[T]) extends Interval[T]:

  override val isEmpty: Boolean     = false
  override val isDegenrate: Boolean = false
  override val isProper: Boolean    = true
  override def isUnbounded: Boolean = left.value.isEmpty && right.value.isEmpty

object Interval:

  /**
   * ∅
   */
  def empty[T]: Interval[T] =
    Empty

  /**
   * (-∞, +∞)
   */
  def unbounded[T]: Interval[T] =
    Proper[T](LeftBoundary[T](None, false), RightBoundary[T](None, false))

  /**
   * [a, a] = {a}
   *
   * @param a
   *   point
   * @return
   *   a new interval
   */
  def degenerate[T](a: T): Interval[T] =
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
  def proper[T](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean): Interval[T] =
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
  def open[T](a: T, b: T): Interval[T] =
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
  def closed[T](a: T, b: T): Interval[T] =
    Proper(LeftBoundary(Some(a), true), RightBoundary(Some(b), true))

  /**
   * (a, +∞) = {x | x > a}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftOpen[T](a: T): Interval[T] =
    Proper(LeftBoundary(Some(a), false), RightBoundary[T](None, false))

  /**
   * [a, +∞) = {x | x >= a}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftClosed[T](a: T): Interval[T] =
    Proper(LeftBoundary(Some(a), true), RightBoundary[T](None, false))

  /**
   * (-∞, b) = {x | x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightOpen[T](b: T): Interval[T] =
    Proper(LeftBoundary[T](None, false), RightBoundary(Some(b), false))

  /**
   * (-∞, b] = {x | x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightClosed[T](b: T): Interval[T] =
    Proper(LeftBoundary[T](None, false), RightBoundary(Some(b), true))

  /**
   * [a, b) = {x | a <= x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftClosedRightOpen[T](a: T, b: T): Interval[T] =
    proper(Some(a), Some(b), isIncludeA = true, isIncludeB = false)

  /**
   * (a, b] = {x | a < x <= b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftOpenRightClosed[T](a: T, b: T): Interval[T] =
    proper(Some(a), Some(b), isIncludeA = false, isIncludeB = true)
