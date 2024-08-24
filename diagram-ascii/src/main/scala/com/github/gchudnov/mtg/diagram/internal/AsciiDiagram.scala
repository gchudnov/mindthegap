package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.diagram.AsciiCanvas
import com.github.gchudnov.mtg.diagram.Diagram
import com.github.gchudnov.mtg.diagram.Renderer
import com.github.gchudnov.mtg.diagram.Viewport
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.internal.Printer

/**
 * ASCII Diagram
 */
private[diagram] final case class AsciiDiagram(
  title: String,
  now: Option[Int],
  width: Int,
  height: Int,
  spans: List[AsciiSpan],
  ticks: List[AsciiTick],
  labels: List[AsciiLabel],
  legends: List[AsciiLegend],
  annotations: List[AsciiAnnotation],
)

private[diagram] object AsciiDiagram:

  lazy val empty: AsciiDiagram =
    AsciiDiagram(
      title = "",
      now = None,
      width = 0,
      height = 0,
      spans = List.empty[AsciiSpan],
      ticks = List.empty[AsciiTick],
      labels = List.empty[AsciiLabel],
      legends = List.empty[AsciiLegend],
      annotations = List.empty[AsciiAnnotation],
    )

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
  def make[T: Domain](d: Diagram[T], viewport: Viewport[T], canvas: AsciiCanvas): AsciiDiagram =
    val intervals               = d.sections.flatMap(_.intervals) // TODO: sections are not supported at the moment
    val annotations             = d.sections.flatMap(_.annotations)
    val translator              = AsciiTranslator.make(viewport, canvas)
    val (viewTicks, viewLabels) = makeTicksLabels(viewport, canvas, translator)

    val ad = intervals.zipWithIndex.foldLeft(AsciiDiagram.empty) { case (acc, (i, j)) =>
      val y = acc.height

      val span   = toSpan(translator, i)
      val ticks  = toTicks(span)
      val labels = toLabels(canvas, i, span)
      val legend = AsciiLegend.make(i)
      val ann    = if j < annotations.size then AsciiAnnotation(annotations(j)) else AsciiAnnotation.empty

      acc.copy(
        width = canvas.width,
        height = y + 1,
        spans = acc.spans :+ span,
        ticks = acc.ticks ++ ticks,
        labels = acc.labels ++ labels,
        legends = acc.legends :+ legend,
        annotations = acc.annotations :+ ann,
      )
    }

    ad.copy(
      ticks = (ad.ticks ++ viewTicks).distinct.sortBy(_.x),
      labels = (ad.labels ++ viewLabels).distinct.sortBy(_.x),
    )

  private def makeTicksLabels[T: Domain](
    view: Viewport[T],
    canvas: AsciiCanvas,
    translator: AsciiTranslator[T],
  ): (List[AsciiTick], List[AsciiLabel]) =
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
      else if i.isPoint then List(AsciiLabel.make(span.x0, Printer.str(i.leftEndpoint.unwrap)))
      else
        val i1 = i.normalize
        List(
          AsciiLabel.make(span.x0, Printer.str(i1.leftEndpoint.unwrap)),
          AsciiLabel.make(span.x1, Printer.str(i1.rightEndpoint.unwrap)),
        )

    val ys = xs.map(x => positionLabelOnCanvas(x, c))
    ys

  /**
   * Position the label on the canvas
   *
   * Centers label relative to the given x position.
   */
  private def positionLabelOnCanvas(l: AsciiLabel, c: AsciiCanvas): AsciiLabel =
    val p = com.github.gchudnov.mtg.diagram.AsciiCanvas.align(l.x.toDouble - (l.value.size.toDouble / 2.0))
    val q = p + l.value.size

    val x1 =
      if p < 0 && c.contains(l.x) then 0
      else if q >= c.width && c.contains(l.x) then c.width - l.value.size
      else p

    l.copy(x = x1)
