package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Show.given
import org.scalatest.matchers.must.Matchers.*
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval

trait IntervalRelAssert:
  import IntervalRelAssert.*

  // private def relFnMap[T](using bOrd: Ordering[Mark[T]]) =
  //   Map(
  //     Rel.Before        -> ((xx: Interval[T], yy: Interval[T]) => xx.before(yy)),
  //     Rel.After         -> ((xx: Interval[T], yy: Interval[T]) => xx.after(yy)),
  //     Rel.Meets         -> ((xx: Interval[T], yy: Interval[T]) => xx.meets(yy)),
  //     Rel.IsMetBy       -> ((xx: Interval[T], yy: Interval[T]) => xx.isMetBy(yy)),
  //     Rel.Overlaps      -> ((xx: Interval[T], yy: Interval[T]) => xx.overlaps(yy)),
  //     Rel.IsOverlapedBy -> ((xx: Interval[T], yy: Interval[T]) => xx.isOverlapedBy(yy)),
  //     Rel.During        -> ((xx: Interval[T], yy: Interval[T]) => xx.during(yy)),
  //     Rel.Contains      -> ((xx: Interval[T], yy: Interval[T]) => xx.contains(yy)),
  //     Rel.Starts        -> ((xx: Interval[T], yy: Interval[T]) => xx.starts(yy)),
  //     Rel.IsStartedBy   -> ((xx: Interval[T], yy: Interval[T]) => xx.isStartedBy(yy)),
  //     Rel.Finishes      -> ((xx: Interval[T], yy: Interval[T]) => xx.finishes(yy)),
  //     Rel.IsFinishedBy  -> ((xx: Interval[T], yy: Interval[T]) => xx.isFinishedBy(yy)),
  //     Rel.EqualsTo      -> ((xx: Interval[T], yy: Interval[T]) => xx.equalsTo(yy))
  //   )

  // private val invRels: Map[Rel, Rel] = Map(
  //   // forward
  //   Rel.Before   -> Rel.After,
  //   Rel.Meets    -> Rel.IsMetBy,
  //   Rel.Overlaps -> Rel.IsOverlapedBy,
  //   Rel.During   -> Rel.Contains,
  //   Rel.Starts   -> Rel.IsStartedBy,
  //   Rel.Finishes -> Rel.IsFinishedBy,
  //   // backward
  //   Rel.After         -> Rel.Before,
  //   Rel.IsMetBy       -> Rel.Meets,
  //   Rel.IsOverlapedBy -> Rel.Overlaps,
  //   Rel.Contains      -> Rel.During,
  //   Rel.IsStartedBy   -> Rel.Starts,
  //   Rel.IsFinishedBy  -> Rel.Finishes,
  //   // dual
  //   Rel.EqualsTo -> Rel.EqualsTo
  // )

  // /**
  //  * Finds relations two itervals are satisfying
  //  */
  // def findRelations[T](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Mark[T]]): Set[Rel] =
  //   relFnMap[T].foldLeft(Set.empty[Rel]) { case (acc, (k, fn)) =>
  //     val res = fn(xx, yy)
  //     if res then acc + k
  //     else acc
  //   }

  // def assertAny[T](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Mark[T]]): Unit =
  //   val trues      = findRelations(xx, yy)
  //   val isNonEmpty = !(xx.isEmpty || yy.isEmpty)
  //   if isNonEmpty && trues.size != 1 then
  //     fail(s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| satisfies ${trues.size} relations: ${trues.mkString("[", ",", "]")}, expected only one relation")

  // def assertOneOf[T](rs: Set[Rel])(xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Mark[T]]): Unit =
  //   val trues = findRelations(xx, yy)
  //   if trues.size != 1 || !rs.contains(trues.head) then
  //     fail(
  //       s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| should satisfy one of ${rs.mkString("[", ",", "]")} relations, however it satisfies ${trues.mkString("[", ",", "]")} instead"
  //     )

  // def assertOne[T: Ordering: Domain](r: Rel)(xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Mark[T]]): Unit =
  //   val relations = relFnMap[T]

  //   val fk = r
  //   val bk = invRels(r)
  //   val ks = List(fk, bk)

  //   val fwdFn  = relations(fk)
  //   val bckFn  = relations(bk)
  //   val restFn = relations.filterNot { case (k, _) => ks.contains(k) }

  //   fwdFn(xx, yy) mustBe (true)
  //   bckFn(yy, xx) mustBe (true)

  //   restFn.foreach { case (k, fn) =>
  //     if fn(xx, yy) then fail(s"xx: ${xx}, yy: ${yy}; given that ${fk}|${xx.show}, ${yy.show}| == true; expected ${k}|${xx.show}, ${yy.show}| to be false, got true")
  //     if fn(yy, xx) then fail(s"xx: ${xx}, yy: ${yy}; given that ${fk}|${xx.show}, ${yy.show}| == true; expected ${k}|${yy.show}, ${xx.show}| to be false, got true")
  //   }

object IntervalRelAssert extends IntervalRelAssert:

  enum Rel(name: String):
    case Before        extends Rel("b")
    case After         extends Rel("B")
    case Meets         extends Rel("m")
    case IsMetBy       extends Rel("M")
    case Overlaps      extends Rel("o")
    case IsOverlapedBy extends Rel("O")
    case During        extends Rel("d")
    case Contains      extends Rel("D")
    case Starts        extends Rel("s")
    case IsStartedBy   extends Rel("S")
    case Finishes      extends Rel("f")
    case IsFinishedBy  extends Rel("F")
    case EqualsTo      extends Rel("e")
