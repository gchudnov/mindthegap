package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.diagram.Renderer
import com.github.gchudnov.mtg.diagram.Translator
import com.github.gchudnov.mtg.diagram.Span
import com.github.gchudnov.mtg.diagram.Theme
import com.github.gchudnov.mtg.diagram.Tick
import com.github.gchudnov.mtg.diagram.Label
import com.github.gchudnov.mtg.diagram.Legend
import com.github.gchudnov.mtg.diagram.Annotation
import com.github.gchudnov.mtg.diagram.internal.BasicRenderer
import com.github.gchudnov.mtg.diagram.internal.BasicTranslator

import Show.given
import com.github.gchudnov.mtg.internal.DiagramMacro

/**
 * Diagram
 */
final case class Diagram(
  width: Int,
  height: Int,
  spans: List[Diagram.Span],
  ticks: List[Diagram.Tick],
  labels: List[Diagram.Label],
  legends: List[Diagram.Legend],
  annotations: List[Diagram.Annotation]
)

object Diagram:

  export com.github.gchudnov.mtg.diagram.Span
  export com.github.gchudnov.mtg.diagram.Theme
  export com.github.gchudnov.mtg.diagram.View
  export com.github.gchudnov.mtg.diagram.Canvas
  export com.github.gchudnov.mtg.diagram.Tick
  export com.github.gchudnov.mtg.diagram.Label
  export com.github.gchudnov.mtg.diagram.Legend
  export com.github.gchudnov.mtg.diagram.Annotation

  lazy val empty: Diagram =
    Diagram(
      width = 0,
      height = 0,
      spans = List.empty[Span],
      ticks = List.empty[Tick],
      labels = List.empty[Label],
      legends = List.empty[Legend],
      annotations = List.empty[Annotation]
    )

  /**
   * Make a Diagram that can be rendered
   */
  def make[T: Domain](intervals: List[Interval[T]], view: View[T], canvas: Canvas, annotations: List[String])(using Ordering[Mark[T]]): Diagram =
    val effectiveView = if view.isEmpty then View.make(intervals) else view
    val translator    = Translator.make(effectiveView, canvas)

    // if view is specified, provide labels and ticks to mark the boundaries of the view
    val (viewTicks, viewLabels) = if view.nonEmpty then
      val vi = view.toInterval
      val vs = translator.translate(vi)
      (Span.toTicks(vs), canvas.labels(vi, vs))
    else (List.empty[Tick], List.empty[Label])

    val d = intervals.zipWithIndex.foldLeft(Diagram.empty) { case (acc, (i, j)) =>
      val y = acc.height

      val span   = translator.translate(i)
      val ticks  = Span.toTicks(span)
      val labels = canvas.labels(i, span)
      val legend = Legend.make(i)
      val ann    = if j < annotations.size then Annotation(annotations(j)) else Annotation.empty

      acc.copy(
        width = canvas.width,
        height = y + 1,
        spans = acc.spans :+ span,
        ticks = acc.ticks ++ ticks,
        labels = acc.labels ++ labels,
        legends = acc.legends :+ legend,
        annotations = acc.annotations :+ ann
      )
    }

    d.copy(
      ticks = (d.ticks ++ viewTicks).distinct.sortBy(_.pos),
      labels = (d.labels ++ viewLabels).distinct.sortBy(_.pos)
    )

  inline def make[T: Domain](inline intervals: List[Interval[T]], view: View[T], canvas: Canvas)(using Ordering[Mark[T]]): Diagram =
    val annotations = DiagramMacro.varNames(intervals)
    make(intervals, view = view, canvas = canvas, annotations = annotations)

  inline def make[T: Domain](inline intervals: List[Interval[T]], view: View[T])(using Ordering[Mark[T]]): Diagram =
    val annotations = DiagramMacro.varNames(intervals)
    make(intervals, view = view, canvas = Canvas.default, annotations = annotations)

  inline def make[T: Domain](inline intervals: List[Interval[T]])(using Ordering[Mark[T]]): Diagram =
    val annotations = DiagramMacro.varNames(intervals)
    make(intervals, view = View.default[T], canvas = Canvas.default, annotations = annotations)

  inline def make[T: Domain](inline intervals: List[Interval[T]], canvas: Canvas)(using Ordering[Mark[T]]): Diagram =
    val annotations = DiagramMacro.varNames(intervals)
    make(intervals, view = View.default[T], canvas = canvas, annotations = annotations)

  def make[T: Domain](intervals: List[Interval[T]], annotations: List[String])(using Ordering[Mark[T]]): Diagram =
    make(intervals, view = View.default[T], canvas = Canvas.default, annotations = annotations)

  /**
   * Render the Diagram
   */
  def render(d: Diagram, theme: Theme = Theme.default)(using r: Renderer): List[String] =
    r.render(d, theme)

  // TODO: add render methods so that make is redundant
