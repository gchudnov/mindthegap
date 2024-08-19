package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.diagram.Diagram

private[mtg] final case class GanttDiagram(
  title: String,
  now: Option[String],
  inFormat: String,
  outFormat: String,
  sections: List[GanttSection],
)

private[mtg] object GanttDiagram {
  def make[T: Domain: InputDate](inputDiagram: Diagram[T]): GanttDiagram = {
    ???
  }
}
