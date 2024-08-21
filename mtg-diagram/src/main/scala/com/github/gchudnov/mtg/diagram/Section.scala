package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

/**
 * Section
 */
final case class Section[T](
  title: String,
  intervals: List[Interval[T]],
  annotations: List[String],
):
  def withTitle(newTitle: String): Section[T] =
    this.copy(title = newTitle)

  def addInterval(interval: Interval[T]): Section[T] =
    this.copy(intervals = intervals :+ interval)

  def addInterval(start: T, end: T)(using D: Domain[T]): Section[T] =
    addInterval(start, end, "")

  def addInterval(start: T, end: T, ann: String)(using D: Domain[T]): Section[T] =
    this.copy(
      intervals = intervals :+ Interval.closed(start, end),
      annotations = annotations :+ (if ann.isEmpty then annotations.size.toString() else ann),
    )

/**
 * Section Companion Object
 */
object Section:

  /**
   * An empty section.
   */
  def empty[T]: Section[T] =
    Section("", List.empty[Interval[T]], List.empty[String])
