package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object Group:

  /**
   * A single group
   */
  private[mtg] final case class GroupState[T: Domain](interval: Interval[T], members: Set[Int]):
    def concat(x: Interval[T], i: Int): GroupState[T] =
      GroupState(interval = interval.span(x), members = members + i)

  private[mtg] object GroupState:
    def of[T: Domain](x: Interval[T], i: Int): GroupState[T] =
      GroupState(x, Set(i))

  /**
   * Accumulator
   */
  private final case class AccState[T: Domain](grouped: List[GroupState[T]], current: GroupState[T]):
    def shift(x: Interval[T], i: Int): AccState[T] =
      AccState(grouped = grouped :+ current, current = GroupState.of(x, i))

    def concat(x: Interval[T], i: Int): AccState[T] =
      this.copy(current = current.concat(x, i))

  private object AccState:
    def of[T: Domain](x: Interval[T], i: Int): AccState[T] =
      AccState(grouped = List.empty[GroupState[T]], current = GroupState(x, Set(i)))

  /**
   * Order of Intervals
   */
  private def isLess[T: Domain](a: Interval[T], b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
    ordM.compare(a.left, b.left) match
      case -1 =>
        true
      case 0 =>
        ordM.lt(a.right, b.right)
      case _ =>
        false

  /**
   * Group
   *
   * Groups a series of intervals by executing union for the intersecting intervals.
   *
   * {{{
   * Given:
   *   List(a, b, c, d, e)
   *
   *   [***]                                  | [0,10]  : a
   *    [********************]                | [3,50]  : b
   *            [***]                         | [20,30] : c
   *                             [****]       | [60,70] : d
   *                                  [***]   | [71,80] : e
   *   [*********************]                | [0,50]  : g1
   *                             [********]   | [60,80] : g2
   * --++--+----+---+--------+---+----+---+-- |
   *   0  10   20  30       50  60   70  80   |
   *
   * Returns:
   *    List(g1, g2)
   * }}}
   */
  final def group[T: Domain](xs: Seq[Interval[T]])(using ordM: Ordering[Mark[T]]): List[Interval[T]] =
    val groups    = groupFind(xs)
    val intervals = groups.map(_.interval)

    intervals

  /**
   * GroupFind
   *
   * Groups a series of intervals by executing union for the intersecting intervals and provide the membership information.
   *
   * {{{
   * Given:
   *   List(a, b, c, d, e)
   *
   *   [***]                                  | [0,10]  : a
   *    [********************]                | [3,50]  : b
   *            [***]                         | [20,30] : c
   *                             [****]       | [60,70] : d
   *                                  [***]   | [71,80] : e
   *   [*********************]                | [0,50]  : g1
   *                             [********]   | [60,80] : g2
   * --++--+----+---+--------+---+----+---+-- |
   *   0  10   20  30       50  60   70  80   |
   *
   * Returns:
   *   List((g1, Set(0, 1, 2)), (g2, Set(4, 5)))
   *   where members of a set are the indices of intervals that were grouped (membership information).
   * }}}
   */
  final def groupFind[T: Domain](xs: Seq[Interval[T]])(using ordM: Ordering[Mark[T]]): List[GroupState[T]] =
    val xs1 = xs.zipWithIndex.sortWith { case ((a, _), (b, _)) => isLess[T](a, b) };

    val acc = xs1.foldLeft[Option[AccState[T]]](None) { case (acc, (it, i)) =>
      acc.fold(Some(AccState.of(it, i)))(acc => if acc.current.interval.merges(it) then Some(acc.concat(it, i)) else Some(acc.shift(it, i)))
    }

    acc.fold(List.empty[GroupState[T]])(it => it.grouped :+ it.current)
