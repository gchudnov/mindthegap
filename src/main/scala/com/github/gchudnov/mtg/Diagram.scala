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
  legend: List[Diagram.Legend]
)

object Diagram:

  val empty: Diagram =
    Diagram(
      width = 0,
      height = 0,
      spans = List.empty[Span],
      ticks = List.empty[Tick],
      labels = List.empty[Label],
      legend = List.empty[Legend]
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
      case NoOverlap // draw sorted labels that are non-overlapping: next label should be separated by space from the prev one
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
        label = Label.None
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

    // println(s"left: ${left}, right: ${right}, first: ${first}, last: ${last}, size: ${size}")

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

    def make(width: Int, padding: Int): Canvas =
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

    // println(("ok", ok))

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
      Span(x0 = x0, x1 = x1, y = 0, includeX0 = i.left.isInclude, includeX1 = i.right.isInclude)

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
  final case class Span(x0: Int, x1: Int, y: Int, includeX0: Boolean, includeX1: Boolean):
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
      // axis height
      val ah = 1

      // height of labels
      val labels = drawLabels(d.labels, d.width)

      // println(("labels", labels))

      ???

    /**
     * Calculate the height of the labels
     */
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
          if l.pos > last then
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

          println(l)

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

    /**
     * Measure the number of lines required to draw all labels
     */
    // private def measure(ls: List[Label], width: Int): Int =
    //   val res = ls.sortBy(_.pos).foldLeft(ListBuffer[Int](0)) { case (acc, l) =>
    //     val first = l.pos
    //     val last  = l.pos + l.size

    //     val row = acc.indices.find(i =>
    //       if first > acc(i) && last <= width then true
    //       else false
    //     )

    //     row match
    //       case None =>
    //         acc.addOne(last)
    //       case Some(i) =>
    //         acc(i) = last
    //         acc
    //   }
    //   res.size

//   /**
//    * Draw Stacked Labels
//    */
//   private def drawStacked(ls: List[Label], theme: Theme, view: Array[Array[Char]]): Unit =
//     val width  = view(0).size
//     val height = view.size

//     ls.sortBy(_.tick)
//       .foreach(l =>
//         val first = l.pos
//         val last  = l.pos + l.size

//         val prev = if first > 0 then first - 1 else 0

//         val row = view.indices.find(i =>
//           if view(i)(prev) == theme.space && last <= width then true
//           else false
//         )

//         row match
//           case None    => require(false, "labels were incorrectly measured")
//           case Some(x) => drawLabel(l, view(x))
//       )

    private def drawLabel(l: Label, view: Array[Char]): Unit =
      l.value.toList.zipWithIndex.foreach { case (ch, i) =>
        val p = l.pos + i
        if (p >= 0 && p < view.size) then view(p) = ch
      }

  /**
   * Make a Diagram that can be rendered
   */
  def make[T: Domain: Numeric](intervals: List[Interval[T]], view: View[T], canvas: Canvas)(using Ordering[Boundary[T]]): Diagram =
    val effectiveView = if view.isEmpty then View.make(intervals) else view
    val translator    = Translator.make(effectiveView, canvas)

    // println(("effectiveView", effectiveView))

    val d = intervals.filter(_.nonEmpty).foldLeft(Diagram.empty) { case (acc, i) =>
      val y = acc.height

      val span    = translator.translate(i).copy(y = y)
      val ticks   = span.ticks
      val labels  = List(canvas.label(span.x0, Show.leftValue(i.left.value)), canvas.label(span.x1, Show.rightValue(i.right.value)))
      val iLegend = Legend(i.show)

      acc.copy(
        width = canvas.width,
        height = y + 1,
        spans = acc.spans :+ span,
        ticks = acc.ticks ++ ticks,
        labels = acc.labels ++ labels,
        legend = acc.legend :+ iLegend
      )
    }

    d.copy(
      spans = d.spans,
      ticks = d.ticks.distinct,
      labels = d.labels.distinct,
      legend = d.legend.distinct
    )

  /**
   * Render the provided Diagram
   */
  def render(d: Diagram, theme: Theme): List[String] =
    val r = Renderer.make(theme)
    r.render(d)

//     val w = d.width
//     val h = d.height + ah + lh // axis, labels

//     val arr: Array[Array[Char]] = Array.fill[Char](h, w)(theme.space)

//     // spans
//     d.spans.foreach(s =>
//       if s.size > 1 then
//         val s0 = math.max(s.x0, 0)
//         val s1 = math.min(s.x1, w)

//         Range(s0, s1).foreach(i => arr(s.y)(i) = theme.fill)

//         if (s.x0 >= 0 && s.x0 < w) then arr(s.y)(s.x0) = theme.leftBound(s.includeX0)
//         if (s.x1 >= 0 && s.x1 < w) then arr(s.y)(s.x1) = theme.rightBound(s.includeX1)
//       else if (s.x0 >= 0 && s.x0 < w) then arr(s.y)(s.x0) = theme.fill
//     )

//     // separator
//     Range(0, d.width).foreach(i => arr(d.height)(i) = theme.axis)

//     def isOverlap(l: Label): Boolean =
//       if l.pos > 0 then arr(d.height + 1)(l.pos - 1) != theme.space
//       else false

//     // ticks, labels
//     theme.label match
//       case LabelTheme.None =>
//         d.labels.foreach(l =>
//           drawTick(l, theme, arr(d.height))
//           drawLabel(l, arr(d.height + 1))
//         )
//       case LabelTheme.NoOverlap =>
//         d.labels
//           .sortBy(_.tick)
//           .foreach(l =>
//             if !isOverlap(l) then
//               drawTick(l, theme, arr(d.height))
//               drawLabel(l, arr(d.height + 1))
//           )
//       case LabelTheme.Stacked =>
//         val larr: Array[Array[Char]] = Array.fill[Char](lh, w)(theme.space)
//         d.labels.foreach(l => drawTick(l, theme, arr(d.height)))
//         drawStacked(d.labels, theme, larr)
//         larr.zipWithIndex.foreach { case (a, i) => a.copyToArray(arr(d.height + ah + i)) }

//     val chart  = arr.map(_.toList.mkString).toList
//     val legend = d.legend ++ List.fill[String](chart.size - d.legend.size)("")

//     val result = if theme.legend then chart.zip(legend).map { case (line, text) => s"${line}${theme.space}|${if text.nonEmpty then theme.space.toString else ""}${text}" }
//     else chart

// val result = ???

// result

//   private def clap(value: Double, minValue: Double, maxValue: Double): Double =
//     if value < minValue then minValue else if value > maxValue then maxValue else value

//   private def drawTick(l: Label, theme: Theme, view: Array[Char]): Unit =
//     if ((l.tick >= 0) && (l.tick < view.size)) then view(l.tick) = theme.tick

//   /**
//    * Draw Stacked Labels
//    */
//   private def drawStacked(ls: List[Label], theme: Theme, view: Array[Array[Char]]): Unit =
//     val width  = view(0).size
//     val height = view.size

//     ls.sortBy(_.tick)
//       .foreach(l =>
//         val first = l.pos
//         val last  = l.pos + l.size

//         val prev = if first > 0 then first - 1 else 0

//         val row = view.indices.find(i =>
//           if view(i)(prev) == theme.space && last <= width then true
//           else false
//         )

//         row match
//           case None    => require(false, "labels were incorrectly measured")
//           case Some(x) => drawLabel(l, view(x))
//       )
