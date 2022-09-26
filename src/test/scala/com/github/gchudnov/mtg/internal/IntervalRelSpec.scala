package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domains.given

import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.BoundaryOrdering
import com.github.gchudnov.mtg.Boundary

final class IntervalRelSpec extends TestSpec:

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  "IntervalRel" when {
    "xxx" should {
      "yyy" in {
        val x = Interval.closed(1, 4)
        val y = Interval.open(3, 6)

      }
    }
  }
