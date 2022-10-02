package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Show.*
import org.scalatest.matchers.must.Matchers.*
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval

trait IntervalRelAssert:

  private def makeIntervalRelationsCheckMap[T: Domain](using bOrd: Ordering[Boundary[T]]) =
    Map(
      "b" -> ((xx: Interval[T], yy: Interval[T]) => xx.before(yy)),
      "B" -> ((xx: Interval[T], yy: Interval[T]) => xx.after(yy)),
      "m" -> ((xx: Interval[T], yy: Interval[T]) => xx.meets(yy)),
      "M" -> ((xx: Interval[T], yy: Interval[T]) => xx.isMetBy(yy)),
      "o" -> ((xx: Interval[T], yy: Interval[T]) => xx.overlaps(yy)),
      "O" -> ((xx: Interval[T], yy: Interval[T]) => xx.isOverlapedBy(yy)),
      "d" -> ((xx: Interval[T], yy: Interval[T]) => xx.during(yy)),
      "D" -> ((xx: Interval[T], yy: Interval[T]) => xx.contains(yy)),
      "s" -> ((xx: Interval[T], yy: Interval[T]) => xx.starts(yy)),
      "S" -> ((xx: Interval[T], yy: Interval[T]) => xx.isStartedBy(yy)),
      "f" -> ((xx: Interval[T], yy: Interval[T]) => xx.finishes(yy)),
      "F" -> ((xx: Interval[T], yy: Interval[T]) => xx.isFinishedBy(yy)),
      "e" -> ((xx: Interval[T], yy: Interval[T]) => xx.equalsTo(yy)),
      "E" -> ((xx: Interval[T], yy: Interval[T]) => xx.equalsTo(yy))
    )

  /**
   * Finds name of the relations two itervals are satisfying
   */
  def findSatisfyRelations[T: Domain](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Set[String] =
    val relations = makeIntervalRelationsCheckMap[T]
    val satisfied = relations.foldLeft(Set.empty[String]) { case (acc, (k, fn)) =>
      val res = fn(xx, yy)
      if res then acc + k
      else acc
    }
    satisfied

  def assertOneOf[T: Ordering: Domain](rs: Set[String], xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
    val trues = findSatisfyRelations(xx, yy) - "E" // "E" is a duplicate of "e"

    if trues.size != 1 || !rs.contains(trues.head) then
      fail(
        s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| should satisfy one of ${rs.mkString("[", ",", "]")} relations, however it satisfies ${trues.mkString("[", ",", "]")} instead"
      )

  def assertFwdBck[T: Ordering: Domain](r: String, xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
    val relations = makeIntervalRelationsCheckMap[T]

    val fk = r
    val bk = r.map(_.toUpper)
    val ks = List(fk, bk)

    val fwd  = relations(fk)
    val bck  = relations(bk)
    val rest = relations.filterNot { case (k, _) => ks.contains(k) }

    fwd(xx, yy) mustBe (true)
    bck(yy, xx) mustBe (true)

    rest.foreach { case (k, fn) =>
      if fn(xx, yy) then fail(s"xx: ${xx}, yy: ${yy}: ${fk}|${xx.show}, ${yy.show}| == true; ${k}|${xx.show}, ${yy.show}| mustBe false, got true")
      if fn(yy, xx) then fail(s"xx: ${xx}, yy: ${yy}: ${fk}|${xx.show}, ${yy.show}| == true; ${k}|${yy.show}, ${xx.show}| mustBe false, got true")
    }

  def assertAnySingle[T: Ordering: Domain](xx: Interval[T], yy: Interval[T])(using bOrd: Ordering[Boundary[T]]): Unit =
    val trues = findSatisfyRelations(xx, yy) - "E" // "E" is a duplicate of "e"

    val isNonEmpty = !(xx.isEmpty || yy.isEmpty)
    if isNonEmpty && trues.size != 1 then
      fail(s"xx: ${xx}, yy: ${yy}: |${xx.show}, ${yy.show}| satisfies ${trues.size} relations: ${trues.mkString("[", ",", "]")}, expected only one relation")

object IntervalRelAssert extends IntervalRelAssert
