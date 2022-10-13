package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec

final class BoundaryOrderingSpec extends TestSpec:

  val bOrd: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "BoundaryOrdering" when {
    "(Boundary.Left, Boundary.Left)" should {
      "compare" in {
        // 1, 1
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](Some(1), true)) mustBe (0)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](Some(1), false)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](Some(1), false)) mustBe (0)

        // 1, 2
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](Some(2), true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](Some(2), true)) mustBe (0)
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](Some(2), false)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](Some(2), false)) mustBe (-1)

        // 2, 1
        bOrd.compare(Boundary.Left[Int](Some(2), true), Boundary.Left[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(2), false), Boundary.Left[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(2), true), Boundary.Left[Int](Some(1), false)) mustBe (0)
        bOrd.compare(Boundary.Left[Int](Some(2), false), Boundary.Left[Int](Some(1), false)) mustBe (1)

        // 1, -inf
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](None, true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](None, false)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](None, true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](None, false)) mustBe (1)

        // -inf, 1
        bOrd.compare(Boundary.Left[Int](None, true), Boundary.Left[Int](Some(1), true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, true), Boundary.Left[Int](Some(1), false)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, false), Boundary.Left[Int](Some(1), true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, false), Boundary.Left[Int](Some(1), false)) mustBe (-1)

        // -inf, -inf
        bOrd.compare(Boundary.Left[Int](None, true), Boundary.Left[Int](None, true)) mustBe (0)
        bOrd.compare(Boundary.Left[Int](None, true), Boundary.Left[Int](None, false)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, false), Boundary.Left[Int](None, true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](None, false), Boundary.Left[Int](None, false)) mustBe (0)
      }
    }

    "(Boundary.Right, Boundary.Right)" should {
      "compare" in {
        // 1, 1
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](Some(1), true)) mustBe (0)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](Some(1), true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](Some(1), false)) mustBe (0)

        // 1, 2
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](Some(2), true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](Some(2), true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](Some(2), false)) mustBe (0)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](Some(2), false)) mustBe (-1)

        // 2, 1
        bOrd.compare(Boundary.Right[Int](Some(2), true), Boundary.Right[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](Some(2), false), Boundary.Right[Int](Some(1), true)) mustBe (0)
        bOrd.compare(Boundary.Right[Int](Some(2), true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](Some(2), false), Boundary.Right[Int](Some(1), false)) mustBe (1)

        // 1, +inf
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](None, true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](None, false)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](None, true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](None, false)) mustBe (-1)

        // +inf, 1
        bOrd.compare(Boundary.Right[Int](None, true), Boundary.Right[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, false), Boundary.Right[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, false), Boundary.Right[Int](Some(1), false)) mustBe (1)

        // +inf, +inf
        bOrd.compare(Boundary.Right[Int](None, true), Boundary.Right[Int](None, true)) mustBe (0)
        bOrd.compare(Boundary.Right[Int](None, true), Boundary.Right[Int](None, false)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, false), Boundary.Right[Int](None, true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](None, false), Boundary.Right[Int](None, false)) mustBe (0)
      }
    }

    "(Boundary.Left, Boundary.Right)" should {
      "compare" in {
        // 1, 1
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(1), true)) mustBe (0)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(1), false)) mustBe (1)

        // 1, 2
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(2), true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(2), true)) mustBe (0)
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(2), false)) mustBe (0)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(2), false)) mustBe (1)

        // 2, 1
        bOrd.compare(Boundary.Left[Int](Some(2), true), Boundary.Right[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(2), false), Boundary.Right[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(2), true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        bOrd.compare(Boundary.Left[Int](Some(2), false), Boundary.Right[Int](Some(1), false)) mustBe (1)

        // 1, +inf
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](None, true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](None, false)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](None, true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](None, false)) mustBe (-1)

        // -inf, 1
        bOrd.compare(Boundary.Left[Int](None, true), Boundary.Right[Int](Some(1), true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, true), Boundary.Right[Int](Some(1), false)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, false), Boundary.Right[Int](Some(1), true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, false), Boundary.Right[Int](Some(1), false)) mustBe (-1)

        // -inf, +inf
        bOrd.compare(Boundary.Left[Int](None, true), Boundary.Right[Int](None, true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, true), Boundary.Right[Int](None, false)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, false), Boundary.Right[Int](None, true)) mustBe (-1)
        bOrd.compare(Boundary.Left[Int](None, false), Boundary.Right[Int](None, false)) mustBe (-1)
      }
    }

    "(Boundary.Right, Boundary.Left)" should {
      "compare" in {
        // 1, 1
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](Some(1), true)) mustBe (0)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](Some(1), true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](Some(1), false)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](Some(1), false)) mustBe (-1)

        // 1, 2
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](Some(2), true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](Some(2), true)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](Some(2), false)) mustBe (-1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](Some(2), false)) mustBe (-1)

        // 2, 1
        bOrd.compare(Boundary.Right[Int](Some(2), true), Boundary.Left[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](Some(2), false), Boundary.Left[Int](Some(1), true)) mustBe (0)
        bOrd.compare(Boundary.Right[Int](Some(2), true), Boundary.Left[Int](Some(1), false)) mustBe (0)
        bOrd.compare(Boundary.Right[Int](Some(2), false), Boundary.Left[Int](Some(1), false)) mustBe (-1)

        // 1, -inf
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](None, true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](None, false)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](None, true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](None, false)) mustBe (1)

        // +inf, 1
        bOrd.compare(Boundary.Right[Int](None, true), Boundary.Left[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, true), Boundary.Left[Int](Some(1), false)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, false), Boundary.Left[Int](Some(1), true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, false), Boundary.Left[Int](Some(1), false)) mustBe (1)

        // +inf, -inf
        bOrd.compare(Boundary.Right[Int](None, true), Boundary.Left[Int](None, true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, true), Boundary.Left[Int](None, false)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, false), Boundary.Left[Int](None, true)) mustBe (1)
        bOrd.compare(Boundary.Right[Int](None, false), Boundary.Left[Int](None, false)) mustBe (1)
      }
    }

    "equiv is tested" should {
      "check" in {
        val ab: Option[Int] = None
        val bb: Option[Int] = None

        val ai = true
        val bi = true

        bOrd.equiv(Boundary.Left(ab, ai), Boundary.Right(bb, bi)) mustBe (true)
      }
    }
  }
