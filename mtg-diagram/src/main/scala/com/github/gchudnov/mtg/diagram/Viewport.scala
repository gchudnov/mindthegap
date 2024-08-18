package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.internal.Endpoint

/**
  * Viewport that needs to be rendered
  * 
  * Might be either finite or infinite.
  */
sealed trait Viewport[+T]

object Viewport:

  final case class Finite[T: Domain](left: T, right: T) extends Viewport[T]:
    def size: Long =
      summon[Domain[T]].count(left, right)

  case object Infinite extends Viewport[Nothing]

  // TODO: ^^^ convert to enum

  def all[T: Domain]: Viewport[T] =
    make(Interval.unbounded[T])

  def make[T: Domain](left: Option[T], right: Option[T]): Viewport[T] =
    (left, right) match
      case (Some(l), Some(r)) =>
        make(Interval.closed(l, r))
      case (Some(l), None) =>
        make(Interval.leftClosed(l))
      case (None, Some(r)) =>
        make(Interval.rightClosed(r))
      case (None, None) =>
        make(Interval.unbounded[T])

  def make[T: Domain](left: T, right: T): Viewport[T] =
    make(Interval.closed(left, right))

  def make[T: Domain](interval: Interval[T]): Viewport[T] =
    make(intervals = List(interval), hasEmpty = false)

  private[mtg] def make[T: Domain](intervals: List[Interval[T]], hasEmpty: Boolean): Viewport[T] =
    given ordV: Ordering[Value[T]] = summon[Domain[T]].ordValue

    val xs: List[Interval[T]] = if hasEmpty then intervals else intervals.filter(_.nonEmpty)

    val ms = xs.map(_.normalize).flatMap(i => List(i.leftEndpoint, i.rightEndpoint))
    val vs = ms.map(_.unwrap)

    val ps = vs.filter(_.isFinite)

    val (vMin, vMax) = (ps.minOption, ps.maxOption) match
      case xy @ (Some(x), Some(y)) =>
        val cmp = ordV.compare(x, y)
        if cmp == 0 then
          // If points are equal, we extend the interval to the left and to the right.
          (Some(x.pred), Some(y.succ))
        else if cmp < 0 then xy
        else (Some(y), Some(x))
      case xy =>
        xy

    val (tMin, tMax) = (vMin.map(_.get), vMax.map(_.get))

    (tMin, tMax) match
      case (Some(min), Some(max)) =>
        Finite(min, max)
      case _ =>
        Infinite
