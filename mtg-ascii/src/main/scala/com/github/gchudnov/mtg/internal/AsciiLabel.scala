package com.github.gchudnov.mtg.internal

/**
 * ASCII Label
 */
private[mtg] final case class AsciiLabel(x: Int, value: String):

  def size: Int =
    value.size

private[mtg] object AsciiLabel:

  lazy val empty: AsciiLabel =
    AsciiLabel(0, "")

  def make(x: Int, text: String): AsciiLabel =
    AsciiLabel(x, text)
