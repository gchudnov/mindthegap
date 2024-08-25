package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain

private[diagram] final case class GanttDiagram(
  title: String,
  inFormat: String,
  axisFormat: String,
  sections: List[GanttSection],
)
