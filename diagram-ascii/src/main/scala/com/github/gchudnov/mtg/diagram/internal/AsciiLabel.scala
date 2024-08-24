package com.github.gchudnov.mtg.diagram.internal

/**
 * ASCII Label
 */
private[diagram] final case class AsciiLabel(x: Int, value: String):

  def size: Int =
    value.size

private[diagram] object AsciiLabel:

  lazy val empty: AsciiLabel =
    AsciiLabel(0, "")

  def make(x: Int, text: String): AsciiLabel =
    AsciiLabel(x, text)
