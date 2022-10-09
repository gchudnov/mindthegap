package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Domains.nothingDomain
import com.github.gchudnov.mtg.internal.IntervalRel

import Domains.given
import BoundaryOrdering.given


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
sealed trait Interval[+T] extends IntervalRel[T]:
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
final case class Degenerate[T: Domain](a: T) extends Interval[T]:
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
final case class Proper[T](left: LeftBoundary[T], right: RightBoundary[T])(using bOrd: Ordering[Boundary[T]]) extends Interval[T]:
  import com.github.gchudnov.mtg.Show.*

  // TODO: make the constructor private and extract the require ???

  // The endpoint relation left (a-) < right (a+) is required for a proper interval
  require(bOrd.lt(left, right), s"${left.show},${right.show}: left boundary must be less than the right boundary")

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
  def unbounded[T: Domain](using bOrd: Ordering[Boundary[T]]): Interval[T] =
    Proper[T](LeftBoundary[T](None, false), RightBoundary[T](None, false))

  /**
   * [a, a] = {a}
   *
   * @param a
   *   point
   * @return
   *   a new interval
   */
  def degenerate[T: Domain](a: T): Interval[T] =
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
  def proper[T: Domain](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
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
  def open[T: Domain](a: T, b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
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
  def closed[T: Domain](a: T, b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    Proper(LeftBoundary(Some(a), true), RightBoundary(Some(b), true))

  /**
   * (a, +∞) = {x | x > a}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftOpen[T: Domain](a: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    Proper(LeftBoundary(Some(a), false), RightBoundary[T](None, false))

  /**
   * [a, +∞) = {x | x >= a}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftClosed[T: Domain](a: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    Proper(LeftBoundary(Some(a), true), RightBoundary[T](None, false))

  /**
   * (-∞, b) = {x | x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightOpen[T: Domain](b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    Proper(LeftBoundary[T](None, false), RightBoundary(Some(b), false))

  /**
   * (-∞, b] = {x | x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightClosed[T: Domain](b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    Proper(LeftBoundary[T](None, false), RightBoundary(Some(b), true))

  /**
   * [a, b) = {x | a <= x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftClosedRightOpen[T: Domain](a: T, b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    proper(Some(a), Some(b), isIncludeA = true, isIncludeB = false)

  /**
   * (a, b] = {x | a < x <= b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftOpenRightClosed[T: Domain](a: T, b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
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
  def make[T: Domain](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    val ba = LeftBoundary(a, isIncludeA)
    val bb = RightBoundary(b, isIncludeB)
    make(ba, bb)

  def make[T: Domain](ba: LeftBoundary[T], bb: RightBoundary[T])(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    (ba.value, bb.value) match
      case (Some(x), Some(y)) =>
        if bOrd.equiv(ba, bb) then degenerate(x)
        else if bOrd.lt(bb, ba) then empty[T]
        else proper(ba.value, bb.value, ba.isInclude, bb.isInclude)
      case _ =>
        proper(ba.value, bb.value, ba.isInclude, bb.isInclude)
