package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domains
import com.github.gchudnov.mtg.TestSpec

final class BoundaryOrderingSpec extends TestSpec:
  import Domains.integralDomain

  given ord: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  "BoundaryOrdering" when {
    "(LeftBoundary, LeftBoundary)" should {
      "compare" in {
        // 1, 1
        ord.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (0)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (0)

        // 1, 2
        ord.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(2), isInclude = true)) mustBe (-1)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(2), isInclude = true)) mustBe (0)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(2), isInclude = false)) mustBe (-1)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(2), isInclude = false)) mustBe (-1)

        // 2, 1
        ord.compare(LeftBoundary[Int](Some(2), isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        ord.compare(LeftBoundary[Int](Some(2), isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        ord.compare(LeftBoundary[Int](Some(2), isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (0)
        ord.compare(LeftBoundary[Int](Some(2), isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (1)

        // 1, -inf
        ord.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](None, isInclude = false)) mustBe (1)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        ord.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](None, isInclude = false)) mustBe (1)

        // -inf, 1
        ord.compare(LeftBoundary[Int](None, isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (-1)
        ord.compare(LeftBoundary[Int](None, isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)
        ord.compare(LeftBoundary[Int](None, isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (-1)
        ord.compare(LeftBoundary[Int](None, isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)

        // -inf, -inf
        ord.compare(LeftBoundary[Int](None, isInclude = true), LeftBoundary[Int](None, isInclude = true)) mustBe (0)
        ord.compare(LeftBoundary[Int](None, isInclude = true), LeftBoundary[Int](None, isInclude = false)) mustBe (-1)
        ord.compare(LeftBoundary[Int](None, isInclude = false), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        ord.compare(LeftBoundary[Int](None, isInclude = false), LeftBoundary[Int](None, isInclude = false)) mustBe (0)
      }
    }
  }
