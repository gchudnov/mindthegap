package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Interval

/**
 * Section
 */
final case class Section[T](
  name: String,
  intervals: List[Interval[T]],
)
