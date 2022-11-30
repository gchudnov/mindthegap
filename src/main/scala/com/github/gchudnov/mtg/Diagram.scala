package com.github.gchudnov.mtg

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

import Show.given

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

object Diagram extends NumericDefaults:

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
   *
   * TODO: instead of Numeric, we need to use Domain here and replace minus with d(...)
   */
  final case class View[T: Numeric](
    left: Option[T], // left boundary of the view
    right: Option[T] // right boundary of the view
  ):
    require(
      left.flatMap(lhs => right.map(rhs => summon[Ordering[T]].lteq(lhs, rhs))).getOrElse(true),
      "left view boundary must be less or equal to the right view boundary"
    )

    val size: Option[T] =
      for
        lhs <- left
        rhs <- right
        dx   = summon[Numeric[T]].minus(rhs, lhs)
      yield dx

    def isEmpty: Boolean =
      left.isEmpty && right.isEmpty

    def nonEmpty: Boolean =
      !isEmpty

    def toInterval: Interval[T] =
      Interval.make(left.map(Value.finite(_)).getOrElse(Value.infNeg), right.map(Value.finite(_)).getOrElse(Value.infPos))

  object View:

    def default[T: Numeric]: View[T] =
      View(
        left = None,
        right = None
      )

    def make[T: Numeric: Domain](left: Option[T], right: Option[T]): View[T] =
      View(left = left, right = right)

    private[mtg] def make[T: Numeric: Domain](intervals: List[Interval[T]])(using ordT: Ordering[Value[T]]): View[T] =
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
        if (p < 0 && isIn(x)) then 0
        else if (q >= width && isIn(x)) then width - text.size
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
  sealed trait Translator[T: Numeric]:
    def translate(i: Interval[T]): Span

  object Translator:
    def make[T: Numeric: Domain](view: View[T], canvas: Canvas)(using Ordering[Mark[T]]): Translator[T] =
      new BasicTranslator[T](view, canvas)

  final class BasicTranslator[T: Numeric: Domain](view: View[T], canvas: Canvas)(using Ordering[Mark[T]]) extends Translator[T]:

    private val ok: Option[Double] =
      view.size.map(vsz => canvas.size.toDouble / summon[Numeric[T]].toDouble(vsz))

    private def translateValue(value: Value[T]): Int =
      value match
        case Value.InfNeg =>
          canvas.left
        case Value.InfPos =>
          canvas.right
        case Value.Finite(x) =>
          val k    = ok.get        // NOTE: it is not possible to have k undefined if we have at least one Finite point
          val numT = summon[Numeric[T]]
          val left = view.left.get // NOTE: .get is safe, otherwise `k` would be None
          val dx   = numT.minus(x, left)
          val dd   = numT.toDouble(dx)
          Canvas.align((k * dd) + canvas.first)

    override def translate(i: Interval[T]): Span =
      if i.isEmpty then Span.empty
      else if i.isPoint then
        val p = translateValue(i.left.eval)
        Span(x0 = p, x1 = p, includeX0 = true, includeX1 = true)
      else Span.make(x0 = translateLeft(i.left), x1 = translateRight(i.right), includeX0 = includeLeft(i.left), includeX1 = includeRight(i.right))

    private def translateLeft(left: Mark[T]): Int =
      left match
        case Mark.At(x) =>
          translateValue(x)
        case Mark.Succ(xx) =>
          translateValue(xx.eval)
        case xx @ Mark.Pred(_) =>
          translateValue(xx.eval)

    private def translateRight(right: Mark[T]): Int =
      right match
        case Mark.At(y) =>
          translateValue(y)
        case yy @ Mark.Succ(_) =>
          translateValue(yy.eval)
        case Mark.Pred(yy) =>
          translateValue(yy.eval)

    private def includeLeft(left: Mark[T]): Boolean =
      left match
        case Mark.At(x) =>
          x.isFinite
        case Mark.Succ(_) =>
          false
        case xx @ Mark.Pred(_) =>
          xx.eval.isFinite

    private def includeRight(right: Mark[T]): Boolean =
      right match
        case Mark.At(y) =>
          y.isFinite
        case yy @ Mark.Succ(_) =>
          yy.eval.isFinite
        case Mark.Pred(_) =>
          false

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
   * Span
   *
   * NOTE: Use negative span for an empty interval
   */
  final case class Span(x0: Int, x1: Int, includeX0: Boolean, includeX1: Boolean):

    def isEmpty: Boolean =
      x1 < x0

    def nonEmpty: Boolean =
      !isEmpty

    def size: Int =
      x1 - x0

    def ticks: List[Tick] =
      if isEmpty then List.empty[Tick]
      else List(Tick(x0), Tick(x1))

  object Span:
    val empty: Span =
      Span(1, -1, true, true)

    def make(x0: Int, x1: Int, includeX0: Boolean, includeX1: Boolean): Span =
      Span(x0, x1, includeX0, includeX1)

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
  sealed trait Renderer:
    def render(d: Diagram): List[String]

  object Renderer:
    def make(theme: Theme): Renderer =
      new BasicRenderer(theme)

  /**
   * Basic Renderer
   */
  final class BasicRenderer(theme: Theme) extends Renderer:

    override def render(d: Diagram): List[String] =
      val spans  = drawSpans(d.spans, d.width)
      val ticks  = drawTicks(d.ticks, d.width)
      val labels = drawLabels(d.labels, d.width)

      val chart = (spans ++ ticks ++ labels)

      // format: "span | legend : annotation"
      val legends     = padWithEmptyLines(chart.size)(d.legends.map(_.value))
      val annotations = padWithEmptyLines(chart.size)(d.annotations.map(_.value))

      val addLegends     = theme.legend && legends.exists(_.nonEmpty)
      val addAnnotations = theme.annotations && annotations.exists(_.nonEmpty)

      // with borders
      val withBorder =
        if (addLegends || addAnnotations) then chart.map(line => if line.nonEmpty then s"${line}${theme.space}${theme.border}" else line)
        else chart

      // with legend
      val withLegend =
        if addLegends then withBorder.zip(legends).map { case (line, legend) => if (line.nonEmpty && legend.nonEmpty) then s"${line}${theme.space}${legend}" else line }
        else withBorder

      // with annotations
      val annotated =
        if addAnnotations then
          val maxLineSize = withLegend.map(_.size).maxOption.getOrElse(0)
          withLegend.zip(annotations).map { case (line, annotation) =>
            if (line.nonEmpty && annotation.nonEmpty) then
              val padSize    = maxLineSize - line.size
              val paddedLine = padRight(padSize, theme.space)(line)
              val separator  = if addLegends then s"${theme.space}${theme.comment}" else ""
              s"${paddedLine}${separator}${theme.space}${annotation}"
            else line
          }
        else withLegend

      annotated.filter(_.nonEmpty)

    private[mtg] def drawSpans(spans: List[Span], width: Int): List[String] =
      val views: Array[Array[Char]] = Array.fill[Char](spans.size, width)(theme.space)
      spans.zipWithIndex.foreach((span, i) => drawSpan(span, views(i)))
      views.map(_.mkString).toList

    private[mtg] def drawTicks(ts: List[Tick], width: Int): List[String] =
      val view = Array.fill[Char](width)(theme.axis)
      ts.sortBy(_.pos).foreach(t => drawTick(t, view))
      List(view.mkString)

    private[mtg] def drawLabels(ls: List[Label], width: Int): List[String] =
      theme.label match
        case Theme.Label.None =>
          drawLabelsNone(ls, width)
        case Theme.Label.NoOverlap =>
          drawLabelsNoOverlap(ls, width)
        case Theme.Label.Stacked =>
          drawLabelsStacked(ls, width)

    private def drawLabelsNone(ls: List[Label], width: Int): List[String] =
      val view = Array.fill[Char](width)(theme.space)
      ls.sortBy(_.pos).foreach(l => drawLabel(l, view))
      List(view.mkString)

    private def drawLabelsNoOverlap(ls: List[Label], width: Int): List[String] =
      val view = Array.fill[Char](width)(theme.space)
      ls.sortBy(_.pos)
        .foldLeft(-1)((last, l) =>
          if (l.pos > last) || (l.pos > 0 && l.pos == last && (!view(l.pos - 1).isDigit || (l.value.nonEmpty && !l.value(0).isDigit))) then
            drawLabel(l, view)
            l.pos + l.value.size
          else last
        )
      List(view.mkString)

    private def drawLabelsStacked(ls: List[Label], width: Int): List[String] =
      val emptyState = (Vector(Array.fill[Char](width)(theme.space)), ListBuffer[Int](-1))
      val res = ls
        .sortBy(_.pos)
        .foldLeft(emptyState)((acc, l) =>
          val p = l.pos
          val q = l.pos + l.size

          val views = acc._1
          val last  = acc._2

          val indices = last.indices
          val i       = indices.find(i => (p > last(i) && q <= width))

          i match
            case None =>
              val view = Array.fill[Char](width)(theme.space)
              drawLabel(l, view)
              (views :+ view, last.addOne(q))
            case Some(j) =>
              val view = views(j)
              drawLabel(l, view)
              last(j) = q
              (views, last)
        )

      res._1.map(_.mkString).toList

    private def drawLabel(l: Label, view: Array[Char]): Unit =
      l.value.toList.zipWithIndex.foreach { case (ch, i) =>
        val p = l.pos + i
        if (p >= 0 && p < view.size) then view(p) = ch
      }

    private def drawTick(t: Tick, view: Array[Char]): Unit =
      if ((t.pos >= 0) && (t.pos < view.size)) then view(t.pos) = theme.tick

    private def drawSpan(span: Span, view: Array[Char]): Unit =
      if span.nonEmpty then
        val p = math.max(span.x0, 0)
        val q = math.min(span.x1, if view.nonEmpty then view.size - 1 else 0)

        Range.inclusive(p, q).foreach(i => view(i) = theme.fill)

        if span.size > 1 then
          if (span.x0 >= 0 && span.x0 < view.size) then view(span.x0) = theme.leftBound(span.includeX0)
          if (span.x1 >= 0 && span.x1 < view.size) then view(span.x1) = theme.rightBound(span.includeX1)

    private[mtg] def padRight(n: Int, pad: Char)(value: String): String =
      if n > 0 then value + (pad.toString * n)
      else value

    private[mtg] def padWithEmptyLines(n: Int)(xs: List[String]): List[String] =
      if ((xs.size < n) && (n > 0)) then xs ++ List.fill[String](n - xs.size)("")
      else xs

  /**
   * Make a Diagram that can be rendered
   */
  def make[T: Domain: Numeric](intervals: List[Interval[T]], view: View[T], canvas: Canvas, annotations: List[String])(using Ordering[Mark[T]]): Diagram =
    val effectiveView = if view.isEmpty then View.make(intervals) else view
    val translator    = Translator.make(effectiveView, canvas)

    // if view is specified, provide labels and ticks to mark the boundaries of the view
    val (viewTicks, viewLabels) = if view.nonEmpty then
      val vi = view.toInterval
      val vs = translator.translate(vi)
      (vs.ticks, canvas.labels(vi, vs))
    else (List.empty[Tick], List.empty[Label])

    val d = intervals.zipWithIndex.foldLeft(Diagram.empty) { case (acc, (i, j)) =>
      val y = acc.height

      val span   = translator.translate(i)
      val ticks  = span.ticks
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

  def make[T: Domain: Numeric](intervals: List[Interval[T]], view: View[T], canvas: Canvas)(using Ordering[Mark[T]]): Diagram =
    make(intervals, view = view, canvas = canvas, annotations = List.empty[String])

  def make[T: Domain: Numeric](intervals: List[Interval[T]], view: View[T])(using Ordering[Mark[T]]): Diagram =
    make(intervals, view = view, canvas = Canvas.default, annotations = List.empty[String])

  def make[T: Domain: Numeric](intervals: List[Interval[T]])(using Ordering[Mark[T]]): Diagram =
    make(intervals, view = View.default[T], canvas = Canvas.default, annotations = List.empty[String])

  def make[T: Domain: Numeric](intervals: List[Interval[T]], canvas: Canvas)(using Ordering[Mark[T]]): Diagram =
    make(intervals, view = View.default[T], canvas = canvas, annotations = List.empty[String])

  def make[T: Domain: Numeric](intervals: List[Interval[T]], annotations: List[String])(using Ordering[Mark[T]]): Diagram =
    make(intervals, view = View.default[T], canvas = Canvas.default, annotations = annotations)

  /**
   * Render the provided Diagram
   */
  def render(d: Diagram, theme: Theme = Theme.default): List[String] =
    val r = Renderer.make(theme)
    r.render(d)
