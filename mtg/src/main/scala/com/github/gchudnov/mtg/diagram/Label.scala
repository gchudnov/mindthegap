package com.github.gchudnov.mtg.diagram

/**
 * Label
 */
final case class Label(posX: Int, value: String):

  def size: Int =
    value.size

object Label:

  lazy val empty: Label =
    Label(0, "")

  def make(x: Int, text: String): Label =
    Label(x, text)
