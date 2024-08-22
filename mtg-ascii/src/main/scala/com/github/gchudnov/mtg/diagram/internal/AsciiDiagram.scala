package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.diagram.Diagram
import com.github.gchudnov.mtg.diagram.Renderer
import com.github.gchudnov.mtg.internal.Printer

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.diagram.Viewport

/**
 * ASCII Diagram
 */
private[diagram] final case class AsciiDiagram(
  title: String,
  now: Option[Int],
  width: Int,
  spans: List[AsciiSpan],
  ticks: List[AsciiTick],
  labels: List[AsciiLabel],
  legends: List[AsciiLegend],
  annotations: List[AsciiAnnotation],
)

private[diagram] object AsciiDiagram:

  /**
   * Make an AsciiDiagram from the given Diagram
   *
   * @param d
   *   the input diagram
   * @return
   *   the AsciiDiagram
   */
  def from[T: Domain](d: Diagram[T], canvas: AsciiCanvas): AsciiDiagram =
    val intervals = d.sections.flatMap(_.intervals)
    val viewport  = Viewport.make(intervals, false)
    from(d, viewport, canvas)

  /**
   * Make an AsciiDiagram from the given Diagram and Viewport
   *
   * @param d
   *   the input diagram
   * @param viewport
   *   the viewport
   * @return
   *   the AsciiDiagram
   */
  def from[T: Domain](d: Diagram[T], viewport: Viewport[T], canvas: AsciiCanvas): AsciiDiagram =
    val translator              = AsciiTranslator.make(viewport, canvas)
    val (viewTicks, viewLabels) = makeTicksLabels(viewport, canvas, translator)

    // AsciiDiagram
    ???

  /*
  // /**
  //  * Make a Diagram that can be rendered
  //  */
  // def make[T: Domain](intervals: List[Interval[T]], view: View[T], canvas: AsciiCanvas, annotations: List[String]): Diagram =
  //   val effectiveView           = makeEffectiveView(intervals, view)
  //   val translator              = Translator.make(effectiveView, canvas)
  //   val (viewTicks, viewLabels) = makeTicksLabels(view, canvas, translator)

  //   val d = intervals.zipWithIndex.foldLeft(Diagram.empty) { case (acc, (i, j)) =>
  //     val y = acc.height

  //     val span   = toSpan(translator, i)
  //     val ticks  = toTicks(span)
  //     val labels = toLabels(canvas, i, span)
  //     val legend = Legend.make(i)
  //     val ann    = if j < annotations.size then Annotation(annotations(j)) else Annotation.empty

  //     acc.copy(
  //       width = canvas.width,
  //       height = y + 1,
  //       spans = acc.spans :+ span,
  //       ticks = acc.ticks ++ ticks,
  //       labels = acc.labels ++ labels,
  //       legends = acc.legends :+ legend,
  //       annotations = acc.annotations :+ ann,
  //     )
  //   }

  //   d.copy(
  //     ticks = (d.ticks ++ viewTicks).distinct.sortBy(_.x),
  //     labels = (d.labels ++ viewLabels).distinct.sortBy(_.x),
  //   )

  // inline def make[T: Domain](inline intervals: List[Interval[T]], view: View[T], canvas: AsciiCanvas): Diagram =
  //   val annotations = DiagramMacro.varNames(intervals)
  //   make(intervals, view = view, canvas = canvas, annotations = annotations)

  // inline def make[T: Domain](inline intervals: List[Interval[T]], view: View[T]): Diagram =
  //   val annotations = DiagramMacro.varNames(intervals)
  //   make(intervals, view = view, canvas = Canvas.default, annotations = annotations)

  // inline def make[T: Domain](inline intervals: List[Interval[T]]): Diagram =
  //   val annotations = DiagramMacro.varNames(intervals)
  //   make(intervals, view = Viewport.all[T], canvas = Canvas.default, annotations = annotations)

  // inline def make[T: Domain](inline intervals: List[Interval[T]], canvas: AsciiCanvas): Diagram =
  //   val annotations = DiagramMacro.varNames(intervals)
  //   make(intervals, view = Viewport.all[T], canvas = canvas, annotations = annotations)

  // def make[T: Domain](intervals: List[Interval[T]], annotations: List[String]): Diagram =
  //   make(intervals, view = Viewport.all[T], canvas = Canvas.default, annotations = annotations)

   */

  private def makeTicksLabels[T: Domain](view: Viewport[T], canvas: AsciiCanvas, translator: AsciiTranslator[T]): (List[AsciiTick], List[AsciiLabel]) =
    view match
      case Viewport.Finite(x, y) =>
        val vi     = Interval.closed[T](x, y)
        val vs     = toSpan(translator, vi)
        val ticks  = toTicks(vs)
        val labels = toLabels(canvas, vi, vs)
        (ticks, labels)
      case Viewport.Infinite =>
        (List.empty[AsciiTick], List.empty[AsciiLabel])

  private def toSpan[T: Domain](t: AsciiTranslator[T], i: Interval[T]): AsciiSpan =
    if i.isEmpty then AsciiSpan.empty
    else if i.isPoint then
      val x = t.translate(i.normalize.leftEndpoint.eval)
      AsciiSpan(
        x0 = x,
        x1 = x,
        includeX0 = true,
        includeX1 = true,
      )
    else
      val i1        = i.normalize
      val x0        = t.translate(i1.leftEndpoint.unwrap)
      val x1        = t.translate(i1.rightEndpoint.unwrap)
      val includeX0 = isLeftInclusive(i1.leftEndpoint)
      val includeX1 = isRightInclusive(i1.rightEndpoint)
      AsciiSpan(
        x0 = x0,
        x1 = x1,
        includeX0 = includeX0,
        includeX1 = includeX1,
      )

  private def isLeftInclusive[T](left: Endpoint[T]): Boolean =
    left match
      case Endpoint.At(x) =>
        x.isFinite
      case Endpoint.Succ(_) =>
        false
      case xx @ Endpoint.Pred(_) =>
        sys.error("unexpected value of a normalized interval: " + xx)

  private def isRightInclusive[T](right: Endpoint[T]): Boolean =
    right match
      case Endpoint.At(y) =>
        y.isFinite
      case yy @ Endpoint.Succ(_) =>
        sys.error("unexpected value of a normalized interval: " + yy)
      case Endpoint.Pred(_) =>
        false

  /**
   * Convert the given span to a list of ticks.
   */
  private def toTicks(s: AsciiSpan): List[AsciiTick] =
    if s.isEmpty then List.empty[AsciiTick]
    else List(AsciiTick(s.x0), AsciiTick(s.x1))

  /**
   * Make Labels
   */
  private def toLabels[T: Domain](c: AsciiCanvas, i: Interval[T], span: AsciiSpan): List[AsciiLabel] =
    val xs =
      if i.isEmpty then List.empty[AsciiLabel]
      else if i.isPoint then List(AsciiLabel.make(span.x0, Printer.printPoint(i.leftEndpoint)))
      else
        val i1 = i.normalize
        List(
          AsciiLabel.make(span.x0, Printer.printLeft(i1.leftEndpoint)),
          AsciiLabel.make(span.x1, Printer.printRight(i1.rightEndpoint)),
        )

    val ys = xs.map(x => positionLabelOnCanvas(x, c))
    ys

  /**
   * Position the label on the canvas
   *
   * Centers label relative to the given x position.
   */
  private def positionLabelOnCanvas(l: AsciiLabel, c: AsciiCanvas): AsciiLabel =
    val p = AsciiCanvas.align(l.x.toDouble - (l.value.size.toDouble / 2.0))
    val q = p + l.value.size

    val x1 =
      if p < 0 && c.contains(l.x) then 0
      else if q >= c.width && c.contains(l.x) then c.width - l.value.size
      else p

    l.copy(x = x1)
