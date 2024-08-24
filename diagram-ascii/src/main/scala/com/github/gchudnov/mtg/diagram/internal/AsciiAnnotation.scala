package com.github.gchudnov.mtg.diagram.internal

/**
 * ASCII Annotation
 */
private[diagram] final case class AsciiAnnotation(value: String):
  def isEmpty: Boolean =
    value.isEmpty

  def nonEmpty: Boolean =
    value.nonEmpty

private[diagram] object AsciiAnnotation:

  lazy val empty: AsciiAnnotation =
    AsciiAnnotation("")

  def make(value: String): AsciiAnnotation =
    AsciiAnnotation(value)
