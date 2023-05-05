package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Diagram
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.diagram.Renderer
import com.github.gchudnov.mtg.diagram.Span
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
import scala.collection.mutable.ListBuffer

/**
 * ASCII Renderer
 */
private[mtg] final class AsciiRenderer() extends Renderer:

  override def render(d: Diagram, theme: Theme): List[String] =
    val spans  = drawSpans(theme, d.spans, d.width)
    val ticks  = drawTicks(theme, d.ticks, d.width)
    val labels = drawLabels(theme, d.labels, d.width)

    val chart = (spans ++ ticks ++ labels)

    // format: "span | legend : annotation"
    val legends     = padWithEmptyLines(chart.size)(d.legends.map(_.value))
    val annotations = padWithEmptyLines(chart.size)(d.annotations.map(_.value))

    val addLegends     = theme.legend && legends.exists(_.nonEmpty)
    val addAnnotations = theme.annotations && annotations.exists(_.nonEmpty)

    // with borders
    val withBorder =
      if addLegends || addAnnotations then chart.map(line => if line.nonEmpty then s"${line}${theme.space}${theme.border}" else line)
      else chart

    // with legend
    val withLegend =
      if addLegends then withBorder.zip(legends).map { case (line, legend) => if line.nonEmpty && legend.nonEmpty then s"${line}${theme.space}${legend}" else line }
      else withBorder

    // with annotations
    val annotated =
      if addAnnotations then
        val maxLineSize = withLegend.map(_.size).maxOption.getOrElse(0)
        withLegend.zip(annotations).map { case (line, annotation) =>
          if line.nonEmpty && annotation.nonEmpty then
            val padSize    = maxLineSize - line.size
            val paddedLine = padRight(padSize, theme.space)(line)
            val separator  = if addLegends then s"${theme.space}${theme.comment}" else ""
            s"${paddedLine}${separator}${theme.space}${annotation}"
          else line
        }
      else withLegend

    annotated.filter(_.nonEmpty)

  private[mtg] def drawSpans(theme: Theme, spans: List[Span], width: Int): List[String] =
    val views: Array[Array[Char]] = Array.fill[Char](spans.size, width)(theme.space)
    spans.zipWithIndex.foreach((span, i) => drawSpan(theme, span, views(i)))
    views.map(_.mkString).toList

  private[mtg] def drawTicks(theme: Theme, ts: List[Tick], width: Int): List[String] =
    val view = Array.fill[Char](width)(theme.axis)
    ts.sortBy(_.posX).foreach(t => drawTick(theme, t, view))
    List(view.mkString)

  private[mtg] def drawLabels(theme: Theme, ls: List[Label], width: Int): List[String] =
    theme.label match
      case Theme.Label.None =>
        drawLabelsNone(theme, ls, width)
      case Theme.Label.NoOverlap =>
        drawLabelsNoOverlap(theme, ls, width)
      case Theme.Label.Stacked =>
        drawLabelsStacked(theme, ls, width)

  private def drawLabelsNone(theme: Theme, ls: List[Label], width: Int): List[String] =
    val view = Array.fill[Char](width)(theme.space)
    ls.sortBy(_.posX).foreach(l => drawLabel(theme, l, view))
    List(view.mkString)

  private def drawLabelsNoOverlap(theme: Theme, ls: List[Label], width: Int): List[String] =
    val view = Array.fill[Char](width)(theme.space)
    ls.sortBy(_.posX)
      .foldLeft(-1)((last, l) =>
        if (l.posX > last) || (l.posX > 0 && l.posX == last && (!view(l.posX - 1).isDigit || (l.value.nonEmpty && !l.value(0).isDigit))) then
          drawLabel(theme, l, view)
          l.posX + l.value.size
        else last
      )
    List(view.mkString)

  private def drawLabelsStacked(theme: Theme, ls: List[Label], width: Int): List[String] =
    val emptyState = (Vector(Array.fill[Char](width)(theme.space)), ListBuffer[Int](-1))
    val res = ls
      .sortBy(_.posX)
      .foldLeft(emptyState)((acc, l) =>
        val p = l.posX
        val q = l.posX + l.size

        val views = acc._1
        val last  = acc._2

        val indices = last.indices
        val i       = indices.find(i => (p > last(i) && q <= width))

        i match
          case None =>
            val view = Array.fill[Char](width)(theme.space)
            drawLabel(theme, l, view)
            (views :+ view, last.addOne(q))
          case Some(j) =>
            val view = views(j)
            drawLabel(theme, l, view)
            last(j) = q
            (views, last)
      )

    res._1.map(_.mkString).toList

  private def drawLabel(theme: Theme, l: Label, spot: Array[Char]): Unit =
    l.value.toList.zipWithIndex.foreach { case (ch, i) =>
      val p = l.posX + i
      if p >= 0 && p < spot.size then spot(p) = ch
    }

  private def drawTick(theme: Theme, t: Tick, spot: Array[Char]): Unit =
    if (t.posX >= 0) && (t.posX < spot.size) then spot(t.posX) = theme.tick

  private def drawSpan(theme: Theme, span: Span, spot: Array[Char]): Unit =
    if span.nonEmpty then
      val p = math.max(span.x0, 0)
      val q = math.min(span.x1, if spot.nonEmpty then spot.size - 1 else 0)

      Range.inclusive(p, q).foreach(i => spot(i) = theme.fill)

      if span.size > 1 then
        if span.x0 >= 0 && span.x0 < spot.size then spot(span.x0) = theme.leftBound(span.includeX0)
        if span.x1 >= 0 && span.x1 < spot.size then spot(span.x1) = theme.rightBound(span.includeX1)

  private[mtg] def padRight(n: Int, pad: Char)(value: String): String =
    if n > 0 then value + (pad.toString * n)
    else value

  private[mtg] def padWithEmptyLines(n: Int)(xs: List[String]): List[String] =
    if (xs.size < n) && (n > 0) then xs ++ List.fill[String](n - xs.size)("")
    else xs
