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
final case class Interval[T](left: Mark[T], right: Mark[T]) extends BasicRel[T] with ExtendedRel[T] with BasicOps[T]:

  def isEmpty(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.gt(left, right)

  def isPoint(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.equiv(left, right)

  def isProper(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.lt(left, right)

  def nonEmpty(using Ordering[Mark[T]]): Boolean =
    !isEmpty

  def nonPoint(using Ordering[Mark[T]]): Boolean =
    !isPoint

  def nonProper(using Ordering[Mark[T]]): Boolean =
    !isProper

  /**
   * A canonical form of an interval is where the interval is closed on both starting and finishing sides.
   */
  def canonical(using Domain[T]): Interval[T] =
    Interval(left.at, right.at)

object Interval extends StaticOps:

  given intervalOrdering[T](using Ordering[Mark[T]]): Ordering[Interval[T]] =
    new IntervalOrdering[T]

  /**
   * ∅
   */
  def empty[T]: Interval[T] =
    Interval(Mark.at(Value.InfPos), Mark.at(Value.InfNeg))

  /**
   * {x} = {x | a- = x = a+}
   */
  def point[T](x: Mark[T]): Interval[T] =
    Interval(x, x)

  def point[T](x: Value[T]): Interval[T] =
    point(Mark.at(x))

  def point[T](x: T): Interval[T] =
    point(Mark.at(x))

  /**
   * {a-, a+}
   */
  def proper[T](x: Mark[T], y: Mark[T])(using ordM: Ordering[Mark[T]]): Interval[T] =
    require(ordM.lt(x, y), s"left boundary '${x}' must be less than the right boundary '${y}'")
    Interval(x, y)

  /**
   * (-∞, +∞)
   */
  def unbounded[T](using Ordering[Mark[T]]): Interval[T] =
    proper[T](Mark.at(Value.InfNeg), Mark.at(Value.InfPos))

  /**
   * (a-, a+) = {x | a- < x < a+}
   */
  def open[T](x: T, y: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.succ(x), Mark.pred(y))

  /**
   * [a-, a+] = {x | a- <= x <= a+}
   */
  def closed[T](x: T, y: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(x), Mark.at(y))

  /**
   * (a-, +∞) = {x | x > a-}
   */
  def leftOpen[T](x: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.succ(x), Mark.at(Value.InfPos))

  /**
   * [a-, +∞) = {x | x >= a-}
   */
  def leftClosed[T](x: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(x), Mark.at(Value.InfPos))

  /**
   * (-∞, a+) = {x | x < a+}
   */
  def rightOpen[T](x: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(Value.InfNeg), Mark.pred(x))

  /**
   * (-∞, a+] = {x | x < a+}
   */
  def rightClosed[T](x: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(Value.InfNeg), Mark.at(x))

  /**
   * [a-, a+) = {x | a- <= x < a+}
   */
  def leftClosedRightOpen[T](x: T, y: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(x), Mark.pred(y))

  /**
   * (a-, a+] = {x | a- < x <= a+}
   */
  def leftOpenRightClosed[T](x: T, y: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.succ(x), Mark.at(y))

  /**
   * Make an arbitraty interval
   */
  def make[T](x: Value[T], y: Value[T]): Interval[T] =
    make(Mark.at(x), Mark.at(y))

  /**
   * Make an arbitraty interval: it might be empty, point or proper.
   */
  def make[T](x: Mark[T], y: Mark[T]): Interval[T] =
    Interval(x, y)
