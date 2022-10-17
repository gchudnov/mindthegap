package com.github.gchudnov.mtg

import Diagram.*
import Show.*
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

final case class Diagram(
  width: Int,
  height: Int,
  spans: List[Span],
  ticks: List[Tick],
  labels: List[Label],
  legend: List[String]
)

object Diagram:

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

  final case class Canvas[+T](
    width: Int,
    padding: Int,
    left: Option[T], // left boundary
    right: Option[T] // right boundary
  )

  object Canvas:

    val small: Canvas[Nothing] =
      Canvas[Nothing](
        width = 40,
        padding = 2,
        left = None,
        right = None
      )

  final case class Tick(pos: Int)

  final case class Label(pos: Int, value: String):
    def size: Int =
      value.size

  final case class Span(x0: Int, x1: Int, y: Int, includeX0: Boolean, includeX1: Boolean):
    require(x0 <= x1, s"A Span |${x0}, ${x1}| is negative")

    def size: Int =
      x1 - x0

  val empty: Diagram =
    Diagram(
      width = 0,
      height = 0,
      spans = List.empty[Span],
      ticks = List.empty[Tick],
      labels = List.empty[Label],
      legend = List.empty[String]
    )

  /**
   * Prepare intervals for the rendering
   */
  def prepare[T: Numeric](intervals: List[Interval[T]], canvas: Canvas[T])(using bOrd: Ordering[Boundary[T]]): Diagram =
    val tNum = summon[Numeric[T]]

    val xs = intervals.filter(_.nonEmpty)
    if xs.isEmpty then Diagram.empty
    else
      val bs: List[Boundary[T]] = xs.flatMap(it => List(it.left, it.right))

      val ofMin = canvas.left.fold(bs.filter(_.isBounded).minOption.flatMap(_.value))(Some(_))
      val ofMax = canvas.right.fold(bs.filter(_.isBounded).maxOption.flatMap(_.value))(Some(_))

//       val cxFirst = 0
//       val cxLast  = canvas.width - 1
//       val cxMin   = cxFirst + canvas.padding
//       val cxMax   = cxLast - canvas.padding
//       val cw      = cxMax - cxMin

//       val ofw = for
//         fMin <- ofMin
//         fMax <- ofMax
//         dx   <- if !tNum.equiv(fMax, fMin) then Some(tNum.minus(fMax, fMin)) else None
//       yield dx

//       val ok = ofw.map(fw => cw.toDouble / tNum.toDouble(fw))

//       // translates the coordindate into position on the canvas
//       def translateX(value: Option[T], isLeft: Boolean): Int =
//         value match
//           case None =>
//             // -inf or +inf
//             if isLeft then cxFirst else cxLast
//           case Some(x) =>
//             ok match
//               case None =>
//                 // one of the boundaries is inf
//                 if isLeft then cxMin else cxMax
//               case Some(k) =>
//                 // finite boundaries
//                 val fMin = ofMin.get
//                 val df   = tNum.minus(x, fMin)
//                 val dd   = tNum.toDouble(df)
//                 align((k * dd) + cxMin)

//       def centerX(value: Option[T]): Int =
//         align(cxMin + (cw / 2.0))

//       /**
//        * Calculate Label position; Try to align only if close to ends of the canvas
//        */
//       def toLabelPos(x: Int, text: String): Int =
//         val p = align(x - (text.size / 2.0))
//         val q = p + text.size

//         if (p < 0 && (x >= 0 && x < canvas.width)) then 0
//         else if (q >= canvas.width && (x >= 0 && x < canvas.width)) then canvas.width - text.size
//         else p

//       val diagram = xs.foldLeft(Diagram.empty) { case (acc, i) =>
//         val y = acc.height

//         // spans
//         val x0   = translateX(i.left.value, isLeft = true)
//         val x1   = if i.isProper then translateX(i.right.value, isLeft = false) else x0
//         val span = Span(x0 = x0, x1 = x1, y = y, includeX0 = i.left.isInclude, includeX1 = i.right.isInclude)

//         // labels
//         val x0Text = Show.leftValue(i.left.value)
//         val x0Pos  = toLabelPos(x0, x0Text)
//         val x1Text = Show.rightValue(i.right.value)
//         val x1Pos  = toLabelPos(x1, x1Text)
//         val labels = List(Label(tick = x0, pos = x0Pos, value = x0Text), Label(tick = x1, pos = x1Pos, value = x1Text))

//         // legend
//         val iLegend = i.show

//         acc.copy(height = y + 1, spans = acc.spans :+ span, labels = acc.labels ++ labels, legend = acc.legend :+ iLegend)
//       }

//       diagram.copy(width = canvas.width, labels = diagram.labels.distinct)

      ???

//   /**
//    * Render the provided Diagram
//    */
//   def render(d: Diagram, theme: Theme): List[String] =
//     // axis height
//     val ah = 1

//     // height of labels
//     val lh = theme.label match
//       case LabelTheme.None | LabelTheme.NoOverlap => 1
//       case LabelTheme.Stacked                     => measure(d.labels, d.width)

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

//     result

//   private def clap(value: Double, minValue: Double, maxValue: Double): Double =
//     if value < minValue then minValue else if value > maxValue then maxValue else value

//   /**
//    * Align value to the grid
//    */
//   private def align(value: Double): Int =
//     value.round.toInt

//   /**
//    * Measure the number of lines required to draw all labels
//    */
//   private def measure(ls: List[Label], width: Int): Int =
//     val res = ls.sortBy(_.tick).foldLeft(ListBuffer[Int](0)) { case (acc, l) =>
//       val first = l.pos
//       val last  = l.pos + l.size

//       val row = acc.indices.find(i =>
//         if first > acc(i) && last <= width then true
//         else false
//       )

//       row match
//         case None =>
//           acc.addOne(last)
//         case Some(i) =>
//           acc(i) = last
//           acc
//     }
//     res.size

//   private def drawTick(l: Label, theme: Theme, view: Array[Char]): Unit =
//     if ((l.tick >= 0) && (l.tick < view.size)) then view(l.tick) = theme.tick

//   private def drawLabel(l: Label, view: Array[Char]): Unit =
//     l.value.toList.zipWithIndex.foreach { case (ch, i) =>
//       val p = l.pos + i
//       if (p >= 0 && p < view.size) then view(p) = ch
//     }

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
