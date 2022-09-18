package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domains
import com.github.gchudnov.mtg.TestSpec

final class BoundaryOrderingSpec extends TestSpec:
  import Domains.integralDomain

  given ord: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  "BoundaryOrdering" when {
    "(LeftBoundary, LeftBoundary)" should {
      "compare" in {
        ord.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe(0)

      }
    }
  }
