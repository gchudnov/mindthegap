package com.github.gchudnov.mtg

/**
 * Generic Interval Representation
 *
 * Classification of Intervals:
 *   - Empty
 *   - Degenerate
 *   - Proper and Bounded
 *     - Open
 *     - Closed
 *     - LeftClosedRightOpen
 *     - LeftOpenRightClosed
 *   - LeftBounded and RightUnbounded
 */
sealed trait Interval[T: Ordering]

/**
 * Proper
 *
 * An interval that is neither Empty nor Degenerate is said to be Proper.
 *
 * Marker Trait
 */
sealed trait Proper

/**
 * LeftBounded
 * 
 * An interval is left-bounded, 
 * if there is a value that is smaller than all its elements.
 */
sealed trait LeftBounded

/**
 * RightBounded
 * 
 * An interval is right-bounded, 
 * if there is s value that is larger than all its elements.
 */
sealed trait RightBounded

/**
 * Bounded
 *
 * An interval is Bounded, if it is both Left- and Right-bounded;
 * and is said to be Unbounded otherwise.
 *
 * Intervals that are Bounded at only one end are said to be Half-Bounded.
 * Bounded intervals are also commonly known as finite intervals.
 * 
 * Marker Trait
 */
sealed trait Bounded extends LeftBounded with RightBounded

/**
  * LeftUnbounded
  * 
  * (+inf, ...
  */
sealed trait LeftUnbounded

/**
  * RightUnbounded
  * 
  * ..., +inf)
  */
sealed trait RightUnbounded

/**
 * HalfOpen
 *
 * includes only one of its endpoints, e.g. (0, 1]. [0, 1).
 *
 * Marker Trait
 */
sealed trait HalfOpen

/**
 * Empty:
 * {{{
 *   [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
 * }}}
 */
object Empty extends Interval[Nothing]

/**
 * Degenerate
 *
 * Sonsists of a single real number
 *
 * {{{
 *   [a, a] = {a}
 * }}}
 */
final case class Degenerate[T: Ordering](a: T) extends Interval[T]

/**
 * Open - does not include its endpoints, and is indicated with parentheses, e.g. (0, 1).
 *
 * Proper and Bounded
 *
 * {{{
 *   (a, b) = {x | a < x < b}
 * }}}
 */
final case class Open[T: Ordering](a: T, b: T) extends Interval[T] with Proper with Bounded

/**
 * Closed - an interval which includes all its limit points, e.g. [0, 1].
 *
 * Proper and Bounded
 *
 * {{{
 *   [a, b] = {x | a <= x <= b}
 * }}}
 */
final case class Closed[T: Ordering](a: T, b: T) extends Interval[T] with Proper with Bounded

/**
 * LeftClosedRightOpen
 *
 * Proper and Bounded
 *
 * {{{
 *   [a, b) = {x | a <= x < b}
 * }}}
 */
final case class LeftClosedRightOpen[T: Ordering](a: T, b: T) extends Interval[T] with Proper with Bounded with HalfOpen

/**
 * LeftOpenRightClosed
 *
 * Proper and Bounded
 *
 * {{{
 *   (a, b] = {x | a < x <= b}
 * }}}
 */
final case class LeftOpenRightClosed[T: Ordering](a: T, b: T) extends Interval[T] with Proper with Bounded with HalfOpen

/**
  * LeftOpen
  * 
  * LeftBounded and RightUnbounded
  * 
  * {{{
  *   (a, +∞) = {x | x > a}
  * }}}
  */
final case class LeftOpen[T: Ordering](a: T) extends Interval[T] with LeftBounded with RightUnbounded

/**
  * LeftClosed
  * 
  * LeftBounded and RightUnbounded
  * 
  * {{{
  *   [a, +∞) = {x | x >= a}
  * }}}
  */
final case class LeftClosed[T: Ordering](a: T) extends Interval[T] with LeftBounded with RightUnbounded



/**
  * Unbounded
  * 
  * An interval without bounds on the left and right side
  */
sealed trait Unbounded