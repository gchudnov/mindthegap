package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.Mark

// TODO: create a View trait
// TODO: create RangeView case class, where all boundaries are finite
// TODO: create InfiniteView case class

/**
 * View
 *
 * Specifies the range of domain values to display.
 */
final case class View[T: Domain](
  left: Option[T], // left boundary of the view
  right: Option[T] // right boundary of the view
):
  require(
    left.flatMap(lhs => right.map(rhs => summon[Ordering[T]].lteq(lhs, rhs))).getOrElse(true),
    "left View boundary must be less or equal to the right View boundary"
  )

  val size: Option[Long] =
    for
      lhs <- left
      rhs <- right
      dx   = summon[Domain[T]].count(lhs, rhs)
    yield dx

  /**
   * Check if the view is set to display all values.
   */
  def isAll: Boolean =
    left.isEmpty && right.isEmpty

  /**
   * Check if the view is limited to a range of values.
   */
  def isLimited: Boolean =
    !isAll

  def toInterval: Interval[T] =
    Interval.make(left.map(Value.finite(_)).getOrElse(Value.infNeg), right.map(Value.finite(_)).getOrElse(Value.infPos))

object View:

  def all[T: Domain]: View[T] =
    View(
      left = None,
      right = None
    )

  // TODO: make from an Interval ???

  def make[T: Domain](left: Option[T], right: Option[T]): View[T] =
    View(left = left, right = right)

  def make[T: Domain](left: T, right: T): View[T] =
    make(left = Some(left), right = Some(right))

  private[mtg] def make[T: Domain](intervals: List[Interval[T]]): View[T] =
    val ordM = summon[Domain[T]].ordMark // TODO: do we need this?
    given ordV: Ordering[Value[T]] = summon[Domain[T]].ordValue

    val xs: List[Interval[T]] = intervals.filter(_.nonEmpty) // TODO: If Empty intervals are displayed, we will need to change this condition

    val ms = xs.map(_.normalize).flatMap(i => List(i.left, i.right))
    val vs = ms.map(_.innerValue)

    val ps = vs.filter(_.isFinite)

    val (vMin, vMax) = (ps.minOption, ps.maxOption) match
      case xy @ (Some(x), Some(y)) =>
        // if a point, extend the interval
        if summon[Domain[T]].ordValue.equiv(x, y) then (Some(x.pred), Some(y.succ)) else xy
      case xy =>
        xy

    val (p, q) = (vMin.map(_.get), vMax.map(_.get))

    println((p, q))

    View(left = p, right = q)
