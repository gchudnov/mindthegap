package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.internal.BasicRel
import com.github.gchudnov.mtg.internal.ExtendedRel
import com.github.gchudnov.mtg.internal.BasicOps
import com.github.gchudnov.mtg.internal.StaticOps

/**
 * An Interval
 *
 * Classification of Intervals:
 * {{{
 *   - Empty                            | [a+, a-] = (a+, a-) = [a+, a-) = (a+, a-] = (a-, a-) = [a-, a-) = (a-, a-] = {} = ∅
 *   - Point                            | {x} = {x | a- = x = a+}
 *   - Proper and Bounded
 *     - Open                           | (a-, a+) = {x | a- < x < a+}
 *     - Closed                         | [a-, a+] = {x | a- <= x <= a+}
 *     - LeftClosedRightOpen            | [a-, a+) = {x | a- <= x < a+}
 *     - LeftOpenRightClosed            | (a-, a+] = {x | a- < x <= a+}
 *   - LeftBounded and RightUnbounded
 *     - LeftOpen                       | (a-, +∞) = {x | x > a-}
 *     - LeftClosed                     | [a-, +∞) = {x | x >= a-}
 *   - LeftUnbounded and RightBounded
 *     - RightOpen                      | (-∞, a+) = {x | x < a+}
 *     - RightClosed                    | (-∞, a+] = {x | x < a+}
 *   - Unbounded                        | (-∞, +∞) = R
 * }}}
 *
 * NOTE: Point -- other names are Singleton, Degenerate.
 *
 * {{{
 *   Proper               - An interval that is neither Empty nor Point is said to be Proper.
 *   LeftBounded          - An interval is left-bounded, if there is a value that is smaller than all its elements.
 *   RightBounded         - An interval is right-bounded, if there is s value that is larger than all its elements.
 *   Bounded              - An interval is Bounded, if it is both Left- and Right-bounded; and is said to be Unbounded otherwise.
 *                          Bounded intervals are also commonly known as finite intervals.
 *   LeftUnbounded        - (+inf, ...
 *   RightUnbounded       - ..., +inf)
 *   HalfOpen             - includes only one of its endpoints, e.g. (0, 1]. [0, 1).
 *   Empty                - [a+, a-] = (a+, a-) = [a+, a-) = (a+, a-] = (a-, a-) = [a-, a-) = (a-, a-] = {} = ∅.
 *   Point                - Consists of a single real number: {x} = {x | a- = x = a+}.
 *   Open                 - does not include its endpoints, and is indicated with parentheses, e.g. (0, 1); (a-, a+) = {x | a- < x < a+}
 *   Closed               - an interval which includes all its limit points, e.g. [0, 1]; [a-, a+] = {x | a- <= x <= a+}
 *   LeftClosedRightOpen  - [a-, a+) = {x | a- <= x < a+}
 *   LeftOpenRightClosed  - (a-, a+] = {x | a- < x <= a+}
 *   LeftOpen             - LeftBounded and RightUnbounded; (a-, +∞) = {x | x > a-}.
 *   LeftClosed           - LeftBounded and RightUnbounded; [a-, +∞) = {x | x >= a-}.
 *   RightOpen            - LeftUnbounded and RightBounded; (-∞, a+) = {x | x < a+}.
 *   RightClosed          - LeftUnbounded and RightBounded; (-∞, a+] = {x | x < a+}.
 *   Unbounded            - Unbounded at both ends; (-∞, +∞) = R
 * }}}
 */

enum Interval[+T] extends BasicRel[T] with ExtendedRel[T] with BasicOps[T]:
  case Empty extends Interval[Nothing]
  case Point(a: T)
  case Proper[T](l: Boundary.Left[T], r: Boundary.Right[T]) extends Interval[T]

  def isEmpty: Boolean =
    this.ordinal == 0

  def isPoint: Boolean =
    this.ordinal == 1

  def isProper: Boolean =
    this.ordinal == 2

  def nonEmpty: Boolean =
    !isEmpty

  def nonPoint: Boolean =
    !isPoint

  def nonProper: Boolean =
    !isProper

  def isUnbounded: Boolean =
    this match
      case Interval.Proper(l, r) =>
        l.isUnbounded && r.isUnbounded
      case _ =>
        false

  def isBounded: Boolean =
    this match
      case Interval.Proper(l, r) =>
        l.isBounded && r.isBounded
      case Interval.Point(_) =>
        true
      case _ =>
        false

  def left[U >: T]: Boundary.Left[U] =
    this match
      case Interval.Empty =>
        throw new NoSuchElementException("Empty.left")
      case Interval.Point(a) =>
        Boundary.Left(Some(a), true)
      case Interval.Proper[U](l, _) =>
        l
      case _ =>
        throw new ClassCastException("Specified type is not compatible with the type of the Interval")

  def right[U >: T]: Boundary.Right[U] =
    this match
      case Interval.Empty =>
        throw new NoSuchElementException("Empty.right")
      case Interval.Point(a) =>
        Boundary.Right(Some(a), true)
      case Interval.Proper[U](_, r) =>
        r
      case _ =>
        throw new ClassCastException("Specified type is not compatible with the type of the Interval")

  /**
   * A canonical form of an interval is where the interval is closed on both starting and finishing sides.
   */
  def canonical[U >: T: Domain]: Interval[U] =
    this match
      case Interval.Proper[U](l, r) =>
        Interval.Proper(Boundary.Left(l.effectiveValue, true), Boundary.Right(r.effectiveValue, true))
      case x =>
        x

object Interval extends StaticOps:

  given intervalOrdering[T](using Ordering[Boundary[T]]): Ordering[Interval[T]] =
    new IntervalOrdering[T]

  /**
   * ∅
   */
  def empty[T]: Interval[T] =
    Interval.Empty

  /**
   * {x} = {x | a- = x = a+}
   *
   * @param a
   *   point
   * @return
   *   a new interval
   */
  def point[T](a: T): Interval[T] =
    Interval.Point(a)

  /**
   * {a-, a+}
   *
   * @param a
   *   left boundary
   * @param isIncludeA
   *   whether to include left boundary in the interval or not
   * @param b
   *   right boundary
   * @param isIncludeB
   *   whether to include right boundary in the interval or not
   * @return
   *   a new interval
   */
  def proper[T](a: Option[T], isIncludeA: Boolean, b: Option[T], isIncludeB: Boolean)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left(a, isIncludeA), Boundary.Right(b, isIncludeB))

  /**
   * {a-, a+}
   *
   * @param ba
   *   left boundary
   * @param bb
   *   right boundary
   * @return
   *   a new interval
   */
  def proper[T](ba: Boundary.Left[T], bb: Boundary.Right[T])(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    import Show.given
    require(bOrd.lt(ba, bb), s"${ba.show},${bb.show}: left boundary must be less than the right boundary")
    Proper(ba, bb)

  /**
   * (-∞, +∞)
   */
  def unbounded[T](using Ordering[Boundary[T]]): Interval[T] =
    proper[T](Boundary.Left(None, false), Boundary.Right(None, false))

  /**
   * (a-, a+) = {x | a- < x < a+}
   *
   * @param a
   *   left boundary
   * @param b
   *   right boundary
   * @return
   *   a new interval
   */
  def open[T](a: T, b: T)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left(Some(a), false), Boundary.Right(Some(b), false))

  /**
   * [a-, a+] = {x | a- <= x <= a+}
   *
   * @param a
   *   left boundary
   * @param b
   *   right boundary
   * @return
   *   a new interval
   */
  def closed[T](a: T, b: T)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left(Some(a), true), Boundary.Right(Some(b), true))

  /**
   * (a-, +∞) = {x | x > a-}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftOpen[T](a: T)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left(Some(a), false), Boundary.Right[T](None, false))

  /**
   * [a-, +∞) = {x | x >= a-}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftClosed[T](a: T)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left(Some(a), true), Boundary.Right[T](None, false))

  /**
   * (-∞, a+) = {x | x < a+}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightOpen[T](b: T)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left[T](None, false), Boundary.Right(Some(b), false))

  /**
   * (-∞, a+] = {x | x < a+}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightClosed[T](b: T)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left[T](None, false), Boundary.Right(Some(b), true))

  /**
   * [a-, a+) = {x | a- <= x < a+}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftClosedRightOpen[T](a: T, b: T)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left[T](Some(a), true), Boundary.Right(Some(b), false))

  /**
   * (a-, a+] = {x | a- < x <= a+}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftOpenRightClosed[T](a: T, b: T)(using Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left[T](Some(a), false), Boundary.Right(Some(b), true))

  /**
   * Make an arbitraty interval
   *
   * @param a
   *   left boundary
   * @param isIncludeA
   *   whether to include left boundary in the interval or not
   * @param b
   *   right boundary
   * @param isIncludeB
   *   whether to include right boundary in the interval or not
   * @return
   *   a new interval
   */
  def make[T: Domain](a: Option[T], isIncludeA: Boolean, b: Option[T], isIncludeB: Boolean)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    make(Boundary.Left(a, isIncludeA), Boundary.Right(b, isIncludeB))

  /**
   * Make an arbitraty interval
   *
   * @param a
   *   left boundary
   * @param b
   *   right boundary
   * @return
   *   a new interval
   */
  def make[T: Domain](ba: Boundary.Left[T], bb: Boundary.Right[T])(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    if bOrd.equiv(ba, bb) then point(ba.effectiveValue.get)
    else if bOrd.lt(bb, ba) then empty[T]
    else proper(ba, bb)
