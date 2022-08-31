package com.github.gchudnov.mtg.types

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Timeline
import com.github.gchudnov.mtg.orderings.given

final case class IntTimeline(intervals: List[Interval[Int]]) extends Timeline[Int]
