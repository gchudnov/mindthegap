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
sealed trait Interval[+T: Ordering]

case object Empty extends Interval[Nothing]

case class Degenerate[+T: Ordering](a: T) extends Interval[T]

final case class Bounded[+T: Ordering](
  a: T,
  b: T,
  isIncludeA: Boolean,
  isIncludeB: Boolean
) extends Interval[T]:

  def isOpen: Boolean =
    !isIncludeA && !isIncludeB

  def isClosed: Boolean =
    isIncludeA && isIncludeB

  def isLeftClosedRightOpen: Boolean =
    isIncludeA && !isIncludeB

  def isLeftOpenRightClosed: Boolean =
    !isIncludeA && isIncludeB

final case class Unbounded[+T: Ordering](
  a: Option[T],
  b: Option[T],
  isIncludeA: Boolean,
  isIncludeB: Boolean
) extends Interval[T]

/*
 *   - LeftBounded and RightUnbounded
 *     - LeftOpen                       | (a, +∞) = {x | x > a}
 *     - LeftClosed                     | [a, +∞) = {x | x >= a}
 *   - LeftUnbounded and RightBounded
 *     - RightOpen                      | (-∞, b) = {x | x < b}
 *     - RightClosed                    | (-∞, b] = {x | x < b}
 *   - Unbounded                        | (-∞, +∞) = R
*/


object Interval:

  val empty: Empty.type =
    Empty

  def degenerate[T: Ordering](a: T): Degenerate[T] =
    Degenerate[T](a)

  def open[T: Ordering](a: T, b: T): Bounded[T] =
    Bounded[T](
      a = a,
      b = b,
      isIncludeA = false,
      isIncludeB = false
    )

  def closed[T: Ordering](a: T, b: T): Bounded[T] =
    Bounded[T](
      a = a,
      b = b,
      isIncludeA = true,
      isIncludeB = true
    )

  def leftClosedRightOpen[T: Ordering](a: T, b: T): Bounded[T] =
    Bounded[T](
      a = a,
      b = b,
      isIncludeA = true,
      isIncludeB = false
    )

  def leftOpenRightClosed[T: Ordering](a: T, b: T): Bounded[T] =
    Bounded[T](
      a = a,
      b = b,
      isIncludeA = false,
      isIncludeB = true
    )

/*
import java.time.Instant
import scala.math.Ordering.Implicits._
case class TsTzRange(
  start: Option[Instant],
  end: Option[Instant],
  startInclusive: Boolean = true,
  endInclusive: Boolean = true) {

  // if start and end are specified, start must be less than or equal to end
  require((for { s <- start; e <- end} yield { s <= e}).getOrElse(true))

  def zero: Boolean = this.bounded && this.start == this.end && !this.startInclusive || !this.endInclusive

  def infinite: Boolean = this.start.isEmpty && this.end.isEmpty

  // unbounded in this context refers to an interval that is only unbounded on one side
  def unbounded: Boolean = leftUnbounded || rightUnbounded

  def leftUnbounded: Boolean = this.start.isEmpty && this.end.nonEmpty

  def rightUnbounded: Boolean = this.start.nonEmpty && this.end.isEmpty

  def bounded: Boolean = this.start.nonEmpty && this.end.nonEmpty

  private def touchingEdges(left: TsTzRange, right: TsTzRange): Boolean =
    left.end == right.start && left.endInclusive && right.startInclusive

  // Checks if the end of bounded2 lies anywhere within bounded1
  private def boundedCheckLeftOverlap(bounded1: TsTzRange, bounded2: TsTzRange): Boolean =
    bounded2.end > bounded1.start && bounded2.end <= bounded1.end

  // Checks if the start of bounded2 lies anywhere within bounded1
  private def boundedCheckRightOverlap(bounded1: TsTzRange, bounded2: TsTzRange): Boolean =
    bounded2.start >= bounded1.start && bounded2.start < bounded1.end

  // boundedCheck assumes both intervals are bounded
  private def boundedCheck(bounded1: TsTzRange, bounded2: TsTzRange): Boolean =
    boundedCheckLeftOverlap(bounded1, bounded2) ||
      boundedCheckRightOverlap(bounded1, bounded2) ||
      touchingEdges(bounded2, bounded1) ||
      touchingEdges(bounded1, bounded2)

  // checks if both ranges are unbounded in opposite directions, and check overlap if so
  private def oppositeUnboundedCheck(leftUnbounded: TsTzRange, rightUnbounded: TsTzRange): Boolean =
    leftUnbounded.leftUnbounded &&
      rightUnbounded.rightUnbounded &&
      (leftUnbounded.end > rightUnbounded.start || touchingEdges(leftUnbounded, rightUnbounded))

  // checks if both ranges are unbounded in the same direction
  private def sameUnboundedCheck(unbounded1: TsTzRange, unbounded2: TsTzRange): Boolean =
    (unbounded1.leftUnbounded && unbounded2.leftUnbounded) ||
      (unbounded1.rightUnbounded && unbounded2.rightUnbounded)

  private def doubleUnboundedCheck(unbounded1: TsTzRange, unbounded2: TsTzRange): Boolean =
    sameUnboundedCheck(unbounded1, unbounded2) ||
      oppositeUnboundedCheck(unbounded1, unbounded2) ||
      oppositeUnboundedCheck(unbounded2, unbounded1)

  private def singleUnboundedLeftUnboundedCheck(bounded: TsTzRange, leftUnbounded: TsTzRange): Boolean =
    leftUnbounded.leftUnbounded && leftUnbounded.end > bounded.start

  private def singleUnboundedRightUnboundedCheck(bounded: TsTzRange, rightUnbounded: TsTzRange): Boolean =
    rightUnbounded.rightUnbounded && rightUnbounded.start < bounded.end

  // if only one of the intervals is bounded
  private def singleUnboundedCheck(bounded: TsTzRange, unbounded: TsTzRange): Boolean =
    singleUnboundedLeftUnboundedCheck(bounded, unbounded) ||
      singleUnboundedRightUnboundedCheck(bounded, unbounded) ||
      touchingEdges(bounded, unbounded) ||
      touchingEdges(unbounded, bounded)

  def overlaps(other: TsTzRange): Boolean = {
    // if any of the intervals are zero, return false early
    !(this.zero || other.zero) &&
      // if any of the intervals are infinite, return true early
      (this.infinite || other.infinite) ||
      // otherwise, depending on the boundedness of the 2 intervals, check accordingly
      (this.unbounded && other.unbounded && doubleUnboundedCheck(this, other)) ||
      (!this.unbounded && !other.unbounded && boundedCheck(this, other)) ||
      (!this.unbounded && other.unbounded && singleUnboundedCheck(this, other)) ||
      (this.unbounded && !other.unbounded && singleUnboundedCheck(other, this))
  }

  def contains(other: Instant): Boolean = {
    overlaps(TsTzRange(Some(other), Some(other)))
  }

  // returns true if it overlaps with the current time
  def isCurrent: Boolean = {
    this.contains(Instant.now)
  }

}
 */
