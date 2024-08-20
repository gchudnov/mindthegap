package com.github.gchudnov.mtg.diagram.internal

private[diagram] final case class GanttSection(
  title: String,
  tasks: List[GanttTask],
)
