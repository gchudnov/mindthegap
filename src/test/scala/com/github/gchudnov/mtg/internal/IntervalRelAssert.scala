package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Show.*
import org.scalatest.matchers.must.Matchers.*
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval

trait IntervalRelAssert:
  import IntervalRelAssert.*

  private def relFnMap[T: Domain](using bOrd: Ordering[Boundary[T]]) =
    Map(
      Rel.Before        -> ((xx: Interval[T], yy: Interval[T]) => xx.before(yy)),
      Rel.After         -> ((xx: Interval[T], yy: Interval[T]) => xx.after(yy)),
      Rel.Meets         -> ((xx: Interval[T], yy: Interval[T]) => xx.meets(yy)),
      Rel.IsMetBy       -> ((xx: Interval[T], yy: Interval[T]) => xx.isMetBy(yy)),
      Rel.Overlaps      -> ((xx: Interval[T], yy: Interval[T]) => xx.overlaps(yy)),
      Rel.IsOverlapedBy -> ((xx: Interval[T], yy: Interval[T]) => xx.isOverlapedBy(yy)),
      Rel.During        -> ((xx: Interval[T], yy: Interval[T]) => xx.during(yy)),
      Rel.Contains      -> ((xx: Interval[T], yy: Interval[T]) => xx.contains(yy)),
      Rel.Starts        -> ((xx: Interval[T], yy: Interval[T]) => xx.starts(yy)),
      Rel.IsStartedBy   -> ((xx: Interval[T], yy: Interval[T]) => xx.isStartedBy(yy)),
      Rel.Finishes      -> ((xx: Interval[T], yy: Interval[T]) => xx.finishes(yy)),
      Rel.IsFinishedBy  -> ((xx: Interval[T], yy: Interval[T]) => xx.isFinishedBy(yy)),
      Rel.EqualsTo      -> ((xx: Interval[T], yy: Interval[T]) => xx.equalsTo(yy))
    )

//   /**
//    * Finds name of the relations two itervals are satisfying
//    */
//   def findSatisfyRelations[T: Domain](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Set[String] =
//     val relations = relFnMap[T]
//     val satisfied = relations.foldLeft(Set.empty[String]) { case (acc, (k, fn)) =>
//       val res = fn(xx, yy)
//       if res then acc + k
//       else acc
//     }
//     satisfied

//   def assertOneOf[T: Ordering: Domain](rs: Set[String], xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
//     val trues = findSatisfyRelations(xx, yy) - "E" // "E" is a duplicate of "e"

//     if trues.size != 1 || !rs.contains(trues.head) then
//       fail(
//         s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| should satisfy one of ${rs.mkString("[", ",", "]")} relations, however it satisfies ${trues.mkString("[", ",", "]")} instead"
//       )

//   def assertFwdBck[T: Ordering: Domain](r: String, xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
//     val relations = relFnMap[T]

//     val fk = r
//     val bk = r.map(_.toUpper)
//     val ks = List(fk, bk)

//     val fwd  = relations(fk)
//     val bck  = relations(bk)
//     val rest = relations.filterNot { case (k, _) => ks.contains(k) }

//     fwd(xx, yy) mustBe (true)
//     bck(yy, xx) mustBe (true)

//     rest.foreach { case (k, fn) =>
//       if fn(xx, yy) then fail(s"xx: ${xx}, yy: ${yy}: ${fk}|${xx.show}, ${yy.show}| == true; ${k}|${xx.show}, ${yy.show}| mustBe false, got true")
//       if fn(yy, xx) then fail(s"xx: ${xx}, yy: ${yy}: ${fk}|${xx.show}, ${yy.show}| == true; ${k}|${yy.show}, ${xx.show}| mustBe false, got true")
//     }

//   def assertAnySingle[T: Ordering: Domain](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
//     val trues = findSatisfyRelations(xx, yy) - "E" // "E" is a duplicate of "e"

//     val isNonEmpty = !(xx.isEmpty || yy.isEmpty)
//     if isNonEmpty && trues.size != 1 then
//       fail(s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| satisfies ${trues.size} relations: ${trues.mkString("[", ",", "]")}, expected only one relation")

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
