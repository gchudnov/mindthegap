package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Diagram
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Renderer
import com.github.gchudnov.mtg.diagram.Span
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
import scala.collection.mutable.ListBuffer

/**
 * Basic Renderer
 */
private[mtg] final class BasicRenderer(theme: Theme) extends Renderer:

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
      if p >= 0 && p < view.size then view(p) = ch
    }

  private def drawTick(t: Tick, view: Array[Char]): Unit =
    if (t.pos >= 0) && (t.pos < view.size) then view(t.pos) = theme.tick

  private def drawSpan(span: Span, view: Array[Char]): Unit =
    if span.nonEmpty then
      val p = math.max(span.x0, 0)
      val q = math.min(span.x1, if view.nonEmpty then view.size - 1 else 0)

      Range.inclusive(p, q).foreach(i => view(i) = theme.fill)

      if span.size > 1 then
        if span.x0 >= 0 && span.x0 < view.size then view(span.x0) = theme.leftBound(span.includeX0)
        if span.x1 >= 0 && span.x1 < view.size then view(span.x1) = theme.rightBound(span.includeX1)

  private[mtg] def padRight(n: Int, pad: Char)(value: String): String =
    if n > 0 then value + (pad.toString * n)
    else value

  private[mtg] def padWithEmptyLines(n: Int)(xs: List[String]): List[String] =
    if (xs.size < n) && (n > 0) then xs ++ List.fill[String](n - xs.size)("")
    else xs
