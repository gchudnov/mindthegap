package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

/**
 * Legend Entry
 */
final case class Legend(value: String):
  def isEmpty: Boolean =
    value.isEmpty

  def nonEmpty: Boolean =
    value.nonEmpty

object Legend:
  lazy val empty: Legend =
    Legend("")

  def make[T: Domain](i: Interval[T]): Legend =
    Legend(i.toString) // TODO: i.asString
