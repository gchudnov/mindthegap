package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.diagram.Renderer
import com.github.gchudnov.mtg.diagram.Translator
import com.github.gchudnov.mtg.diagram.Span
import com.github.gchudnov.mtg.diagram.Theme
import com.github.gchudnov.mtg.diagram.Tick
import com.github.gchudnov.mtg.diagram.Label
import com.github.gchudnov.mtg.diagram.Legend
import com.github.gchudnov.mtg.diagram.Annotation

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
  def make[T: Domain](intervals: List[Interval[T]], view: View[T], canvas: Canvas, annotations: List[String]): Diagram =
    val effectiveView           = makeEffectiveView(intervals, view)
    val translator              = Translator.make(effectiveView, canvas)
    val (viewTicks, viewLabels) = makeTicksLabels(view, canvas, translator)

    val d = intervals.zipWithIndex.foldLeft(Diagram.empty) { case (acc, (i, j)) =>
      val y = acc.height

      val span   = toSpan(translator, i)
      val ticks  = toTicks(span)
      val labels = toLabels(canvas, i, span)
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
      ticks = (d.ticks ++ viewTicks).distinct.sortBy(_.x),
      labels = (d.labels ++ viewLabels).distinct.sortBy(_.x)
    )

  inline def make[T: Domain](inline intervals: List[Interval[T]], view: View[T], canvas: Canvas): Diagram =
    val annotations = DiagramMacro.varNames(intervals)
    make(intervals, view = view, canvas = canvas, annotations = annotations)

  inline def make[T: Domain](inline intervals: List[Interval[T]], view: View[T]): Diagram =
    val annotations = DiagramMacro.varNames(intervals)
    make(intervals, view = view, canvas = Canvas.default, annotations = annotations)

  inline def make[T: Domain](inline intervals: List[Interval[T]]): Diagram =
    val annotations = DiagramMacro.varNames(intervals)
    make(intervals, view = View.all[T], canvas = Canvas.default, annotations = annotations)

  inline def make[T: Domain](inline intervals: List[Interval[T]], canvas: Canvas): Diagram =
    val annotations = DiagramMacro.varNames(intervals)
    make(intervals, view = View.all[T], canvas = canvas, annotations = annotations)

  def make[T: Domain](intervals: List[Interval[T]], annotations: List[String]): Diagram =
    make(intervals, view = View.all[T], canvas = Canvas.default, annotations = annotations)

  /**
   * Render the Diagram
   */
  def render(d: Diagram, theme: Theme = Theme.default)(using r: Renderer): List[String] =
    r.render(d, theme)

  // TODO: add render methods so that make is redundant

  // TODO: implement it

  private def toLabels[T: Domain](c: Canvas, i: Interval[T], span: Span): List[Label] =
    val xs =
      if i.isEmpty then List.empty[Label]
      else if i.isPoint then List(Label.make(span.x0, Show.str(i.left.eval)))
      else
        val i1 = i.normalize
        List(
          Label.make(span.x0, Show.str(i1.left.innerValue)),
          Label.make(span.x1, Show.str(i1.right.innerValue))
        )

    val ys = xs.map(x => positionLabelOnCanvas(x, c))
    ys

  private def positionLabelOnCanvas(l: Label, c: Canvas): Label =
    val p = Canvas.align(l.x.toDouble - (l.value.size.toDouble / 2.0))
    val q = p + l.value.size

    val x1 =
      if p < 0 && c.contains(l.x) then 0
      else if q >= c.width && c.contains(l.x) then c.width - l.value.size
      else p

    l.copy(x = x1)

  /**
   * Convert the given span to a list of ticks.
   */
  private def toTicks(s: Span): List[Tick] =
    if s.isEmpty then List.empty[Tick]
    else List(Tick(s.x0), Tick(s.x1))

  private def toSpan[T: Domain](t: Translator[T], i: Interval[T]): Span =
    if i.isEmpty then Span.empty
    else if i.isPoint then
      val p = t.translate(i.normalize.left.eval)
      Span(
        x0 = p,
        x1 = p,
        includeX0 = true,
        includeX1 = true
      )
    else
      val i1 = i.normalize
      Span.make(
        x0 = t.translate(i1.left.innerValue),
        x1 = t.translate(i1.right.innerValue),
        includeX0 = isLeftInclusive(i1.left),
        includeX1 = isRightInclusive(i1.right)
      )

  private def isLeftInclusive[T](left: Mark[T]): Boolean =
    left match
      case Mark.At(x) =>
        x.isFinite
      case Mark.Succ(_) =>
        false
      case xx @ Mark.Pred(_) =>
        sys.error("unexpected value of a normalized interval: " + xx)

  private def isRightInclusive[T](right: Mark[T]): Boolean =
    right match
      case Mark.At(y) =>
        y.isFinite
      case yy @ Mark.Succ(_) =>
        sys.error("unexpected value of a normalized interval: " + yy)
      case Mark.Pred(_) =>
        false

  /**
   * Make an effective view from the given view and intervals
   */
  private def makeEffectiveView[T: Domain](intervals: List[Interval[T]], view: View[T]): View[T] =
    view match
      case v @ View.Range(_, _) =>
        v
      case View.Infinite =>
        View.make(intervals, false)

  private def makeTicksLabels[T: Domain](view: View[T], canvas: Canvas, translator: Translator[T]): (List[Tick], List[Label]) =
    view match
      case View.Range(x, y) =>
        val vi     = Interval.closed[T](x, y)
        val vs     = toSpan(translator, vi)
        val ticks  = toTicks(vs)
        val labels = toLabels(canvas, vi, vs)
        (ticks, labels)
      case View.Infinite =>
        (List.empty[Tick], List.empty[Label])
