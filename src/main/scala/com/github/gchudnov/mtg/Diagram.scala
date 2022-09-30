package com.github.gchudnov.mtg

import Diagram.*
import scala.collection.mutable.ArrayBuffer

final case class Diagram(
  width: Int,
  height: Int,
  spans: List[Span],
  labels: List[Label]
)

object Diagram:

  // TODO: ADD SETTING TO SKIP LABELS OR MOVE THEM TO THE NEXT LINE
  // TODO: ADD NO-SCALE MODE WHEN WE PAINT IN THE GIVEN WIDTH

  final case class Theme(
    padding: Int,
    fill: Char,
    leftOpen: Char,
    leftClosed: Char,
    rightOpen: Char,
    rightClosed: Char,
    axis: Char,
    tick: Char
  ):
    def leftBound(isInclude: Boolean): Char =
      if isInclude then leftClosed else leftOpen

    def rightBound(isInclude: Boolean): Char =
      if isInclude then rightClosed else rightOpen

  object Theme:
    val default: Theme =
      Theme(
        padding = 2,
        fill = '*',
        leftOpen = '(',
        leftClosed = '[',
        rightOpen = ')',
        rightClosed = ']',
        axis = '-',
        tick = '+'
      )

  final case class Label(tick: Int, pos: Int, value: String)

  final case class Span(x0: Int, x1: Int, y: Int, includeX0: Boolean, includeX1: Boolean):
    def size: Int =
      x1 - x0

  val empty: Diagram =
    Diagram(width = 0, height = 0, spans = List.empty[Span], labels = List.empty[Label])

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

      val cw  = cxMax - cxMin
      val ofw = ofMin.flatMap(fMin => ofMax.map(fMax => tNum.minus(fMax.value.get, fMin.value.get)))
      val ok  = ofw.map(fw => cw.toDouble / tNum.toDouble(fw))

      println(cw)
      println(ofw)
      println(ok)

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
                (k * dx + cxMin).toInt

      def toLabelPos(x: Int, text: String): Int =
        val p = x - (text.size / 2)
        if p < 0 then 0
        else if p + text.size - 1 > width then width - text.size
        else p

      val diagram = xs.foldLeft(Diagram.empty) { case (acc, i) =>
        val y = acc.height

        // spans
        val x0    = translateX(i.left.value, isLeft = true)
        val x1    = translateX(i.right.value, isLeft = false)
        val spans = acc.spans :+ Span(x0 = x0, x1 = x1, y = y, includeX0 = i.left.isInclude, includeX1 = i.right.isInclude)

        // labels
        val x0Text = Show.leftValue(i.left.value)
        val x0Pos  = toLabelPos(x0, x0Text)
        val x1Text = Show.rightValue(i.right.value)
        val x1Pos  = toLabelPos(x1, x1Text)
        val labels = acc.labels ++ List(Label(tick = x0, pos = x0Pos, value = x0Text), Label(tick = x1, pos = x1Pos, value = x1Text))

        acc.copy(height = y + 1, spans = spans, labels = labels)
      }

      diagram.copy(width = width)

  def render(d: Diagram, theme: Theme): List[String] =
    val w = d.width
    val h = d.height + 2 // axis, labels

    val arr: Array[Array[Char]] = Array.fill[Char](h, w)(' ')

    // spans
    d.spans.foreach(s =>
      if s.size > 1 then
        Range(s.x0 + 1, s.x1).foreach(i => arr(s.y)(i) = theme.fill)
        arr(s.y)(s.x0) = theme.leftBound(s.includeX0)
        arr(s.y)(s.x1) = theme.rightBound(s.includeX1)
      else arr(s.y)(s.x0) = theme.fill
    )

    // separator
    Range(0, d.width).foreach(i => arr(d.height)(i) = '-')

    // ticks
    d.labels.foreach(l => arr(d.height)(l.tick) = theme.tick)

    // labels
    d.labels.foreach(l =>
      l.value.toList.zipWithIndex.foreach { case (ch, i) =>
        arr(d.height + 1)(l.pos + i) = ch
      }
    )

    arr.map(_.toList.mkString).toList
