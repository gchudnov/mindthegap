package com.github.gchudnov.mtg.internal

/**
 * ASCII Annotation
 */
private[mtg] final case class AsciiAnnotation(value: String):
  def isEmpty: Boolean =
    value.isEmpty

  def nonEmpty: Boolean =
    value.nonEmpty

private[mtg] object AsciiAnnotation:

  lazy val empty: AsciiAnnotation =
    AsciiAnnotation("")

  def make(value: String): AsciiAnnotation =
    AsciiAnnotation(value)
