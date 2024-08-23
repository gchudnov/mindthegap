package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.diagram.internal.*

/**
 * Mermaid Renderer
 *
 * Renders a diagram as a Mermaid diagram.
 *
 * https://mermaid.js.org/syntax/gantt.html
 */
final class MermaidRenderer[T](using I: InputFormat[T], O: OutputFormat[T]) extends Renderer[T]:

  private val sb = new StringBuilder()

  def result: String =
    sb.toString()

  override def render(d: Diagram[T]): Unit =
    val gantt = toGanttDiagram(d)
    write(gantt)

  private def write(d: GanttDiagram): Unit =
    sb.clear()

    val title = if d.title.isEmpty then "<no title>" else d.title

    sb.append("gantt\n")
    sb.append(s"  title       ${title}\n")
    sb.append(s"  dateFormat  ${d.inFormat}\n")
    sb.append(s"  axisFormat  ${d.axisFormat}\n")

    sb.append("\n")

    val maxTaskNameLen = d.sections.flatMap(_.tasks).map(_.name.length).maxOption.getOrElse(0)

    d.sections.foreach { s =>
      val title = if s.title.isEmpty then "<no title>" else s.title
      sb.append(s"  section ${title}\n")
      s.tasks.foreach { t =>
        val padding = " " * (maxTaskNameLen - t.name.length)
        sb.append(s"  ${t.name}${padding}  :${t.start}, ${t.end}\n")
      }
      sb.append("\n")
    }

  private def toGanttDiagram(d: Diagram[T]): GanttDiagram =
    val sections = d.sections.map { s =>
      val tasks = s.intervals.zipWithIndex.map { (i, j) =>
        val name = if j < s.annotations.size then s.annotations(j) else ""
        val left =
          i.left.map(x => I.format(x)).getOrElse(throw new UnsupportedOperationException(s"Left endpoint of an interval is missing"))
        val right =
          i.right.map(x => I.format(x)).getOrElse(throw new UnsupportedOperationException(s"Right endpoint of an interval is missing"))

        GanttTask(
          name = name,
          start = left,
          end = right,
        )
      }
      GanttSection(
        title = s.title,
        tasks = tasks,
      )
    }

    GanttDiagram(
      title = d.title,
      now = None,
      inFormat = I.pattern,
      axisFormat = O.pattern,
      sections = sections,
    )

object MermaidRenderer:
  def make[T: InputFormat: OutputFormat]: MermaidRenderer[T] =
    new MermaidRenderer[T]
