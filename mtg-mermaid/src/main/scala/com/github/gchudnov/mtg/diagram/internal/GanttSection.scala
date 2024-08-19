package com.github.gchudnov.mtg.diagram.internal

private[internal] final case class GanttSection(
  title: String,
  tasks: List[GanttTask],
)
