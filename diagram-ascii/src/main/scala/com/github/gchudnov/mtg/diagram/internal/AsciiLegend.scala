package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Interval

/**
 * ASCII Legend Entry
 */
private[diagram] final case class AsciiLegend(value: String):
  def isEmpty: Boolean =
    value.isEmpty

  def nonEmpty: Boolean =
    value.nonEmpty

private[diagram] object AsciiLegend:
  lazy val empty: AsciiLegend =
    AsciiLegend("")

  def make[T](i: Interval[T]): AsciiLegend =
    AsciiLegend(i.toString)
