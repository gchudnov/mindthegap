package com.github.gchudnov.mtg.types

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.orderings.given
import java.time.Instant

final case class InstantInterval(x1: Option[Instant], x2: Option[Instant]) extends Interval[Instant]
