package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Interval

/**
 * ASCII Legend Entry
 */
private[mtg] final case class AsciiLegend(value: String):
  def isEmpty: Boolean =
    value.isEmpty

  def nonEmpty: Boolean =
    value.nonEmpty

private[mtg] object AsciiLegend:
  lazy val empty: AsciiLegend =
    AsciiLegend("")

  def make[T](i: Interval[T]): AsciiLegend =
    AsciiLegend(i.toString)
