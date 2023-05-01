package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.Mark

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
    "left view boundary must be less or equal to the right view boundary"
  )

  val size: Option[Long] =
    for
      lhs <- left
      rhs <- right
      dx   = summon[Domain[T]].count(lhs, rhs)
    yield dx

  def isEmpty: Boolean =
    left.isEmpty && right.isEmpty

  def nonEmpty: Boolean =
    !isEmpty

  def toInterval: Interval[T] =
    Interval.make(left.map(Value.finite(_)).getOrElse(Value.infNeg), right.map(Value.finite(_)).getOrElse(Value.infPos))

object View:

  def default[T: Domain]: View[T] =
    View(
      left = None,
      right = None
    )

  def make[T: Domain](left: Option[T], right: Option[T]): View[T] =
    View(left = left, right = right)

  private[mtg] def make[T: Domain](intervals: List[Interval[T]])(using ordT: Ordering[Value[T]]): View[T] =
    val xs: List[Interval[T]] = intervals.filter(_.nonEmpty) // TODO: If Empty intervals are displayed, we will need to change this condition

    val ps = xs.flatMap(toLeftRightValues).filter(_.isFinite)

    val (vMin, vMax) = (ps.minOption, ps.maxOption) match
      case xy @ (Some(x), Some(y)) =>
        if ordT.equiv(x, y) then (Some(x.pred), Some(y.succ)) else xy
      case xy =>
        xy

    val (p, q) = (vMin.map(_.get), vMax.map(_.get))
    View(left = p, right = q)

  private def toLeftRightValues[T: Domain](i: Interval[T]): List[Value[T]] =
    List(toLeftValue(i.left), toRightValue(i.right))

  private def toLeftValue[T: Domain](left: Mark[T]): Value[T] =
    left match
      case Mark.At(x) =>
        x
      case Mark.Pred(_) =>
        left.eval
      case Mark.Succ(xx) =>
        xx.eval

  private def toRightValue[T: Domain](right: Mark[T]): Value[T] =
    right match
      case Mark.At(y) =>
        y
      case Mark.Pred(yy) =>
        yy.eval
      case Mark.Succ(_) =>
        right.eval
