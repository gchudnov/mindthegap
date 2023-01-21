package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object Group:

  private[mtg] final case class GroupState[T: Domain](interval: Interval[T], members: Set[Int])

  private[mtg] object GroupState:
    def empty[T: Domain]: GroupState[T] =
      GroupState(interval = Interval.empty[T], members = Set.empty[Int])

  private final case class AccState[T: Domain](grouped: List[GroupState[T]], current: GroupState[T])

  private object AccState:
    def empty[T: Domain]: AccState[T] =
      AccState(grouped = List.empty[GroupState[T]], current = GroupState.empty[T])

  private def groupIsLess[T: Domain](a: Interval[T], b: Interval[T])(using ordM: Ordering[Mark[T]]): Boolean =
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
   * Group
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
    val xs1 = xs.zipWithIndex.sortWith { case ((a, _), (b, _)) => groupIsLess(a, b) };

    val acc = xs1.foldLeft(AccState.empty) { case (acc, (it, i)) =>
      val cur = acc.current
      if cur.interval.merges(it) then acc.copy(current = cur.copy(interval = cur.interval.union(it), members = cur.members + i))
      else acc.copy(grouped = acc.grouped :+ cur, current = GroupState(interval = it, members = Set(i)))
    }

    val groups = acc.grouped :+ acc.current
    groups
