package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

/**
 * Section
 */
final case class Section[T: Domain](
  name: String,
  intervals: List[Interval[T]],
)
