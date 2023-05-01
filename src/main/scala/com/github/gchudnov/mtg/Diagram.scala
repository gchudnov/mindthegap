package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.diagram.Span
import com.github.gchudnov.mtg.diagram.internal.BasicRenderer
import com.github.gchudnov.mtg.diagram.internal.BasicTranslator
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

import Show.given
import com.github.gchudnov.mtg.internal.DiagramMacro

/**
 * Diagram
 */
final case class Diagram(
  width: Int,
  height: Int,
  spans: List[Span],
  ticks: List[Diagram.Tick],
  labels: List[Diagram.Label],
  legends: List[Diagram.Legend],
  annotations: List[Diagram.Annotation]
)

object Diagram:

  export com.github.gchudnov.mtg.diagram.Span

  val empty: Diagram =
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
   * Theme
   *
   * Provides rendering options.
   */
  final case class Theme(
    space: Char,
    fill: Char,
    leftOpen: Char,
    leftClosed: Char,
    rightOpen: Char,
    rightClosed: Char,
    axis: Char,
    tick: Char,
    border: Char,
    comment: Char,
    legend: Boolean,
    annotations: Boolean,
    label: Theme.Label
  ):
    def leftBound(isInclude: Boolean): Char =
      if isInclude then leftClosed else leftOpen

    def rightBound(isInclude: Boolean): Char =
      if isInclude then rightClosed else rightOpen

  object Theme:
    enum Label:
      case None      // draw labels as-is on one line
      case NoOverlap // draw sorted labels that are non-overlapping, some of the labels might be skipped
      case Stacked   // draw all labels, but stack them onto multiple lines

    val default: Theme =
      Theme(
        space = ' ',
        fill = '*',
        leftOpen = '(',
        leftClosed = '[',
        rightOpen = ')',
        rightClosed = ']',
        axis = '-',
        tick = '+',
        border = '|',
        comment = ':',
        legend = true,
        annotations = true,
        label = Label.NoOverlap
      )

  /**
   * View
   *
   * Specifies the range to display.
   */
  final case class View[T: Domain](
    left: Option[T], // left boundary of the view
    right: Option[T] // right boundary of the view
  ):
    require(
      left.flatMap(lhs => right.map(rhs => summon[Ordering[T]].lteq(lhs, rhs))).getOrElse(true),
      "left view boundary must be less or equal to the right view boundary"
    )

    val size: Option[Long] =
      for
        lhs <- left
        rhs <- right
        dx   = summon[Domain[T]].count(lhs, rhs)
      yield dx

    def isEmpty: Boolean =
      left.isEmpty && right.isEmpty

    def nonEmpty: Boolean =
      !isEmpty

    def toInterval: Interval[T] =
      Interval.make(left.map(Value.finite(_)).getOrElse(Value.infNeg), right.map(Value.finite(_)).getOrElse(Value.infPos))

  object View:

    def default[T: Domain]: View[T] =
      View(
        left = None,
        right = None
      )

    def make[T: Domain](left: Option[T], right: Option[T]): View[T] =
      View(left = left, right = right)

    private[mtg] def make[T: Domain](intervals: List[Interval[T]])(using ordT: Ordering[Value[T]]): View[T] =
      val xs: List[Interval[T]] = intervals.filter(_.nonEmpty) // TODO: If Empty intervals are displayed, we will need to change this condition

      val ps = xs.flatMap(toLeftRightValues).filter(_.isFinite)

      val (vMin, vMax) = (ps.minOption, ps.maxOption) match
        case xy @ (Some(x), Some(y)) =>
          if ordT.equiv(x, y) then (Some(x.pred), Some(y.succ)) else xy
        case xy =>
          xy

      val (p, q) = (vMin.map(_.get), vMax.map(_.get))
      View(left = p, right = q)

    private def toLeftRightValues[T: Domain](i: Interval[T]): List[Value[T]] =
      List(toLeftValue(i.left), toRightValue(i.right))

    private def toLeftValue[T: Domain](left: Mark[T]): Value[T] =
      left match
        case Mark.At(x) =>
          x
        case Mark.Pred(_) =>
          left.eval
        case Mark.Succ(xx) =>
          xx.eval

    private def toRightValue[T: Domain](right: Mark[T]): Value[T] =
      right match
        case Mark.At(y) =>
          y
        case Mark.Pred(yy) =>
          yy.eval
        case Mark.Succ(_) =>
          right.eval

  /**
   * Canvas
   *
   * Specifies the width of the text buffer to draw a diagram on.
   */
  final case class Canvas(
    width: Int,
    padding: Int
  ):
    val left: Int  = 0         // start of the canvas, inclusive
    val right: Int = width - 1 // end of the canvas, inclusive

    val first: Int = left + padding  // first offset for non-inf value
    val last: Int  = right - padding // last offset for non-inf value

    val size: Int = last - first

    /**
     * Make a label so that it is visible on the canvas.
     */
    def label(x: Int, text: String): Label =
      val p = Canvas.align(x.toDouble - (text.size.toDouble / 2.0))
      val q = p + text.size

      // if label just slightly out of the bounds, make it visible
      val x1 =
        if p < 0 && isIn(x) then 0
        else if q >= width && isIn(x) then width - text.size
        else p

      Label(x1, text)

    def labels[T: Domain](i: Interval[T], span: Span)(using Ordering[Mark[T]]): List[Label] =
      if i.isEmpty then List.empty[Label]
      else if i.isPoint then List(label(span.x0, Show.str(i.left.eval)))
      else
        List(
          label(span.x0, toLeftLabel(i.left)),
          label(span.x1, toRightLabel(i.right))
        )

    private def toLeftLabel[T: Domain](left: Mark[T]): String =
      left match
        case Mark.At(x) =>
          Show.str(x)
        case Mark.Succ(xx) =>
          Show.str(xx.eval)
        case xx @ Mark.Pred(_) =>
          Show.str(xx.eval)

    private def toRightLabel[T: Domain](right: Mark[T]): String =
      right match
        case Mark.At(y) =>
          Show.str(y)
        case yy @ Mark.Succ(_) =>
          Show.str(yy.eval)
        case Mark.Pred(yy) =>
          Show.str(yy.eval)

    private def isIn(x: Int): Boolean =
      (x >= 0 && x < width)

  object Canvas:
    val default: Canvas =
      Canvas(
        width = 40,
        padding = 2
      )

    /**
     * Make a new Canvas
     *
     * @param width
     *   width of the canvas
     * @param padding
     *   left and right padding between -inf and +inf and value on a canvas
     * @return
     *   canvas
     */
    def make(width: Int, padding: Int = 2): Canvas =
      Canvas(width = width, padding = padding)

    /**
     * Align value to the grid
     */
    private[mtg] def align(value: Double): Int =
      value.round.toInt

  /**
   * Translator
   */
  trait Translator[T: Domain]:
    def translate(i: Interval[T]): Span

  object Translator:
    def make[T: Domain](view: View[T], canvas: Canvas)(using Ordering[Mark[T]]): Translator[T] =
      new BasicTranslator[T](view, canvas)

  /**
   * Tick
   */
  final case class Tick(pos: Int)

  /**
   * Label
   */
  final case class Label(pos: Int, value: String):
    def size: Int =
      value.size

  object Label:
    val empty: Label =
      Label(0, "")

  /**
   * Legend Entry
   */
  final case class Legend(value: String):
    def isEmpty: Boolean =
      value.isEmpty

    def nonEmpty: Boolean =
      value.nonEmpty

  object Legend:
    val empty: Legend =
      Legend("")

    def make[T: Domain](i: Interval[T])(using Ordering[Mark[T]]): Legend =
      Legend(Show.asString(i))

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

  /**
   * Renderer
   */
  trait Renderer:
    def render(d: Diagram): List[String]

  object Renderer:
    def make(theme: Theme): Renderer =
      new BasicRenderer(theme)

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
   * Render the provided Diagram
   */
  def render(d: Diagram, theme: Theme = Theme.default): List[String] =
    val r = Renderer.make(theme)
    r.render(d)
