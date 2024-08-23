package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

/**
 * Any interval.
 *
 * @param leftEndpoint
 *   Left endpoint
 * @param rightEndpoint
 *   Right endpoint
 */
private[mtg] final case class AnyInterval[T: Domain](override val leftEndpoint: Endpoint[T], override val rightEndpoint: Endpoint[T])
    extends Interval[T]:

  override def left: Option[T] =
    leftEndpoint.unwrap.opt

  override def right: Option[T] =
    rightEndpoint.unwrap.opt

  override def size: Option[Long] =
    (leftEndpoint.eval, rightEndpoint.eval) match
      case (Value.Finite(x), Value.Finite(y)) =>
        Some(summon[Domain[T]].count(x, y))
      case _ =>
        None

  override def isEmpty: Boolean =
    summon[Domain[T]].ordEndpoint.gt(leftEndpoint, rightEndpoint)

  override def isPoint: Boolean =
    summon[Domain[T]].ordEndpoint.equiv(leftEndpoint, rightEndpoint)

  override def isProper: Boolean =
    summon[Domain[T]].ordEndpoint.lt(leftEndpoint, rightEndpoint)

  override def isLeftOpen: Boolean =
    leftEndpoint.isSucc

  override def isLeftClosed: Boolean =
    leftEndpoint.isAt && leftEndpoint.eval.isFinite

  override def isRightOpen: Boolean =
    rightEndpoint.isPred

  override def isRightClosed: Boolean =
    rightEndpoint.isAt && rightEndpoint.eval.isFinite

  override def nonEmpty: Boolean =
    !isEmpty

  override def nonPoint: Boolean =
    !isPoint

  override def nonProper: Boolean =
    !isProper

  override def swap: Interval[T] =
    AnyInterval(rightEndpoint, leftEndpoint)

  override def inflate: Interval[T] =
    AnyInterval(leftEndpoint.pred, rightEndpoint.succ)

  override def inflateLeft: Interval[T] =
    AnyInterval(leftEndpoint.pred, rightEndpoint)

  override def inflateRight: Interval[T] =
    AnyInterval(leftEndpoint, rightEndpoint.succ)

  override def deflate: Interval[T] =
    AnyInterval(leftEndpoint.succ, rightEndpoint.pred)

  override def deflateLeft: Interval[T] =
    AnyInterval(leftEndpoint.succ, rightEndpoint)

  override def deflateRight: Interval[T] =
    AnyInterval(leftEndpoint, rightEndpoint.pred)

  override def canonical: Interval[T] =
    AnyInterval(leftEndpoint.at, rightEndpoint.at)

  override def normalize: Interval[T] =
    val l = normalizeLeft
    val r = normalizeRight
    if l != leftEndpoint || r != rightEndpoint then AnyInterval(l, r)
    else this

  private def normalizeLeft: Endpoint[T] =
    leftEndpoint match
      case Endpoint.At(_) =>
        leftEndpoint
      case Endpoint.Pred(_) =>
        leftEndpoint.at
      case Endpoint.Succ(xx) =>
        Endpoint.Succ(xx.at)

  private def normalizeRight: Endpoint[T] =
    rightEndpoint match
      case Endpoint.At(_) =>
        rightEndpoint
      case Endpoint.Pred(yy) =>
        Endpoint.Pred(yy.at)
      case Endpoint.Succ(_) =>
        rightEndpoint.at

  override def toString: String =
    Printer.print(this)

  private[mtg] override def toDebugString: String =
    s"AnyInterval(${leftEndpoint.toString}, ${rightEndpoint.toString})"
