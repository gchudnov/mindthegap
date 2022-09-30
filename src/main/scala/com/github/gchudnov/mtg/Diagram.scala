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

  final case class Theme(
    padding: Int,
    sep: Char,
    axis: Char,
    tick: Char
  )

  object Theme:
    val default: Theme =
      Theme(
        padding = 2,
        sep = '*',
        axis = '-',
        tick = '+'
      )

  final case class Label(tick: Int, pos: Int, value: String)

  final case class SpanStyle(start: Char, end: Char)

  final case class Span(x0: Int, x1: Int, y: Int, style: SpanStyle)

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

      val optfMin = bs.filter(_.isBounded).minOption // != -inf
      val optfMax = bs.filter(_.isBounded).maxOption // != +inf

      val cxMinInf = 0
      val cxMaxInf = width - 1
      val cxMin    = cxMinInf + padding
      val cxMax    = cxMaxInf - padding

      val cw = cxMax - cxMin
      val fw = optfMin.flatMap(fMin => optfMax.map(fMax => tNum.minus(fMax.value.get, fMin.value.get)))
      val ok = fw.map(w => cw.toDouble / tNum.toDouble(w))

      // translates the coordindate into position on the canvas
      def translateX(value: Option[T], bound: Either[Unit, Unit]): Int =
        value.fold(bound.fold(_ => cxMinInf, _ => cxMaxInf))(x => ok.fold(bound.fold(_ => cxMin, _ => cxMax))(k => (k * tNum.toDouble(x) + cxMin).toInt))

      def toStyle[T](i: Interval[T]): SpanStyle =
        SpanStyle(start = Show.leftBound(i.left.isInclude), end = Show.rightBound(i.right.isInclude))

      def toLabelPos(x: Int, text: String): Int =
        val p = x - (text.size / 2)
        if p < 0 then 0
        else if p + text.size - 1 > width then width - text.size
        else p

      val diagram = xs.foldLeft(Diagram.empty) { case (acc, i) =>
        val y = acc.height

        // spans
        val x0    = translateX(i.left.value, Left(()))
        val x1    = translateX(i.right.value, Right(()))
        val spans = acc.spans :+ Span(x0 = x0, x1 = x1, y = y, style = toStyle(i))

        // labels
        val x0Text = Show.leftValue(i.left.value)
        val x0Pos  = toLabelPos(x0, x0Text)
        val x1Text = Show.rightValue(i.right.value)
        val x1Pos  = toLabelPos(x1, x1Text)
        val labels = acc.labels ++ List(Label(tick = x0, pos = x0Pos, value = x0Text), Label(tick = x1, pos = x1Pos, value = x1Text))

        acc.copy(height = y + 1, spans = spans, labels = labels)
      }

      // remove overlapping and invisible spans
      // TODO: do it

      diagram.copy(width = width)

  def render(d: Diagram, theme: Theme): List[String] =
    val w = d.width
    val h = d.height + 2 // axis, labels

    val arr: Array[Array[Char]] = Array.fill[Char](h, w)(' ')

    // spans
    

    // separator
    Range(0, d.width).foreach(i => arr(d.height)(i) = '-')

    // ticks
    d.labels.foreach(l => arr(d.height)(l.tick) = theme.tick)

    // labels
    d.labels.foreach(l => {
      l.value.toList.zipWithIndex.foreach({
        case (ch, i) => arr(d.height + 1)(l.pos + i) = ch
      })
    })

    arr.map(_.toList.mkString).toList
