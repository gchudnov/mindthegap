package com.github.gchudnov.mtg.types

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Timeline
import com.github.gchudnov.mtg.orderings.given

import java.time.Instant

final case class InstantTimeline(intervals: List[Interval[Instant]]) extends Timeline[Instant]
