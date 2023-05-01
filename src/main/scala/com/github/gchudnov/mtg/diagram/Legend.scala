package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Show

/**
 * Legend Entry
 */
final case class Legend(value: String):
  def isEmpty: Boolean =
    value.isEmpty

  def nonEmpty: Boolean =
    value.nonEmpty

object Legend:
  val empty: Legend =
    Legend("")

  def make[T: Domain](i: Interval[T])(using Ordering[Mark[T]]): Legend =
    Legend(Show.asString(i))
