package com.github.gchudnov.mtg

/**
 * Generic Interval Representation
 *
 * Classification of Intervals:
 *   - Empty
 *   - Degenerate
 *   - Proper and Bounded
 *      - Open
 *      - Closed
 *      - TODO: define
 */
sealed trait Interval[T: Ordering]

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
 * Proper and Bounded
 *
 * Proper - an interval that is neither Empty nor Degenerate is said to be Proper.
 *
 * Bounded - an interval is said to be Bounded, if it is both left- and right-bounded; and is said to be Unbounded otherwise.
 */

/**
 * Open - does not include its endpoints, and is indicated with parentheses, e.g. (0, 1).
 *
 * Proper and Bounded
 *
 * {{{
 *   (a, b) = {x | a < x < b}
 * }}}
 */
final case class Open[T: Ordering](a: T, b: T) extends Interval[T]

/**
 * Closed - an interval which includes all its limit points, e.g. [0, 1].
 *
 * Proper and Bounded
 *
 * {{{
 *   [a, b] = {x | a <= x <= b}
 * }}}
 */
final case class Closed[T: Ordering](a: T, b: T) extends Interval[T]

/**
 * HalfOpen - includes only one of its endpoints, e.g. (0, 1]. [0, 1).
 */
