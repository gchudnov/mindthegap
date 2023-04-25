package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object Split:

  enum Side:
    case Left
    case Right

  private case class Point[T](pt: Mark[T], i: Int, s: Side)

  /**
   * A single split
   */
  private[mtg] final case class SplitState[T: Domain](interval: Interval[T], members: Set[Int])

  /**
   * Accumulator
   */
  private final case class AccState[T: Domain](splits: List[SplitState[T]], opens: Set[Int], last: Point[T]):

    def withPoint(p: Point[T])(using ordM: Ordering[Mark[T]]): AccState[T] =
      val it = Interval.make[T](last.pt, p.pt)
      val ms = opens
      val s  = SplitState(it, ms)
      val ss = if isLess(last, p) then splits ++ List(s) else splits

      val os = if opens.contains(p.i) then opens - p.i else opens + p.i
      val ls = p

      this.copy(splits = ss, opens = os, last = ls)

  private object AccState:
    def init[T: Domain](p: Point[T]): AccState[T] =
      AccState(splits = List.empty[SplitState[T]], opens = Set(p.i), last = p)

  private def isLess(x: Side, y: Side): Boolean =
    (x, y) match
      case (Side.Left, Side.Right) =>
        true
      case (_, _) =>
        false

  private def isLess[T: Domain](x: Point[T], y: Point[T])(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.compare(x.pt, y.pt) match
      case 0 =>
        isLess(x.s, y.s)
      case 1 =>
        false
      case -1 =>
        true

  /**
   * Split
   *
   * Split intervals into a collection of non-overlapping intervals (splits).
   */
  final def split[T: Domain](xs: Seq[Interval[T]])(using ordM: Ordering[Mark[T]]): List[Interval[T]] =
    val splits    = splitFind(xs)
    val intervals = splits.map(_.interval)

    intervals

  /**
   * SplitFind
   *
   * Split intervals into a collection of non-overlapping intervals (splits) and provide membership information.
   */
  final def splitFind[T: Domain](xs: Seq[Interval[T]])(using ordM: Ordering[Mark[T]]): List[SplitState[T]] =
    val ms = xs.zipWithIndex.flatMap(it => List(Point(it._1.left, it._2, Side.Left), Point(it._1.right, it._2, Side.Right))).sortWith(isLess);

    val acc = ms.foldLeft[Option[AccState[T]]](None) { case (acc, p) =>
      acc.fold(Some(AccState.init(p)))(acc => Some(acc.withPoint(p)))
    }

    acc.fold(List.empty[SplitState[T]])(it => it.splits)
