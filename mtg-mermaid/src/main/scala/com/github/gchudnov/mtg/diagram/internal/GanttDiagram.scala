package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.diagram.Diagram

private[diagram] final case class GanttDiagram(
  title: String,
  now: Option[String],
  inFormat: String,
  axisFormat: String,
  sections: List[GanttSection],
)

private[diagram] object GanttDiagram {
  def make[T: Domain: InputFormat](inputDiagram: Diagram[T]): GanttDiagram = {
    ???
  }
}
