package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Domains
import com.github.gchudnov.mtg.TestSpec

final class BoundaryOrderingSpec extends TestSpec:
  import Domains.integralDomain
  import BoundaryOrdering.given

  val bOrd: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "BoundaryOrdering" when {
    "(LeftBoundary, LeftBoundary)" should {
      "compare" in {
        // 1, 1
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (0)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (0)

        // 1, 2
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(2), isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(2), isInclude = true)) mustBe (0)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(2), isInclude = false)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(2), isInclude = false)) mustBe (-1)

        // 2, 1
        bOrd.compare(LeftBoundary[Int](Some(2), isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(2), isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(2), isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (0)
        bOrd.compare(LeftBoundary[Int](Some(2), isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (1)

        // 1, -inf
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](None, isInclude = false)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](None, isInclude = false)) mustBe (1)

        // -inf, 1
        bOrd.compare(LeftBoundary[Int](None, isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)

        // -inf, -inf
        bOrd.compare(LeftBoundary[Int](None, isInclude = true), LeftBoundary[Int](None, isInclude = true)) mustBe (0)
        bOrd.compare(LeftBoundary[Int](None, isInclude = true), LeftBoundary[Int](None, isInclude = false)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = false), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = false), LeftBoundary[Int](None, isInclude = false)) mustBe (0)
      }
    }

    "(RightBoundary, RightBoundary)" should {
      "compare" in {
        // 1, 1
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](Some(1), isInclude = true)) mustBe (0)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](Some(1), isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](Some(1), isInclude = false)) mustBe (0)

        // 1, 2
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](Some(2), isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](Some(2), isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](Some(2), isInclude = false)) mustBe (0)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](Some(2), isInclude = false)) mustBe (-1)

        // 2, 1
        bOrd.compare(RightBoundary[Int](Some(2), isInclude = true), RightBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](Some(2), isInclude = false), RightBoundary[Int](Some(1), isInclude = true)) mustBe (0)
        bOrd.compare(RightBoundary[Int](Some(2), isInclude = true), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)
        bOrd.compare(RightBoundary[Int](Some(2), isInclude = false), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)

        // 1, +inf
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](None, isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](None, isInclude = false)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](None, isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](None, isInclude = false)) mustBe (-1)

        // +inf, 1
        bOrd.compare(RightBoundary[Int](None, isInclude = true), RightBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = true), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = false), RightBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = false), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)

        // +inf, +inf
        bOrd.compare(RightBoundary[Int](None, isInclude = true), RightBoundary[Int](None, isInclude = true)) mustBe (0)
        bOrd.compare(RightBoundary[Int](None, isInclude = true), RightBoundary[Int](None, isInclude = false)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = false), RightBoundary[Int](None, isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](None, isInclude = false), RightBoundary[Int](None, isInclude = false)) mustBe (0)
      }
    }

    "(LeftBoundary, RightBoundary)" should {
      "compare" in {
        // 1, 1
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](Some(1), isInclude = true)) mustBe (0)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)

        // 1, 2
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](Some(2), isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](Some(2), isInclude = true)) mustBe (0)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](Some(2), isInclude = false)) mustBe (0)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](Some(2), isInclude = false)) mustBe (1)

        // 2, 1
        bOrd.compare(LeftBoundary[Int](Some(2), isInclude = true), RightBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(2), isInclude = false), RightBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(2), isInclude = true), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)
        bOrd.compare(LeftBoundary[Int](Some(2), isInclude = false), RightBoundary[Int](Some(1), isInclude = false)) mustBe (1)

        // 1, +inf
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](None, isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = true), RightBoundary[Int](None, isInclude = false)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](None, isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](Some(1), isInclude = false), RightBoundary[Int](None, isInclude = false)) mustBe (-1)

        // -inf, 1
        bOrd.compare(LeftBoundary[Int](None, isInclude = true), RightBoundary[Int](Some(1), isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = true), RightBoundary[Int](Some(1), isInclude = false)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = false), RightBoundary[Int](Some(1), isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = false), RightBoundary[Int](Some(1), isInclude = false)) mustBe (-1)

        // -inf, +inf
        bOrd.compare(LeftBoundary[Int](None, isInclude = true), RightBoundary[Int](None, isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = true), RightBoundary[Int](None, isInclude = false)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = false), RightBoundary[Int](None, isInclude = true)) mustBe (-1)
        bOrd.compare(LeftBoundary[Int](None, isInclude = false), RightBoundary[Int](None, isInclude = false)) mustBe (-1)
      }
    }

    "(RightBoundary, LeftBoundary)" should {
      "compare" in {
        // 1, 1
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (0)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)

        // 1, 2
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(2), isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(2), isInclude = true)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](Some(2), isInclude = false)) mustBe (-1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](Some(2), isInclude = false)) mustBe (-1)

        // 2, 1
        bOrd.compare(RightBoundary[Int](Some(2), isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](Some(2), isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (0)
        bOrd.compare(RightBoundary[Int](Some(2), isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (0)
        bOrd.compare(RightBoundary[Int](Some(2), isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (-1)

        // 1, -inf
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = true), LeftBoundary[Int](None, isInclude = false)) mustBe (1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](Some(1), isInclude = false), LeftBoundary[Int](None, isInclude = false)) mustBe (1)

        // +inf, 1
        bOrd.compare(RightBoundary[Int](None, isInclude = true), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = true), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = false), LeftBoundary[Int](Some(1), isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = false), LeftBoundary[Int](Some(1), isInclude = false)) mustBe (1)

        // +inf, -inf
        bOrd.compare(RightBoundary[Int](None, isInclude = true), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = true), LeftBoundary[Int](None, isInclude = false)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = false), LeftBoundary[Int](None, isInclude = true)) mustBe (1)
        bOrd.compare(RightBoundary[Int](None, isInclude = false), LeftBoundary[Int](None, isInclude = false)) mustBe (1)
      }
    }
  }
