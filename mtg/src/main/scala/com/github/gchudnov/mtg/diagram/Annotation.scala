package com.github.gchudnov.mtg.diagram

/**
 * Annotation Entry
 */
final case class Annotation(value: String):
  def isEmpty: Boolean =
    value.isEmpty

  def nonEmpty: Boolean =
    value.nonEmpty

object Annotation:

  val empty: Annotation =
    Annotation("")

  def make(value: String): Annotation =
    Annotation(value)
