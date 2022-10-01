package com.github.gchudnov.mtg

import Diagram.*
import Show.*
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer

final case class Diagram(
  width: Int,
  height: Int,
  spans: List[Span],
  labels: List[Label],
  legend: List[String]
)

object Diagram:

  // TODO: ADD SETTING TO SKIP LABELS OR MOVE THEM TO THE NEXT LINE
  // TODO: ADD NO-SCALE MODE WHEN WE PAINT IN THE GIVEN WIDTH

  sealed trait LabelTheme

  object LabelTheme:
    case object None      extends LabelTheme // draw labels as-is on one line
    case object NoOverlap extends LabelTheme // draw sorted labels that are non-overlapping: next label should be separated by space from the prev one
    case object Stacked   extends LabelTheme // draw all labels, but stack them onto multiple lines

  final case class Theme(
    padding: Int,
    space: Char,
    fill: Char,
    leftOpen: Char,
    leftClosed: Char,
    rightOpen: Char,
    rightClosed: Char,
    axis: Char,
    tick: Char,
    legend: Boolean,
    label: LabelTheme
  ):
    def leftBound(isInclude: Boolean): Char =
      if isInclude then leftClosed else leftOpen

    def rightBound(isInclude: Boolean): Char =
      if isInclude then rightClosed else rightOpen

  object Theme:
    val default: Theme =
      Theme(
        padding = 2,
        space = ' ',
        fill = '*',
        leftOpen = '(',
        leftClosed = '[',
        rightOpen = ')',
        rightClosed = ']',
        axis = '-',
        tick = '+',
        legend = false,
        label = LabelTheme.None
      )

  final case class Label(tick: Int, pos: Int, value: String):
    def size: Int =
      value.size

  final case class Span(x0: Int, x1: Int, y: Int, includeX0: Boolean, includeX1: Boolean):
    def size: Int =
      x1 - x0

  val empty: Diagram =
    Diagram(width = 0, height = 0, spans = List.empty[Span], labels = List.empty[Label], legend = List.empty[String])

  /**
   * Prepare intervals for the rendering
   */
  def prepare[T: Numeric](intervals: List[Interval[T]], width: Int, padding: Int)(using bOrd: Ordering[Boundary[T]]): Diagram =
    val tNum = summon[Numeric[T]]

    val xs = intervals.filter(_.nonEmpty)

    if xs.isEmpty then Diagram.empty
    else
      val bs = xs.flatMap(it => List(it.left, it.right))

      val bMin = bs.min // might be -inf
      val bMax = bs.max // might be +inf

      val ofMin = bs.filter(_.isBounded).minOption // != -inf
      val ofMax = bs.filter(_.isBounded).maxOption // != +inf

      val cxMinInf = 0
      val cxMaxInf = width - 1
      val cxMin    = cxMinInf + padding
      val cxMax    = cxMaxInf - padding

      val cw = cxMax - cxMin
      val ofw = for
        fMin <- ofMin
        fMax <- ofMax
        r    <- fMax.value
        l    <- fMin.value
        dx   <- if !tNum.equiv(r, l) then Some(tNum.minus(r, l)) else None
      yield dx
      val ok = ofw.map(fw => cw.toDouble / tNum.toDouble(fw))

      // translates the coordindate into position on the canvas
      def translateX(value: Option[T], isLeft: Boolean): Int =
        value match
          case None =>
            // -inf or +inf
            if isLeft then cxMinInf else cxMaxInf
          case Some(x) =>
            ok match
              case None =>
                // one of the boundaries is inf
                if isLeft then cxMin else cxMax
              case Some(k) =>
                // finite boundaries
                val dx = tNum.toDouble(x) - tNum.toDouble(ofMin.flatMap(_.value).get)
                align((k * dx) + cxMin)

      def centerX(value: Option[T]): Int =
        align(cxMin + (cw / 2.0))

      def toLabelPos(x: Int, text: String): Int =
        val p = align(x - (text.size / 2.0))
        if p < 0 then 0
        else if p + text.size - 1 > width then width - text.size
        else p

      val diagram = xs.foldLeft(Diagram.empty) { case (acc, i) =>
        val y = acc.height

        // spans
        val x0   = translateX(i.left.value, isLeft = true)
        val x1   = if i.isProper then translateX(i.right.value, isLeft = false) else x0
        val span = Span(x0 = x0, x1 = x1, y = y, includeX0 = i.left.isInclude, includeX1 = i.right.isInclude)

        // labels
        val x0Text = Show.leftValue(i.left.value)
        val x0Pos  = toLabelPos(x0, x0Text)
        val x1Text = Show.rightValue(i.right.value)
        val x1Pos  = toLabelPos(x1, x1Text)
        val labels = List(Label(tick = x0, pos = x0Pos, value = x0Text), Label(tick = x1, pos = x1Pos, value = x1Text))

        // legend
        val iLegend = i.show

        acc.copy(height = y + 1, spans = acc.spans :+ span, labels = acc.labels ++ labels, legend = acc.legend :+ iLegend)
      }

      diagram.copy(width = width, labels = diagram.labels.distinct)

  /**
   * Render the provided Diagram
   */
  def render(d: Diagram, theme: Theme): List[String] =
    // axis height
    val ah = 1

    // height of labels
    val lh = theme.label match
      case LabelTheme.None | LabelTheme.NoOverlap => 1
      case LabelTheme.Stacked                     => measure(d.labels, d.width)

    val w = d.width
    val h = d.height + ah + lh // axis, labels

    val arr: Array[Array[Char]] = Array.fill[Char](h, w)(theme.space)

    // spans
    d.spans.foreach(s =>
      if s.size > 1 then
        Range(s.x0 + 1, s.x1).foreach(i => arr(s.y)(i) = theme.fill)
        arr(s.y)(s.x0) = theme.leftBound(s.includeX0)
        arr(s.y)(s.x1) = theme.rightBound(s.includeX1)
      else arr(s.y)(s.x0) = theme.fill
    )

    // separator
    Range(0, d.width).foreach(i => arr(d.height)(i) = theme.axis)

    def isOverlap(l: Label): Boolean =
      if l.pos > 0 then arr(d.height + 1)(l.pos - 1) != theme.space
      else false

    // ticks, labels
    theme.label match
      case LabelTheme.None =>
        d.labels.foreach(l =>
          drawTick(l, theme, arr(d.height))
          drawLabel(l, arr(d.height + 1))
        )
      case LabelTheme.NoOverlap =>
        d.labels
          .sortBy(_.tick)
          .foreach(l =>
            if !isOverlap(l) then
              drawTick(l, theme, arr(d.height))
              drawLabel(l, arr(d.height + 1))
          )
      case LabelTheme.Stacked =>
        val larr: Array[Array[Char]] = Array.fill[Char](lh, w)(theme.space)
        d.labels.foreach(l => drawTick(l, theme, arr(d.height)))
        drawStacked(d.labels, theme, larr)
        // copy back

    val chart  = arr.map(_.toList.mkString).toList
    val legend = d.legend ++ List.fill[String](chart.size - d.legend.size)("")

    val result = if theme.legend then chart.zip(legend).map { case (line, text) => s"${line}${theme.space}|${if text.nonEmpty then theme.space.toString else ""}${text}" }
    else chart

    result

  /**
   * Align value to the grid
   */
  private def align(value: Double): Int =
    value.round.toInt

  /**
   * Measure the number of lines required to draw all labels
   */
  private def measure(ls: List[Label], width: Int): Int =
    val res = ls.sortBy(_.tick).foldLeft(ListBuffer[Int](0)) { case (acc, l) =>
      val first = l.pos
      val last  = l.pos + l.size

      val row = acc.indices.find(i =>
        if first > acc(i) && last <= width then true
        else false
      )

      row match
        case None =>
          acc.addOne(last)
        case Some(i) =>
          acc(i) = last
          acc
    }
    res.size

  private def drawTick(l: Label, theme: Theme, view: Array[Char]): Unit =
    view(l.tick) = theme.tick

  private def drawLabel(l: Label, view: Array[Char]): Unit =
    l.value.toList.zipWithIndex.foreach { case (ch, i) =>
      view(l.pos + i) = ch
    }

  /**
   * Draw Stacked Labels
   */
  private def drawStacked(ls: List[Label], theme: Theme, view: Array[Array[Char]]): Unit =
    // val width = arr(0).size
    // val height = arr.size

    // ls.sortBy(_.tick).foreach(l =>
    //   val first = l.pos
    //   val last  = l.pos + l.size

    //   val row = Range(0, height).find(i =>
    //     if arr(i)(first) == theme.space && last <= width then true
    //     else false
    //   )

    //   arr(row)(l.pos)
    // )
    ()
