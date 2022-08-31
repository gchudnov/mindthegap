package com.github.gchudnov.mtg.types

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.orderings.given

final case class IntInterval(x1: Option[Int], x2: Option[Int]) extends Interval[Int]
