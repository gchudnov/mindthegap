package com.github.gchudnov.mtg.diagram

/**
 * Label
 */
final case class Label(pos: Int, value: String):
  def size: Int =
    value.size

object Label:
  val empty: Label =
    Label(0, "")
