package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.diagram.internal.*

/**
 * Mermaid Renderer
 *
 * Renders a diagram as a Mermaid diagram.
 */
private[mtg] final class MermaidRenderer extends Renderer:

  private val sb = new StringBuilder()

  def result: String =
    sb.toString()

  override def render[T: Domain](d: Diagram[T]): Unit =
    ???

  private def prepare[T](d: GanttDiagram): Unit = {
    sb.clear()
    
    sb.append("gantt\n")
    sb.append(s"  title       ${d.title}\n")
    sb.append(s"  dateFormat  ${d.inFormat}\n")
    sb.append(s"  axisFormat  ${d.axisFormat}\n")

    d.sections.foreach { s =>
      sb.append(s"  section ${s.title}\n")
      s.tasks.foreach { t =>
        sb.append(s"  ${t.name}  :${t.start}, ${t.end}\n")
      }
    }
  }

object MermaidRenderer:
  def make(): MermaidRenderer =
    new MermaidRenderer()

// https://mermaid.js.org/syntax/gantt.html


/* 
gantt
    title A Gantt Diagram
    dateFormat  YYYY-MM-DDTHH:mm:ss.SSS
    axisFormat  %d-%m-%Y %H:%M

    section Section
    Task 1           :a1, 2024-08-19T09:00:00.000, 2024-08-20T10:00:00.000
*/
