package com.github.gchudnov.mtg

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
sealed trait Interval[+T: Ordering]:
  def isEmpty: Boolean
  def isDegenrate: Boolean
  def isProper: Boolean

  def nonEmpty: Boolean =
    !isEmpty

  def nonDegenerate: Boolean =
    !isDegenrate

  def nonProper: Boolean =
    !isProper

/**
 * Empty Interval
 */
case object Empty extends Interval[Nothing]:
  override def toString(): String = "∅"

  override def isEmpty: Boolean     = true
  override def isDegenrate: Boolean = false
  override def isProper: Boolean    = false

/**
 * Degenerate Interval
 */
final case class Degenerate[T: Ordering](a: T) extends Interval[T]:
  override def toString(): String = s"{${a.toString()}}"

  override def isEmpty: Boolean     = false
  override def isDegenrate: Boolean = true
  override def isProper: Boolean    = false

/**
 * Proper Interval
 */
final case class Proper[T: Ordering](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean) extends Interval[T]:

  // make sure that a < b, if provided
  require((for x <- a; y <- b yield summon[Ordering[T]].lt(x, y)).getOrElse(true))

  override def toString(): String =
    import Proper.Tags
    val leftBound  = Tags.leftBound(isIncludeA)
    val rightBound = Tags.rightBound(isIncludeB)
    val leftValue  = Tags.leftValue(a)
    val rightValue = Tags.rightValue(b)
    s"${leftBound}${leftValue},${rightValue}${rightBound}"

  override def isEmpty: Boolean     = false
  override def isDegenrate: Boolean = false
  override def isProper: Boolean    = true

object Proper:

  private object Tags:
    private val infinite = "∞"

    private val leftOpen    = "("
    private val leftClosed  = "["
    private val rightOpen   = ")"
    private val rightClosed = "]"

    def leftBound(isInclude: Boolean): String =
      if isInclude then Tags.leftClosed else Tags.leftOpen

    def rightBound(isInclude: Boolean): String =
      if isInclude then Tags.rightClosed else Tags.rightOpen

    def leftValue(x: Option[?]): String =
      x.fold(s"-${Tags.infinite}")(_.toString())

    def rightValue(x: Option[?]): String =
      x.fold(s"+${Tags.infinite}")(_.toString())

object Interval:

  /**
   * ∅
   */
  def empty[T: Ordering]: Interval[T] =
    Empty

  /**
   * (-∞, +∞)
   */
  def unbounded[T: Ordering]: Interval[T] =
    Proper[T](None, None, isIncludeA = false, isIncludeB = false)

  /**
   * [a, a] = {a}
   *
   * @param a
   *   point
   * @return
   *   a new interval
   */
  def degenerate[T: Ordering](a: T): Interval[T] =
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
  def proper[T: Ordering](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean): Interval[T] =
    Proper(a, b, isIncludeA, isIncludeB)

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
  def open[T: Ordering](a: T, b: T): Interval[T] =
    Proper(Some(a), Some(b), isIncludeA = false, isIncludeB = false)

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
  def closed[T: Ordering](a: T, b: T): Interval[T] =
    Proper(Some(a), Some(b), isIncludeA = true, isIncludeB = true)

  /**
   * (a, +∞) = {x | x > a}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftOpen[T: Ordering](a: T): Interval[T] =
    Proper(Some(a), None, isIncludeA = false, isIncludeB = false)

  /**
   * [a, +∞) = {x | x >= a}
   *
   * @param a
   *   left boundary
   * @return
   *   a new interval
   */
  def leftClosed[T: Ordering](a: T): Interval[T] =
    Proper(Some(a), None, isIncludeA = true, isIncludeB = false)

  /**
   * (-∞, b) = {x | x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightOpen[T: Ordering](b: T): Interval[T] =
    Proper(None, Some(b), isIncludeA = false, isIncludeB = false)

  /**
   * (-∞, b] = {x | x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def rightClosed[T: Ordering](b: T): Interval[T] =
    Proper(None, Some(b), isIncludeA = false, isIncludeB = true)

  /**
   * [a, b) = {x | a <= x < b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftClosedRightOpen[T: Ordering](a: T, b: T): Interval[T] =
    proper(Some(a), Some(b), isIncludeA = true, isIncludeB = false)

  /**
   * (a, b] = {x | a < x <= b}
   *
   * @param a
   *   right boundary
   * @return
   *   a new interval
   */
  def leftOpenRightClosed[T: Ordering](a: T, b: T): Interval[T] =
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
  def make[T: Ordering](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean): Interval[T] =
    val ord = summon[Ordering[T]]
    (a, b) match
      case (Some(x), Some(y)) =>
        if ord.equiv(x, y) && isIncludeA && isIncludeB then degenerate(x)
        else if ord.lt(x, y) then proper(a, b, isIncludeA, isIncludeB)
        else empty
      case _ =>
        proper(a, b, isIncludeA, isIncludeB)
