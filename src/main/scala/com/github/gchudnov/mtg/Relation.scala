package com.github.gchudnov.mtg

/**
 * Relations
 *
 * {{{
 *   AAA              | A preceeds B            | (p)
 *        BBB         | B is-predeeded-by A     | (P)
 *
 *   AAA              | A meets B               | (m)
 *      BBB           | B is-met-by A           | (M)
 *
 *   AAA              | A overlaps B            | (o)
 *     BBB            | B is-overlapped-by A    | (O)
 *
 *     AA             | A during B              | (d)
 *   BBBBBB           | B contains A            | (D)
 *
 *   AAA              | A starts B              | (s)
 *   BBBBBB           | B is-started-by A       | (S)
 *
 *      AAA           | A finishes B            | (f)
 *   BBBBBB           | B is-finished-by A      | (F)
 *
 *   AAA              | A equals B              | (e)
 *   BBB              |                         |
 * }}}
 */
object Relation:

  extension [T: Ordering](a: Interval[T])
    /**
     * Preceeds (p), Before (b)
     *
     * IsPreceededBy (P), After (A)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- < b-
     *   a- < b+
     *   a+ < b-
     *   a+ < b+
     * }}}
     *
     * {{{
     *   A preceeds B
     *   A before B
     *   B is-predeeded-by A
     *   B after A
     * }}}
     *
     * {{{
     *   A <p> B
     *   B <P> A
     * }}}
     *
     * {{{
     *   AAA
     *        BBB
     * }}}
     */
    def preceeds(b: Interval[T]): Boolean =
      (a, b) match
        case (Degenerate(x), Degenerate(y)) =>
          summon[Ordering[T]].lt(x, y)
        case (Degenerate(x), Proper(Some(y1), _, includeY1, _)) =>
          summon[Ordering[T]].lt(x, y1) || (summon[Ordering[T]].equiv(x, y1) && !includeY1)
        case (Proper(_, Some(x2), _, includeX2), Degenerate(y)) =>
          summon[Ordering[T]].lt(x2, y) || (summon[Ordering[T]].equiv(x2, y) && !includeX2)
        case (Proper(_, Some(x2), _, includeX2), Proper(Some(y1), _, includeY1, _)) =>
          summon[Ordering[T]].lt(x2, y1) || (summon[Ordering[T]].equiv(x2, y1) && (!includeX2 || !includeY1))
        case _ =>
          false

    def isPreceededBy(b: Interval[T]): Boolean =
      b.preceeds(a)

    def before(b: Interval[T]): Boolean =
      a.preceeds(b)

    def after(b: Interval[T]): Boolean =
      b.preceeds(a)

    /**
     * Meets (m)
     *
     * IsMetBy (M)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- < b-
     *   a- < b+
     *   a+ = b-
     *   a+ < b+
     * }}}
     *
     * {{{
     *   A meets B
     *   A is-met-by B
     * }}}
     *
     * {{{
     *   A <m> B
     *   B <M> A
     * }}}
     *
     * {{{
     *   AAA
     *      BBB
     * }}}
     */
    def meets(b: Interval[T]): Boolean =
      (a, b) match
        case (Degenerate(x), Degenerate(y)) =>
          summon[Ordering[T]].equiv(x, y)
        case (Degenerate(x), Proper(Some(y1), _, includeY1, _)) =>
          summon[Ordering[T]].equiv(x, y1) && includeY1
        case (Proper(_, Some(x2), _, includeX2), Degenerate(y)) =>
          summon[Ordering[T]].equiv(x2, y) && includeX2
        case (Proper(_, Some(x2), _, includeX2), Proper(Some(y1), _, includeY1, _)) =>
          summon[Ordering[T]].equiv(x2, y1) && includeX2 && includeY1
        case _ =>
          false

    def isMetBy(b: Interval[T]): Boolean =
      b.meets(a)

    /**
     * Overlaps (o)
     *
     * IsOverlapedBy (O)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- < b-
     *   a- < b+
     *   a+ > b-
     *   a+ < b+
     * }}}
     *
     * {{{
     *   A overlaps B
     *   B is-overlapped-by A
     * }}}
     *
     * {{{
     *   A <o> B
     *   B <O> A
     * }}}
     *
     * {{{
     *   AAAA
     *     BBBB
     * }}}
     */
    def overlaps(b: Interval[T]): Boolean =
      (a, b) match
        case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
          val ordT = summon[Ordering[T]]
          (ordT.lt(x1, y1) || (ordT.equiv(x1, y1) && includeX1 && !includeY1)) &&
          (ordT.lt(y1, x2)) &&
          (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
        case (Proper(None, Some(x2), _, includeX2), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
          val ordT = summon[Ordering[T]]
          (ordT.lt(y1, x2)) &&
          (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
        case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), None, includeY1, _)) =>
          val ordT = summon[Ordering[T]]
          (ordT.lt(y1, x2)) &&
          (ordT.lt(x1, y1) || (ordT.equiv(x1, y1) && includeX1 && !includeY1))
        case (Proper(None, Some(x2), _, includeX2), Proper(Some(y1), None, includeY1, _)) =>
          val ordT = summon[Ordering[T]]
          ordT.lt(y1, x2)
        case _ =>
          false

    def isOverlapedBy(b: Interval[T]): Boolean =
      b.overlaps(a)

    /**
     * During (d)
     *
     * Contains (D), Includes (I)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- > b-
     *   a- < b+
     *   a+ > b-
     *   a+ < b+
     * }}}
     *
     * {{{
     *   A during B
     *   B contains A
     *   B includes A
     * }}}
     *
     * {{{
     *   A <d> B
     *   B <D> A
     * }}}
     *
     * {{{
     *     AA
     *   BBBBBB
     * }}}
     */
    def during(b: Interval[T]): Boolean =
      (a, b) match
        case (Degenerate(x), Proper(Some(y1), None, includeY1, _)) =>
          val ordT = summon[Ordering[T]]
          ordT.gt(x, y1)
        case (Degenerate(x), Proper(None, Some(y2), _, includeY2)) =>
          val ordT = summon[Ordering[T]]
          ordT.lt(x, y2)
        case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), None, includeY1, _)) =>
          val ordT = summon[Ordering[T]]
          (ordT.lt(y1, x1) || (ordT.equiv(y1, x1) && includeY1 && !includeX1))
        case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(None, Some(y2), _, includeY2)) =>
          val ordT = summon[Ordering[T]]
          (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
        case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
          val ordT = summon[Ordering[T]]
          (ordT.lt(y1, x1) || (ordT.equiv(y1, x1) && includeY1 && !includeX1)) &&
          (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
        case _ =>
          false

    def contains(b: Interval[T]): Boolean =
      b.during(a)

    def includes(b: Interval[T]): Boolean =
      b.during(a)

    /**
     * Starts (s)
     *
     * IsStartedBy (S)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- = b-
     *   a- < b+
     *   a+ > b-
     *   a+ < b+
     * }}}
     *
     * {{{
     *   A starts B
     *   B is-started-by A
     * }}}
     *
     * {{{
     *   A <s> B
     *   B <S> A
     * }}}
     *
     * {{{
     *   AAA
     *   BBBBBB
     * }}}
     */
    def starts(b: Interval[T]): Boolean =
      (a, b) match
        case (Degenerate(x), Proper(Some(y1), _, includeY1, _)) =>
          val ordT = summon[Ordering[T]]
          ordT.equiv(x, y1) && includeY1
        case (Proper(Some(x1), Some(_), includeX1, _), Proper(Some(y1), None, includeY1, _)) =>
          val ordT = summon[Ordering[T]]
          (ordT.equiv(x1, y1) && includeX1 && includeY1)
        case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
          val ordT = summon[Ordering[T]]
          (ordT.equiv(x1, y1) && includeX1 && includeY1) && (ordT.lt(x2, y2) || (ordT.equiv(x2, y2) && !includeX2 && includeY2))
        case _ =>
          false

    def isStartedBy(b: Interval[T]): Boolean =
      b.starts(a)

    /**
     * Finishes (f)
     *
     * IsFinishedBy (F)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- > b-
     *   a- < b+
     *   a+ > b-
     *   a+ = b+
     * }}}
     *
     * {{{
     *   A finishes B
     *   B is-finished-by A
     * }}}
     *
     * {{{
     *   A <f> B
     *   B <F> A
     * }}}
     *
     * {{{
     *      AAA
     *   BBBBBB
     * }}}
     */
    def finishes(b: Interval[T]): Boolean =
      (a, b) match
        case (Degenerate(x), Proper(_, Some(y2), _, includeY2)) =>
          val ordT = summon[Ordering[T]]
          ordT.equiv(x, y2) && includeY2
        case (Proper(Some(_), Some(x2), _, includeX2), Proper(None, Some(y2), _, includeY2)) =>
          val ordT = summon[Ordering[T]]
          (ordT.equiv(x2, y2) && includeX2 && includeY2)
        case (Proper(Some(x1), Some(x2), includeX1, includeX2), Proper(Some(y1), Some(y2), includeY1, includeY2)) =>
          val ordT = summon[Ordering[T]]
          (ordT.equiv(x2, y2) && includeX2 && includeY2) && (ordT.gt(x1, y1) || (ordT.equiv(x1, y1) && !includeX1 && includeY1))
        case _ =>
          false

    def isFinishedBy(b: Interval[T]): Boolean =
      b.finishes(a)

    /**
     * Equals (e)
     *
     * {{{
     *   {a-, a+}; {b-; b+}
     *   a- = b-
     *   a- < b+
     *   a+ > b-
     *   a+ = b+
     * }}}
     *
     * {{{
     *   A equals B
     * }}}
     *
     * {{{
     *   A <e> B
     * }}}
     *
     * {{{
     *   AAAA
     *   BBBB
     * }}}
     */
    def same(b: Interval[T]): Boolean =
      import com.github.gchudnov.mtg.ordering.OptionOrdering      
      (a, b) match
        case (Empty, Empty) =>
          true
        case (Degenerate(x), Degenerate(y)) =>
          val ordT = summon[Ordering[T]]
          ordT.equiv(x, y)
        case (Proper(ox1, ox2, includeX1, includeX2), Proper(oy1, oy2, includeY1, includeY2)) =>
          val ordT = summon[Ordering[Option[T]]]
          (ordT.equiv(ox1, oy1) && (includeX1 == includeY1)) &&
          (ordT.equiv(ox2, oy2) && (includeX2 == includeY2))
        case _ =>
          false

// sealed abstract class Bounded[T: Ordering](a: T, b: T, isIncludeA: Boolean, isIncludeB: Boolean) extends Interval[T]

// final case class Open[T: Ordering](a: T, b: T)                extends Bounded[T](a, b, isIncludeA = false, isIncludeB = false)
// final case class Closed[T: Ordering](a: T, b: T)              extends Bounded[T](a, b, isIncludeA = true, isIncludeB = true)
// final case class LeftClosedRightOpen[T: Ordering](a: T, b: T) extends Bounded[T](a, b, isIncludeA = true, isIncludeB = false)
// final case class LeftOpenRightClosed[T: Ordering](a: T, b: T) extends Bounded[T](a, b, isIncludeA = false, isIncludeB = true)

// sealed abstract class Unbounded[T: Ordering](a: Option[T], b: Option[T], isIncludeA: Boolean, isIncludeB: Boolean) extends Interval[T]

// final case class LeftOpen[T: Ordering](a: T)   extends Unbounded[T](Some(a), None, isIncludeA = false, isIncludeB = false)
// final case class LeftClosed[T: Ordering](a: T) extends Unbounded[T](Some(a), None, isIncludeA = true, isIncludeB = false)

// final case class RightOpen[T: Ordering](b: T)   extends Unbounded[T](None, Some(b), isIncludeA = false, isIncludeB = false)
// final case class RightClosed[T: Ordering](b: T) extends Unbounded[T](None, Some(b), isIncludeA = false, isIncludeB = true)

/*   def isOpen: Boolean =
    !isIncludeA && !isIncludeB

  def isClosed: Boolean =
    isIncludeA && isIncludeB

  def isLeftClosedRightOpen: Boolean =
    isIncludeA && !isIncludeB

  def isLeftOpenRightClosed: Boolean =
    !isIncludeA && isIncludeB
 */

// object Interval:

// val empty: Empty.type =
//   Empty

// def degenerate[T: Ordering](a: T): Degenerate[T] =
//   Degenerate[T](a)

// def open[T: Ordering](a: T, b: T): Open[T] =
//   Open[T](a = a, b = b)

// def closed[T: Ordering](a: T, b: T): Closed[T] =
//   Closed[T](a = a, b = b)

// def leftClosedRightOpen[T: Ordering](a: T, b: T): LeftClosedRightOpen[T] =
//   LeftClosedRightOpen[T](a = a, b = b)

// def leftOpenRightClosed[T: Ordering](a: T, b: T): LeftOpenRightClosed[T] =
//   LeftOpenRightClosed[T](a = a, b = b)

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
