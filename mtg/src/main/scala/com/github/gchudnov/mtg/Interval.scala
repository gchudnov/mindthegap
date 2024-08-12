package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.internal.AlgBasic
import com.github.gchudnov.mtg.internal.AlgStatic
import com.github.gchudnov.mtg.internal.RelBasic
import com.github.gchudnov.mtg.internal.RelExtended
import com.github.gchudnov.mtg.internal.RelStatic
import com.github.gchudnov.mtg.ordering.IntervalOrdering

export com.github.gchudnov.mtg.ordering.IntervalOrdering

/**
 * Interval
 */
final case class Interval[T: Domain](left: Endpoint[T], right: Endpoint[T]) extends RelBasic[T] with RelExtended[T] with AlgBasic[T]:

  /**
   * Get the size of the interval
   *
   * @return
   *   Some(N) if the interval is finite and None otherwise.
   */
  def size: Option[Long] =
    (left.eval, right.eval) match
      case (Value.Finite(x), Value.Finite(y)) =>
        Some(summon[Domain[T]].count(x, y))
      case _ =>
        None

  /**
   * Returns true if the interval is empty and false otherwise.
   */
  def isEmpty: Boolean =
    summon[Domain[T]].ordEndpoint.gt(left, right)

  /**
   * Returns true if the interval is degenerate (a point) or false otherwise.
   */
  def isPoint: Boolean =
    summon[Domain[T]].ordEndpoint.equiv(left, right)

  /**
   * Returns true if the interval is proper and false otherwise.
   */
  def isProper: Boolean =
    summon[Domain[T]].ordEndpoint.lt(left, right)

  /**
   * Returns true if the interval is non-empty and false otherwise.
   */
  def nonEmpty: Boolean =
    !isEmpty

  /**
   * Returns true if the interval is not a point and false otherwise.
   */
  def nonPoint: Boolean =
    !isPoint

  /**
   * Returns true, if interval is not proper and false otherwise.
   */
  def nonProper: Boolean =
    !isProper

  /**
   * Swap left and right endpoint.
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
   * Applies pred and succ functions to the left and right endpoints of an interval extending it.
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
   * Applies succ and pred functions to the left and right endpoints of an interval reducing it it.
   *
   * When deflating, the operation might produce an empty interval, where left endpoint is greater than the right endpoint.
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
   * A canonical form of an interval is where the interval is closed on both endpoints:
   *
   * {{{
   *  [a-, a+]
   * }}}
   */
  def canonical: Interval[T] =
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
  def normalize: Interval[T] =
    val l = normalizeLeft
    val r = normalizeRight
    if l != left || r != right then Interval(l, r)
    else this

  private def normalizeLeft: Endpoint[T] =
    left match
      case Endpoint.At(_) =>
        left
      case Endpoint.Pred(_) =>
        left.at
      case Endpoint.Succ(xx) =>
        Endpoint.Succ(xx.at)

  private def normalizeRight: Endpoint[T] =
    right match
      case Endpoint.At(_) =>
        right
      case Endpoint.Pred(yy) =>
        Endpoint.Pred(yy.at)
      case Endpoint.Succ(_) =>
        right.at

object Interval extends AlgStatic with RelStatic:

  given intervalOrdering[T: Domain]: Ordering[Interval[T]] =
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
  def empty[T: Domain]: Interval[T] =
    Interval(Endpoint.at(Value.InfPos), Endpoint.at(Value.InfNeg))

  /**
   * Point
   *
   * A degenerate interval where the left endpoint is equal to the right endpoint.
   *
   * {{{
   *   {x} = {x | a- = x = a+}
   * }}}
   */
  def point[T: Domain](x: Endpoint[T]): Interval[T] =
    Interval(x, x)

  def point[T: Domain](x: Value[T]): Interval[T] =
    point(Endpoint.at(x))

  def point[T: Domain](x: T): Interval[T] =
    point(Endpoint.at(x))

  /**
   * Proper
   *
   * An interval that is neither Empty nor Point is said to be Proper.
   *
   * {{{
   *   {a-, a+}
   * }}}
   */
  def proper[T: Domain](x: Endpoint[T], y: Endpoint[T]): Interval[T] =
    require(summon[Domain[T]].ordEndpoint.lt(x, y), s"left endpoint '${x}' must be less than the right endpoint '${y}'")
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
  def unbounded[T: Domain]: Interval[T] =
    proper[T](Endpoint.at(Value.InfNeg), Endpoint.at(Value.InfPos))

  /**
   * Open
   *
   * Does not include its endpoints, and is indicated with parentheses, e.g. (0, 5).
   *
   * {{{
   *   (a-, a+) = {x | a- < x < a+}
   * }}}
   */
  def open[T: Domain](x: T, y: T): Interval[T] =
    proper(Endpoint.succ(x), Endpoint.pred(y))

  /**
   * Create one of the open intervals
   *
   * {{{
   *   (a-, a+)
   *   (a-, +∞)
   *   (-∞, a+)
   *   (-∞, +∞)
   * }}}
   */
  def open[T: Domain](x: Option[T], y: Option[T]): Interval[T] =
    (x, y) match
      case (Some(x), Some(y)) => open(x, y)
      case (Some(x), None)    => leftOpen(x)
      case (None, Some(y))    => rightOpen(y)
      case (None, None)       => unbounded

  /**
   * Closed
   *
   * A closed intervals
   *
   * {{{
   *   [a-, a+] = {x | a- <= x <= a+}
   * }}}
   */
  def closed[T: Domain](x: T, y: T): Interval[T] =
    proper(Endpoint.at(x), Endpoint.at(y))

  /**
   * Create one of the closed intervals
   *
   * {{{
   *   [a-, a+]
   *   [a-, +∞)
   *   (-∞, a+]
   *   (-∞, +∞)
   * }}}
   */
  def closed[T: Domain](x: Option[T], y: Option[T]): Interval[T] =
    (x, y) match
      case (Some(x), Some(y)) => closed(x, y)
      case (Some(x), None)    => leftClosed(x)
      case (None, Some(y))    => rightClosed(y)
      case (None, None)       => unbounded

  /**
   * LeftOpen
   *
   * An interval is left-bounded, if there is a value that is smaller than all its elements.
   *
   * {{{
   *   (a-, +∞) = {x | x > a-}
   * }}}
   */
  def leftOpen[T: Domain](x: T): Interval[T] =
    proper(Endpoint.succ(x), Endpoint.at(Value.InfPos))

  /**
   * LeftClosed
   *
   * An interval is left-bounded, if there is a value that is smaller than all its elements.
   *
   * {{{
   *   [a-, +∞) = {x | x >= a-}
   * }}}
   */
  def leftClosed[T: Domain](x: T): Interval[T] =
    proper(Endpoint.at(x), Endpoint.at(Value.InfPos))

  /**
   * RightOpen
   *
   * An interval is right-bounded, if there is s value that is larger than all its elements.
   *
   * {{{
   *   (-∞, a+) = {x | x < a+}
   * }}}
   */
  def rightOpen[T: Domain](x: T): Interval[T] =
    proper(Endpoint.at(Value.InfNeg), Endpoint.pred(x))

  /**
   * RightClosed
   *
   * An interval is right-bounded, if there is s value that is larger than all its elements.
   *
   * {{{
   *   (-∞, a+] = {x | x < a+}
   * }}}
   */
  def rightClosed[T: Domain](x: T): Interval[T] =
    proper(Endpoint.at(Value.InfNeg), Endpoint.at(x))

  /**
   * LeftClosedRightOpen
   *
   * An interval closed on the left side and open on the right side.
   *
   * {{{
   *   [a-, a+) = {x | a- <= x < a+}
   * }}}
   */
  def leftClosedRightOpen[T: Domain](x: T, y: T): Interval[T] =
    proper(Endpoint.at(x), Endpoint.pred(y))

  /**
   * LeftOpenRightClosed
   *
   * An interval open on the left side and closed on the right side.
   *
   * {{{
   *   (a-, a+] = {x | a- < x <= a+}
   * }}}
   */
  def leftOpenRightClosed[T: Domain](x: T, y: T): Interval[T] =
    proper(Endpoint.succ(x), Endpoint.at(y))

  /**
   * Make an arbitrary interval
   */
  def make[T: Domain](x: Value[T], y: Value[T]): Interval[T] =
    make(Endpoint.at(x), Endpoint.at(y))

  /**
   * Make an arbitrary interval.
   */
  def make[T: Domain](x: Endpoint[T], y: Endpoint[T]): Interval[T] =
    Interval(x, y)
