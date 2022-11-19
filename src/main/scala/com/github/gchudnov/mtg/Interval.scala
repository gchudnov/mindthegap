package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.internal.BasicOps
import com.github.gchudnov.mtg.internal.BasicRel
import com.github.gchudnov.mtg.internal.ExtendedRel
import com.github.gchudnov.mtg.internal.StaticOps

/**
 * Interval
 *
 * An interval is Bounded, if it is both Left- and Right-bounded; and is said to be Unbounded otherwise. Bounded intervals are also commonly known as finite intervals.
 *
 * An empty interval has starting value greater than the ending value.
 *
 * Point is a generate interval that consists of a single value.
 *
 * An interval that is neither Empty nor Point is said to be Proper.
 */
final case class Interval[T](left: Mark[T], right: Mark[T]) extends BasicRel[T] with ExtendedRel[T] with BasicOps[T]:

  /**
   * Returns true if the interval is empty and false otherwise.
   */
  def isEmpty(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.gt(left, right)

  /**
   * Returns true if the interval is degenerate (a point) or false otherwise.
   */
  def isPoint(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.equiv(left, right)

  /**
   * Returns true if the itnerval is proper and false otherwise.
   */
  def isProper(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.lt(left, right)

  /**
   * Returns true if the interval is non-empty and false otherwise.
   */
  def nonEmpty(using Ordering[Mark[T]]): Boolean =
    !isEmpty

  /**
   * Returns true if the interval is not a point and false otherwise.
   */
  def nonPoint(using Ordering[Mark[T]]): Boolean =
    !isPoint

  /**
   * Returns true, if interval is not proper and false otherwise.
   */
  def nonProper(using Ordering[Mark[T]]): Boolean =
    !isProper

  /**
   * Swap left and right boundary.
   *
   * Can be used to create an empty interval out of a non-empty one or vice-versa.
   *
   * {{{
   *   [a-, a+] -> [a+, a-]
   * }}}
   */
  def swap: Interval[T] =
    Interval(right, left)

  /**
   * Inflate
   *
   * Applies pred and succ functions to the left and right boundaries of an interval extending it.
   *
   * {{{
   *   [a-, a+] -> [pred(a-), succ(a+)]
   *
   * Example:
   *   inflate([1, 2]) = [0, 3]
   * }}}
   */
  def inflate: Interval[T] =
    Interval(left.pred, right.succ)

  /**
   * Inflate only the left side of the interval
   *
   * {{{
   *   [a-, a+] -> [pred(a-), a+]
   *
   * Example:
   *   inflateLeft([1, 2]) = [0, 2]
   * }}}
   */
  def inflateLeft: Interval[T] =
    Interval(left.pred, right)

  /**
   * Inflate only the right side of the interval
   *
   * {{{
   *   [a-, a+] -> [a-, succ(a+)]
   *
   * Example:
   *   inflateLeft([1, 2]) = [0, 3]
   * }}}
   */
  def inflateRight: Interval[T] =
    Interval(left, right.succ)

  /**
   * Deflate
   *
   * Applies succ and pred functions to the left and right boundaries of an interval reducing it it.
   *
   * When deflating, the operation might produce an empty interval, where left boundary is greater than the right boundary.
   *
   * {{{
   *   [a-, a+] -> [succ(a-), pred(a+)]
   *
   * Example:
   *   deflate([1, 2]) = [2, 1]
   * }}}
   */
  def deflate: Interval[T] =
    Interval(left.succ, right.pred)

  /**
   * Deflate the left side of the interval
   *
   * {{{
   *   [a-, a+] -> [succ(a-), a+]
   *
   * Example:
   *   deflateLeft([1, 2]) = [2, 2]
   * }}}
   */
  def deflateLeft: Interval[T] =
    Interval(left.succ, right)

  /**
   * Deflate the right side of the interval
   *
   * {{{
   *   [a-, a+] -> [a-, pred(a+)]
   *
   * Example:
   *   deflateRight([1, 2]) = [1, 1]
   * }}}
   */
  def deflateRight: Interval[T] =
    Interval(left, right.pred)

  /**
   * Canonical
   *
   * A canonical form of an interval is where the interval is closed on both starting and finishing sides:
   *
   * {{{
   *  [a-, a+]
   * }}}
   */
  def canonical(using Domain[T]): Interval[T] =
    Interval(left.at, right.at)

  /**
   * Normalize
   *
   * Reduces the amount of `succ` and `pred` operations so that the interval is represented in one of the following ways:
   *
   * {{{
   *   [a-, a+]
   *   [a-, a+)
   *   (a-, a+)
   *   (a-, a+]
   * }}}
   */
  def normalize(using Domain[T]): Interval[T] =
    val l = normalizeLeft
    val r = normalizeRight
    if l != left || r != right then Interval(l, r)
    else this

  private def normalizeLeft(using Domain[T]): Mark[T] =
    left match
      case Mark.At(_) =>
        left
      case Mark.Pred(_) =>
        left.at
      case Mark.Succ(xx) =>
        Mark.Succ(xx.at)

  private def normalizeRight(using Domain[T]): Mark[T] =
    right match
      case Mark.At(_) =>
        right
      case Mark.Pred(yy) =>
        Mark.Pred(yy.at)
      case Mark.Succ(_) =>
        right.at

object Interval extends StaticOps:

  given intervalOrdering[T](using Ordering[Mark[T]]): Ordering[Interval[T]] =
    new IntervalOrdering[T]

  /**
   * Empty
   *
   * An empty interval has starting value greater than the ending value.
   *
   * ∅
   *
   * {{{
   *   [a+, a-] = (a+, a-) = [a+, a-) = (a+, a-] = (a-, a-) = [a-, a-) = (a-, a-] = {} = ∅.
   * }}}
   */
  def empty[T]: Interval[T] =
    Interval(Mark.at(Value.InfPos), Mark.at(Value.InfNeg))

  /**
   * Point
   *
   * A degenerate interval where the left boundary is equal to the right boundary.
   *
   * {{{
   *   {x} = {x | a- = x = a+}
   * }}}
   */
  def point[T](x: Mark[T]): Interval[T] =
    Interval(x, x)

  def point[T](x: Value[T]): Interval[T] =
    point(Mark.at(x))

  def point[T](x: T): Interval[T] =
    point(Mark.at(x))

  /**
   * Proper
   *
   * An interval that is neither Empty nor Point is said to be Proper.
   *
   * {{{
   *   {a-, a+}
   * }}}
   */
  def proper[T](x: Mark[T], y: Mark[T])(using ordM: Ordering[Mark[T]]): Interval[T] =
    require(ordM.lt(x, y), s"left boundary '${x}' must be less than the right boundary '${y}'")
    Interval(x, y)

  /**
   * Unbounded
   *
   * An interval is unbounded if is has no left and right bounds.
   *
   * {{{
   *   (-∞, +∞)
   * }}}
   */
  def unbounded[T](using Ordering[Mark[T]]): Interval[T] =
    proper[T](Mark.at(Value.InfNeg), Mark.at(Value.InfPos))

  /**
   * Open
   *
   * Does not include its endpoints, and is indicated with parentheses, e.g. (0, 5).
   *
   * {{{
   *   (a-, a+) = {x | a- < x < a+}
   * }}}
   */
  def open[T](x: T, y: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.succ(x), Mark.pred(y))

  /**
   * Closed
   *
   * A closed intervals
   *
   * {{{
   *   [a-, a+] = {x | a- <= x <= a+}
   * }}}
   */
  def closed[T](x: T, y: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(x), Mark.at(y))

  /**
   * LeftOpen
   *
   * An interval is left-bounded, if there is a value that is smaller than all its elements.
   *
   * {{{
   *   (a-, +∞) = {x | x > a-}
   * }}}
   */
  def leftOpen[T](x: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.succ(x), Mark.at(Value.InfPos))

  /**
   * LeftClosed
   *
   * An interval is left-bounded, if there is a value that is smaller than all its elements.
   *
   * {{{
   *   [a-, +∞) = {x | x >= a-}
   * }}}
   */
  def leftClosed[T](x: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(x), Mark.at(Value.InfPos))

  /**
   * RightOpen
   *
   * An interval is right-bounded, if there is s value that is larger than all its elements.
   *
   * {{{
   *   (-∞, a+) = {x | x < a+}
   * }}}
   */
  def rightOpen[T](x: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(Value.InfNeg), Mark.pred(x))

  /**
   * RightClosed
   *
   * An interval is right-bounded, if there is s value that is larger than all its elements.
   *
   * {{{
   *   (-∞, a+] = {x | x < a+}
   * }}}
   */
  def rightClosed[T](x: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(Value.InfNeg), Mark.at(x))

  /**
   * LeftClosedRightOpen
   *
   * An interval closed on the left side and open on the right side.
   *
   * {{{
   *   [a-, a+) = {x | a- <= x < a+}
   * }}}
   */
  def leftClosedRightOpen[T](x: T, y: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.at(x), Mark.pred(y))

  /**
   * LeftOpenRightClosed
   *
   * An interval open on the left side and closed on the right side.
   *
   * {{{
   *   (a-, a+] = {x | a- < x <= a+}
   * }}}
   */
  def leftOpenRightClosed[T](x: T, y: T)(using Ordering[Mark[T]]): Interval[T] =
    proper(Mark.succ(x), Mark.at(y))

  /**
   * Make an arbitrary interval
   */
  def make[T](x: Value[T], y: Value[T]): Interval[T] =
    make(Mark.at(x), Mark.at(y))

  /**
   * Make an arbitrary interval.
   */
  def make[T](x: Mark[T], y: Mark[T]): Interval[T] =
    Interval(x, y)
