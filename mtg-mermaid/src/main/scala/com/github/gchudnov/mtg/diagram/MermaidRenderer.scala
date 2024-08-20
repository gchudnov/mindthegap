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
private[mtg] final class MermaidRenderer[T](using I: InputFormat[T], O: OutputFormat[T]) extends Renderer[T]:

  private val sb = new StringBuilder()

  def result: String =
    sb.toString()

  override def render(d: Diagram[T]): Unit =
    ???

  private def write(d: GanttDiagram): Unit = {
    sb.clear()
    
    sb.append("gantt\n")
    sb.append(s"  title       ${d.title}\n")
    sb.append(s"  dateFormat  ${d.inFormat}\n")
    sb.append(s"  axisFormat  ${d.axisFormat}\n")

    sb.append("\n")

    d.sections.foreach { s =>
      sb.append(s"  section ${s.title}\n")
      s.tasks.foreach { t =>
        sb.append(s"  ${t.name}  :${t.start}, ${t.end}\n")
      }
      sb.append("\n")
    }
  }

object MermaidRenderer:
  def make[T: InputFormat: OutputFormat]: MermaidRenderer[T] =
    new MermaidRenderer[T]

// 


/* 
gantt
    title A Gantt Diagram
    dateFormat  YYYY-MM-DDTHH:mm:ss.SSS
    axisFormat  %d-%m-%Y %H:%M

    section Section
    Task 1           :a1, 2024-08-19T09:00:00.000, 2024-08-20T10:00:00.000
*/
