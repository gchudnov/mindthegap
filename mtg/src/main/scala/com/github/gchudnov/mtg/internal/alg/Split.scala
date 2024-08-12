package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Endpoint
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import scala.collection.mutable.ListBuffer

private[mtg] object Split:

  private enum Side:
    case Left
    case Right

  /**
   * A Split Point
   *
   * @param pt
   *   interval endpoint
   * @param i
   *   interval index in the input sequence
   * @param s
   *   side of the interval
   */
  private case class SplitPoint[T](pt: Endpoint[T], i: Int, s: Side)

  /**
   * A single split
   */
  private[mtg] final case class SingleSplit[T: Domain](interval: Interval[T], members: Set[Int])

  /**
   * Accumulator
   */
  private final case class AccState[T: Domain](splits: ListBuffer[SingleSplit[T]], open: Set[Int], last: SplitPoint[T]):

    def append(p: SplitPoint[T]): AccState[T] =
      val tMinus = if last.s == Side.Left then last.pt else last.pt.succ
      val tPlus  = if p.s == Side.Left then p.pt.pred else p.pt

      val splits1 =
        if summon[Domain[T]].ordEndpoint.lteq(tMinus, tPlus) then splits :+ SingleSplit(Interval.make(tMinus, tPlus), open) else splits

      val open1 = if p.s == Side.Left then open + p.i else open - p.i
      val last1 = p

      this.copy(splits = splits1, open = open1, last = last1)

  private object AccState:
    def init[T: Domain](p: SplitPoint[T]): AccState[T] =
      AccState(splits = ListBuffer.empty[SingleSplit[T]], open = Set(p.i), last = p)

  private def isLess(x: Side, y: Side): Boolean =
    (x, y) match
      case (Side.Left, Side.Right) =>
        true
      case (_, _) =>
        false

  private def isLess[T: Domain](x: SplitPoint[T], y: SplitPoint[T]): Boolean =
    summon[Domain[T]].ordEndpoint.compare(x.pt, y.pt) match
      case 0 =>
        isLess(x.s, y.s)
      case 1 =>
        false
      case -1 =>
        true

  /**
   * Converts an interval and an index to two points
   */
  private def toPoints[T](x: Interval[T], i: Int): List[SplitPoint[T]] =
    List(SplitPoint(x.left, i, Side.Left), SplitPoint(x.right, i, Side.Right))

  /**
   * Split
   *
   * Split intervals into a collection of non-overlapping intervals (splits).
   */
  final def split[T: Domain](xs: Iterable[Interval[T]]): List[Interval[T]] =
    val splits    = splitFind(xs)
    val intervals = splits.map(_.interval)

    intervals

  /**
   * SplitFind
   *
   * Split intervals into a collection of non-overlapping intervals (splits) and provide membership information.
   */
  final def splitFind[T: Domain](xs: Iterable[Interval[T]]): List[SingleSplit[T]] =
    val ms = xs.zipWithIndex.toList.flatMap((x, i) => toPoints(x, i)).sortWith(isLess)

    val acc = ms.foldLeft[Option[AccState[T]]](None) { case (acc, p) =>
      acc.fold(Some(AccState.init(p)))(acc => Some(acc.append(p)))
    }

    acc.fold(List.empty[SingleSplit[T]])(it => it.splits.toList)
