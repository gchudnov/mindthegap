package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.diagram.internal.DiagramMacro
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

/**
 * Diagram
 */
final case class Diagram[T](
  name: String,
  sections: List[Section[T]],
)

/**
 * Diagram Companion Object
 */
object Diagram:

  def make[T: Domain](
    name: String,
    intervals: IterableOnce[IterableOnce[Interval[T]]],
    annotations: IterableOnce[IterableOnce[String]],
    names: IterableOnce[String],
  ): Diagram[T] =
    val sections = intervals.iterator
      .zip(annotations.iterator)
      .zip(names.iterator)
      .map { case ((is, as), n) =>
        val sectionIntervals = is.iterator.to(List)

        val inAnnotations = as.iterator.to(List)
        val sectionAnnotations =
          if inAnnotations.isEmpty then DiagramMacro.varNames(sectionIntervals)
          else inAnnotations

        Section(n, sectionIntervals, sectionAnnotations)
      }
      .toList

    Diagram(name, sections)
