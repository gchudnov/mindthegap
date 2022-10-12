package com.github.gchudnov.mtg

// import com.github.gchudnov.mtg.internal.IntervalRel

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

enum Interval[T]:
  case Empty extends Interval[Nothing]
  case Degenerate(a: T)
  case Proper(l: Boundary.Left[T], r: Boundary.Right[T])

  def isEmpty: Boolean =
    this.ordinal == 0

  def isDegenrate: Boolean =
    this.ordinal == 1

  def isProper: Boolean =
    this.ordinal == 2

  def nonEmpty: Boolean =
    !isEmpty

  def nonDegenerate: Boolean =
    !isDegenrate

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
      case Interval.Degenerate(_) =>
        true
      case _ =>
        false

  def left: Boundary.Left[T] =
    this match
      case Interval.Empty =>
        throw new NoSuchElementException("Empty.left")
      case Interval.Degenerate(a) =>
        Boundary.Left(Some(a), true)
      case Interval.Proper(l, _) =>
        l

  def right: Boundary.Right[T] =
    this match
      case Interval.Empty =>
        throw new NoSuchElementException("Empty.right")
      case Interval.Degenerate(a) =>
        Boundary.Right(Some(a), true)
      case Interval.Proper(_, r) =>
        r

object Interval:

  /**
   * ∅
   */
  def empty[T]: Interval[Nothing] =
    Interval.Empty

  /**
   * [a, a] = {a}
   *
   * @param a
   *   point
   * @return
   *   a new interval
   */
  def degenerate[T](a: T): Interval[T] =
    Interval.Degenerate(a)

  /**
   * {a, b}
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
  def proper[T: Domain](a: Option[T], isIncludeA: Boolean, b: Option[T], isIncludeB: Boolean)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    proper(Boundary.Left(a, isIncludeA), Boundary.Right(b, isIncludeB))

  /**
   * {a, b}
   *
   * @param ba
   *   left boundary
   * @param bb
   *   right boundary
   * @return
   *   a new interval
   */
  def proper[T: Domain](ba: Boundary.Left[T], bb: Boundary.Right[T])(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    import Show.given
    require(bOrd.lt(ba, bb), s"${ba.show},${bb.show}: left boundary must be less than the right boundary")
    Proper(ba, bb)

  /**
   * (-∞, +∞)
   */
  def unbounded[T: Domain](using bOrd: Ordering[Boundary[T]]): Interval[T] =
    proper[T](Boundary.Left(None, false), Boundary.Right(None, false))

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
    proper(Boundary.Left(Some(a), false), Boundary.Right(Some(b), false))

  // /**
  //  * [a, b] = {x | a <= x <= b}
  //  *
  //  * @param a
  //  *   left boundary
  //  * @param b
  //  *   right boundary
  //  * @return
  //  *   a new interval
  //  */
  // def closed[T: Domain](a: T, b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   Proper(LeftBoundary(Some(a), true), RightBoundary(Some(b), true))

  // /**
  //  * (a, +∞) = {x | x > a}
  //  *
  //  * @param a
  //  *   left boundary
  //  * @return
  //  *   a new interval
  //  */
  // def leftOpen[T: Domain](a: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   Proper(LeftBoundary(Some(a), false), RightBoundary[T](None, false))

  // /**
  //  * [a, +∞) = {x | x >= a}
  //  *
  //  * @param a
  //  *   left boundary
  //  * @return
  //  *   a new interval
  //  */
  // def leftClosed[T: Domain](a: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   Proper(LeftBoundary(Some(a), true), RightBoundary[T](None, false))

  // /**
  //  * (-∞, b) = {x | x < b}
  //  *
  //  * @param a
  //  *   right boundary
  //  * @return
  //  *   a new interval
  //  */
  // def rightOpen[T: Domain](b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   Proper(LeftBoundary[T](None, false), RightBoundary(Some(b), false))

  // /**
  //  * (-∞, b] = {x | x < b}
  //  *
  //  * @param a
  //  *   right boundary
  //  * @return
  //  *   a new interval
  //  */
  // def rightClosed[T: Domain](b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   Proper(LeftBoundary[T](None, false), RightBoundary(Some(b), true))

  // /**
  //  * [a, b) = {x | a <= x < b}
  //  *
  //  * @param a
  //  *   right boundary
  //  * @return
  //  *   a new interval
  //  */
  // def leftClosedRightOpen[T: Domain](a: T, b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   proper(Some(a), Some(b), isIncludeA = true, isIncludeB = false)

  // /**
  //  * (a, b] = {x | a < x <= b}
  //  *
  //  * @param a
  //  *   right boundary
  //  * @return
  //  *   a new interval
  //  */
  // def leftOpenRightClosed[T: Domain](a: T, b: T)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   proper(Some(a), Some(b), isIncludeA = false, isIncludeB = true)

  // /**
  //  * Make an arbitraty interval
  //  *
  //  * @param a
  //  *   left boundary
  //  * @param b
  //  *   right boundary
  //  * @param isIncludeA
  //  *   whether to include left boundary in the interval or not
  //  * @param isIncludeB
  //  *   whether to include right boundary in the interval or not
  //  * @return
  //  */
  // def make[T: Domain](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean)(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   val ba = LeftBoundary(a, isIncludeA)
  //   val bb = RightBoundary(b, isIncludeB)
  //   make(ba, bb)

  // def make[T: Domain](ba: LeftBoundary[T], bb: RightBoundary[T])(using bOrd: Ordering[Boundary[T]]): Interval[T] =
  //   (ba.value, bb.value) match
  //     case (Some(x), Some(y)) =>
  //       if bOrd.equiv(ba, bb) then degenerate(x)
  //       else if bOrd.lt(bb, ba) then empty[T]
  //       else proper(ba.value, bb.value, ba.isInclude, bb.isInclude)
  //     case _ =>
  //       proper(ba.value, bb.value, ba.isInclude, bb.isInclude)

//     // import com.github.gchudnov.mtg.Show.*

//     // // TODO: make the constructor private and extract the require ???

//     // // The endpoint relation left (a-) < right (a+) is required for a proper interval
//     // // require(bOrd.lt(left, right), s"${left.show},${right.show}: left boundary must be less than the right boundary")
