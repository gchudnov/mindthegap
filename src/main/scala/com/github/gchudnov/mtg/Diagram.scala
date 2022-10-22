package com.github.gchudnov.mtg

import Show.given
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

/**
 * Diagram
 */
final case class Diagram(
  width: Int,
  height: Int,
  spans: List[Diagram.Span],
  ticks: List[Diagram.Tick],
  labels: List[Diagram.Label],
  legends: List[Diagram.Legend]
)

object Diagram extends NumericDefaults:

  val empty: Diagram =
    Diagram(
      width = 0,
      height = 0,
      spans = List.empty[Span],
      ticks = List.empty[Tick],
      labels = List.empty[Label],
      legends = List.empty[Legend]
    )

  /**
   * Theme
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
    legend: Boolean,
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
        legend = false,
        label = Label.NoOverlap
      )

  /**
   * View
   */
  final case class View[+T: Numeric](
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

    def toInterval[T1 >: T](using Ordering[Boundary[T1]]): Interval[T1] =
      Interval.make[T1](left, true, right, true)

  object View:

    def empty[T: Numeric]: View[T] =
      View(
        left = None,
        right = None
      )

    def make[T: Numeric: Domain](intervals: List[Interval[T]])(using Ordering[Boundary[T]]): View[T] =
      val xs: List[Interval[T]] = intervals.filter(_.nonEmpty)

      val ls: List[Boundary[T]] = xs.map(_.left).filter(_.isBounded)
      val rs: List[Boundary[T]] = xs.map(_.right).filter(_.isBounded)

      val lMin: Option[T] = ls.minOption.flatMap(_.value)
      val rMax: Option[T] = rs.maxOption.flatMap(_.value)

      val (vMin, vMax) = (lMin, rMax) match
        case xy @ (Some(x), Some(y)) =>
          if summon[Ordering[T]].equiv(x, y) then (Some(summon[Domain[T]].pred(x)), Some(summon[Domain[T]].succ(y))) else xy
        case xy =>
          xy

      View(left = vMin, right = vMax)

  /**
   * Canvas
   */
  final case class Canvas(
    width: Int,
    padding: Int
  ):
    val left: Int  = 0         // start of the canvas, inclusive
    val right: Int = width - 1 // end of the canvas, inclusive

    val first: Int = left + padding  // first offset for non-inf value
    val last: Int  = right - padding // laft offset for non-inf value

    val size: Int = last - first

    /**
     * Make a label so that it is visible on the canvas.
     */
    def label(x: Int, text: String): Label =
      val p = Canvas.align(x.toDouble - (text.size.toDouble / 2.0))
      val q = p + text.size

      // if label just slighly out of the bounds, make it visible
      val x1 =
        if (p < 0 && isIn(x)) then 0
        else if (q >= width && isIn(x)) then width - text.size
        else p

      Label(x1, text)

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
     *   left and right padding betweein -inf and +inf and value on a canvas
     * @return
     *   canvas
     */
    def make(width: Int, padding: Int = 2): Canvas =
      Canvas(width = width, padding = padding)

    /**
     * Align value to the grid
     */
    def align(value: Double): Int =
      value.round.toInt

  /**
   * Translator
   */
  sealed trait Translator[T: Numeric]:
    def translate(i: Interval[T]): Span

  object Translator:
    def make[T: Numeric](view: View[T], canvas: Canvas): Translator[T] =
      new BasicTranslater[T](view, canvas)

  final class BasicTranslater[T: Numeric](view: View[T], canvas: Canvas) extends Translator[T]:

    private val ok: Option[Double] =
      view.size.map(vsz => canvas.size.toDouble / summon[Numeric[T]].toDouble(vsz))

    private def translate(b: Boundary[T]): Int =
      b.value match
        case None =>
          // -inf or +inf
          if b.isLeft then canvas.left else canvas.right
        case Some(x) =>
          ok match
            case Some(k) =>
              // finite view-boundaries
              val numT = summon[Numeric[T]]
              val left = view.left.get // NOTE: .get is safe, otherwise `k` would be None
              val dx   = numT.minus(x, left)
              val dd   = numT.toDouble(dx)
              Canvas.align((k * dd) + canvas.first)
            case None =>
              // one of the view-boundaries is inf
              if b.isLeft then canvas.first else canvas.last

    override def translate(i: Interval[T]): Span =
      val x0 = translate(i.left)
      val x1 = translate(i.right)
      Span(x0 = x0, x1 = x1, includeX0 = i.left.isInclude, includeX1 = i.right.isInclude)

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
   */
  final case class Span(x0: Int, x1: Int, includeX0: Boolean, includeX1: Boolean):
    require(x0 <= x1, s"A Span |${x0}, ${x1}| cannot be negative")

    def size: Int =
      x1 - x0

    def ticks: List[Tick] =
      List(Tick(x0), Tick(x1))

  /**
   * Legend Entry
   */
  final case class Legend(value: String)

  object Legend:
    val empty: Legend =
      Legend("")

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

      val chart = (spans ++ ticks ++ labels).filter(_.nonEmpty)

      if theme.legend then
        val legend = d.legends.map(_.value) ++ List.fill[String](chart.size - d.legends.size)("")
        chart.zip(legend).map { case (ch, lg) => s"${ch}${theme.space}|${if lg.nonEmpty then theme.space.toString else ""}${lg}" }
      else chart

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

          val indides = last.indices
          val i       = indides.find(i => (p > last(i) && q <= width))

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
      val p = math.max(span.x0, 0)
      val q = math.min(span.x1, if view.nonEmpty then view.size - 1 else 0)

      Range.inclusive(p, q).foreach(i => view(i) = theme.fill)

      if span.size > 1 then
        if (span.x0 >= 0 && span.x0 < view.size) then view(span.x0) = theme.leftBound(span.includeX0)
        if (span.x1 >= 0 && span.x1 < view.size) then view(span.x1) = theme.rightBound(span.includeX1)

  /**
   * Make a Diagram that can be rendered
   */
  def make[T: Domain: Numeric](intervals: List[Interval[T]], view: View[T], canvas: Canvas)(using Ordering[Boundary[T]]): Diagram =
    val effectiveView = if view.isEmpty then View.make(intervals) else view
    val translator    = Translator.make(effectiveView, canvas)

    val viewTicks = if view.nonEmpty then translator.translate(view.toInterval).ticks else List.empty[Tick]

    val d = intervals.filter(_.nonEmpty).foldLeft(Diagram.empty) { case (acc, i) =>
      val y = acc.height

      val span   = translator.translate(i)
      val ticks  = span.ticks
      val labels = List(canvas.label(span.x0, Show.leftValue(i.left.value)), canvas.label(span.x1, Show.rightValue(i.right.value)))
      val legend = Legend(i.show)

      acc.copy(
        width = canvas.width,
        height = y + 1,
        spans = acc.spans :+ span,
        ticks = acc.ticks ++ ticks,
        labels = acc.labels ++ labels,
        legends = acc.legends :+ legend
      )
    }

    d.copy(
      spans = d.spans,
      ticks = (d.ticks ++ viewTicks).distinct.sortBy(_.pos),
      labels = d.labels.distinct.sortBy(_.pos),
      legends = d.legends // NOTE: the order should match the order of spans
    )

  def make[T: Domain: Numeric](intervals: List[Interval[T]], canvas: Canvas)(using Ordering[Boundary[T]]): Diagram =
    make(intervals, view = View.empty[T], canvas = canvas)

  /**
   * Render the provided Diagram
   */
  def render(d: Diagram, theme: Theme): List[String] =
    val r = Renderer.make(theme)
    r.render(d)
