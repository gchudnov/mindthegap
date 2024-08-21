package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.diagram.internal.DiagramMacro
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

/**
 * Diagram
 */
final case class Diagram[T](
  title: String,
  sections: List[Section[T]],
):
  def withTitle(newTitle: String): Diagram[T] =
    this.copy(title = newTitle)

  def withSection(f: Section[T] => Section[T]): Diagram[T] =
    this.copy(sections = sections :+ f(Section.empty[T]))

// TODO: add functions to adjust name, sections, etc. ^^^
// TODO: do it

/**
 * Diagram Companion Object
 */
object Diagram:

  /**
   * An empty diagram.
   */
  def empty[T: Domain]: Diagram[T] =
    Diagram("", List.empty[Section[T]])

  /**
   * Make a Diagram with the given intervals and annotations.
   *
   * @param name
   *   the name of the diagram
   * @param intervals
   *   the intervals
   * @param annotations
   *   the annotations
   * @param sectionNames
   *   the names of the sections
   * @return
   */
  def make[T: Domain](
    name: String,
    intervals: IterableOnce[IterableOnce[Interval[T]]],
    annotations: IterableOnce[IterableOnce[String]],
    sectionNames: IterableOnce[String],
  ): Diagram[T] =
    val sections =
      intervals.iterator
        .zip(annotations.iterator)
        .zip(sectionNames.iterator)
        .map { case ((is, as), n) =>
          val sectionIntervals   = is.iterator.to(List)
          val sectionAnnotations = as.iterator.to(List)
          val sectionName        = n

          Section(sectionName, sectionIntervals, sectionAnnotations)
        }
        .toList

    Diagram(name, sections)

  /**
   * Make a Diagram with one section the given intervals and annotations.
   *
   * @param name
   *   the name of the diagram
   * @param intervals
   *   the intervals
   * @param annotations
   *   the annotations
   */
  def make[T: Domain](
    name: String,
    intervals: IterableOnce[Interval[T]],
    annotations: IterableOnce[String],
  ): Diagram[T] =
    val effectiveIntervals   = intervals.iterator.to(List)
    val effectiveAnnotations = annotations.iterator.to(List)
    val section              = Section("", effectiveIntervals, effectiveAnnotations)

    Diagram(name, List(section))

  /**
   * Make a Diagram with one section the given intervals The annotations are inferred from the variable names of the intervals.
   *
   * @param name
   *   the name of the diagram
   * @param intervals
   *   the intervals
   */
  inline def make[T: Domain](
    name: String,
    inline intervals: IterableOnce[Interval[T]],
  ): Diagram[T] =
    val effectiveIntervals   = intervals.iterator.to(List)
    val effectiveAnnotations = DiagramMacro.varNames(intervals)
    val section              = Section("", effectiveIntervals, effectiveAnnotations)

    Diagram(name, List(section))
