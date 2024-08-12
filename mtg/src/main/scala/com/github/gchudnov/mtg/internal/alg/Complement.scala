package com.github.gchudnov.mtg.internal.alg

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Value

private[mtg] object Complement:

  private[mtg] final case class AccState[T: Domain](is: List[Interval[T]], last: Option[Interval[T]])

  private[mtg] object AccState:
    def empty[T: Domain]: AccState[T] =
      AccState[T](List.empty[Interval[T]], None)

  /**
   * Order of Intervals
   */
  private def isLess[T: Domain](a: Interval[T], b: Interval[T]): Boolean =
    val ordE = summon[Domain[T]].ordEndpoint
    ordE.compare(a.leftEndpoint, b.leftEndpoint) match
      case -1 =>
        true
      case 0 =>
        ordE.lt(a.rightEndpoint, b.rightEndpoint)
      case _ =>
        false

  /**
   * Calculates complement for a given collection of intervals
   */
  final def complement[T: Domain](xs: Iterable[Interval[T]]): List[Interval[T]] =
    val ys = (List(Interval.point[T](Value.infNeg)) ++ xs ++ List(Interval.point[T](Value.infPos))).sortWith(isLess[T])
    val acc = ys.foldLeft(AccState.empty[T]) { case (acc, b) =>
      acc.last match
        case Some(a) =>
          if a.isDisjoint(b) && !a.isAdjacent(b) then acc.copy(is = acc.is ++ List(a.gap(b).canonical), last = Some(b))
          else acc.copy(last = Some(a.span(b)))
        case None =>
          acc.copy(last = Some(b))
    }

    acc.is
