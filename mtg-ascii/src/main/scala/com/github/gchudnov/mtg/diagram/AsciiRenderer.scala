package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.diagram.internal.*
import com.github.gchudnov.mtg.diagram.*
import scala.collection.mutable.ListBuffer
import scala.annotation.nowarn

/**
 * ASCII Renderer
 *
 * Renders a diagram as ASCII text.
 *
 * @param theme
 *   the theme to use
 */
private[diagram] final class AsciiRenderer[T](theme: AsciiTheme, canvas: AsciiCanvas)(using D: Domain[T]) extends Renderer[T]:

  private val resultLines: ListBuffer[String] = ListBuffer.empty[String]

  /**
   * The result of the rendering.
   */
  def result: String =
    resultLines.toList.mkString("\n")

  override def render(d: Diagram[T]): Unit =
    val ad = toAsciiDiagram(d)
    draw(ad)

  private def draw(ad: AsciiDiagram): Unit =
    val spans  = drawSpans(ad.spans, ad.width)
    val ticks  = drawTicks(ad.ticks, ad.width)
    val labels = drawLabels(ad.labels, ad.width)

    val chart = (spans ++ ticks ++ labels)

    // format: "span | legend : annotation"
    val legends     = padWithEmptyLines(chart.size)(ad.legends.map(_.value))
    val annotations = padWithEmptyLines(chart.size)(ad.annotations.map(_.value))

    val addLegends     = theme.hasLegend && legends.exists(_.nonEmpty)
    val addAnnotations = theme.hasAnnotations && annotations.exists(_.nonEmpty)

    // with borders
    val withBorder =
      if addLegends || addAnnotations then chart.map(line => if line.nonEmpty then s"${line}${theme.space}${theme.border}" else line)
      else chart

    // with legend
    val withLegend =
      if addLegends then
        withBorder.zip(legends).map { case (line, legend) =>
          if line.nonEmpty && legend.nonEmpty then s"${line}${theme.space}${legend}" else line
        }
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

    resultLines.clear()
    resultLines ++= annotated.filter(_.nonEmpty)

    result

  private def toAsciiDiagram(d: Diagram[T]): AsciiDiagram =
    AsciiDiagram.from(d, canvas)

  private[mtg] def drawSpans(spans: List[AsciiSpan], width: Int): List[String] =
    val views: Array[Array[Char]] = Array.fill[Char](spans.size, width)(theme.space)
    spans.zipWithIndex.foreach((span, i) => drawSpanInPlace(span, views(i)))
    views.map(_.mkString).toList

  private def drawSpanInPlace(span: AsciiSpan, spot: Array[Char]): Unit =
    if span.nonEmpty then
      val p = math.max(span.x0, 0)
      val q = math.min(span.x1, if spot.nonEmpty then spot.size - 1 else 0)

      Range.inclusive(p, q).foreach(i => spot(i) = theme.fill)

      if span.size > 1 then
        if span.x0 >= 0 && span.x0 < spot.size then spot(span.x0) = theme.leftBoundary(span.includeX0)
        if span.x1 >= 0 && span.x1 < spot.size then spot(span.x1) = theme.rightBoundary(span.includeX1)

  private[mtg] def drawTicks(ts: List[AsciiTick], width: Int): List[String] =
    val view = Array.fill[Char](width)(theme.axis)
    ts.sortBy(_.x).foreach(t => drawTickInPlace(t, view))
    List(view.mkString)

  private def drawTickInPlace(t: AsciiTick, spot: Array[Char]): Unit =
    if (t.x >= 0) && (t.x < spot.size) then spot(t.x) = theme.tick

  private[mtg] def drawLabels(ls: List[AsciiLabel], width: Int): List[String] =
    theme.labelPosition match
      case AsciiLabelPosition.None =>
        drawLabelsNone(ls, width)
      case AsciiLabelPosition.NoOverlap =>
        drawLabelsNoOverlap(ls, width)
      case AsciiLabelPosition.Stacked =>
        drawLabelsStacked(ls, width)

  private def drawLabelsNone(ls: List[AsciiLabel], width: Int): List[String] =
    val view = Array.fill[Char](width)(theme.space)
    ls.sortBy(_.x).foreach(l => drawLabelInPlace(l, view))
    List(view.mkString)

  private def drawLabelsNoOverlap(ls: List[AsciiLabel], width: Int): List[String] =
    val view = Array.fill[Char](width)(theme.space)
    ls.sortBy(_.x)
      .foldLeft(-1)((last, l) =>
        if (l.x > last) || (l.x > 0 && l.x == last && (!view(l.x - 1).isDigit || (l.value.nonEmpty && !l.value(0).isDigit))) then
          drawLabelInPlace(l, view)
          l.x + l.value.size
        else last
      )
    List(view.mkString)

  private def drawLabelsStacked(ls: List[AsciiLabel], width: Int): List[String] =
    val emptyState = (Vector(Array.fill[Char](width)(theme.space)), ListBuffer[Int](-1))
    val res = ls
      .sortBy(_.x)
      .foldLeft(emptyState)((acc, l) =>
        val p = l.x
        val q = l.x + l.size

        val views = acc._1
        val last  = acc._2

        val indices = last.indices
        val i       = indices.find(i => (p > last(i) && q <= width))

        i match
          case None =>
            val view = Array.fill[Char](width)(theme.space)
            drawLabelInPlace(l, view)
            (views :+ view, last.addOne(q))
          case Some(j) =>
            val view = views(j)
            drawLabelInPlace(l, view)
            last(j) = q
            (views, last)
      )

    res._1.map(_.mkString).toList

  private def drawLabelInPlace(l: AsciiLabel, spot: Array[Char]): Unit =
    l.value.toList.zipWithIndex.foreach { case (ch, i) =>
      val p = l.x + i
      if p >= 0 && p < spot.size then spot(p) = ch
    }

  /**
   * Pad the list with empty lines if the size is less than n.
   *
   * @param n
   *   the total number of lines
   * @param xs
   *   the list of lines
   * @return
   *   the list of lines padded with empty lines
   */
  private[mtg] def padWithEmptyLines(n: Int)(xs: List[String]): List[String] =
    if (xs.size < n) && (n > 0) then xs ++ List.fill[String](n - xs.size)("")
    else xs

  /**
   * Pad the string with a character on the right.
   * @param n
   *   the number of characters to pad
   * @param pad
   *   the character to pad with
   * @param value
   *   the value to pad
   * @return
   *   the padded value
   */
  private[mtg] def padRight(n: Int, pad: Char)(value: String): String =
    if n > 0 then value + (pad.toString * n)
    else value

object AsciiRenderer:
  def make[T: Domain](theme: AsciiTheme = AsciiTheme.default, canvas: AsciiCanvas = AsciiCanvas.default): AsciiRenderer[T] =
    new AsciiRenderer[T](theme, canvas)
