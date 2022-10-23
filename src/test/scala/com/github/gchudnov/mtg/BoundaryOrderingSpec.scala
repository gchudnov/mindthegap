package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

final class BoundaryOrderingSpec extends TestSpec:

  val ordB: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "BoundaryOrdering" when {
    "(Boundary.Left, Boundary.Left)" should {
      "compare" in {
        // 1, 1
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](Some(1), true)) mustBe (0)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](Some(1), false)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](Some(1), false)) mustBe (0)

        // 1, 2
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](Some(2), true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](Some(2), true)) mustBe (0)
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](Some(2), false)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](Some(2), false)) mustBe (-1)

        // 2, 1
        ordB.compare(Boundary.Left[Int](Some(2), true), Boundary.Left[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(2), false), Boundary.Left[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(2), true), Boundary.Left[Int](Some(1), false)) mustBe (0)
        ordB.compare(Boundary.Left[Int](Some(2), false), Boundary.Left[Int](Some(1), false)) mustBe (1)

        // 1, -inf
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](None, true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Left[Int](None, false)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](None, true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Left[Int](None, false)) mustBe (1)

        // -inf, 1
        ordB.compare(Boundary.Left[Int](None, true), Boundary.Left[Int](Some(1), true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, true), Boundary.Left[Int](Some(1), false)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, false), Boundary.Left[Int](Some(1), true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, false), Boundary.Left[Int](Some(1), false)) mustBe (-1)

        // -inf, -inf
        ordB.compare(Boundary.Left[Int](None, true), Boundary.Left[Int](None, true)) mustBe (0)
        ordB.compare(Boundary.Left[Int](None, true), Boundary.Left[Int](None, false)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, false), Boundary.Left[Int](None, true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](None, false), Boundary.Left[Int](None, false)) mustBe (0)
      }

      "min" in {
        // (2 vs. [3
        ordB.min(Boundary.Left(Some(2), false), Boundary.Left(Some(3), true)) mustBe Boundary.Left(Some(2), false)
      }

      "max" in {
        // (2 vs. [3
        ordB.max(Boundary.Left(Some(2), false), Boundary.Left(Some(3), true)) mustBe Boundary.Left(Some(2), false)
      }

      "equiv" in {
        // (2 vs. [3
        ordB.equiv(Boundary.Left(Some(2), false), Boundary.Left(Some(3), true)) mustBe true
      }
    }

    "(Boundary.Right, Boundary.Right)" should {
      "compare" in {
        // 1, 1
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](Some(1), true)) mustBe (0)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](Some(1), true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](Some(1), false)) mustBe (0)

        // 1, 2
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](Some(2), true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](Some(2), true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](Some(2), false)) mustBe (0)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](Some(2), false)) mustBe (-1)

        // 2, 1
        ordB.compare(Boundary.Right[Int](Some(2), true), Boundary.Right[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](Some(2), false), Boundary.Right[Int](Some(1), true)) mustBe (0)
        ordB.compare(Boundary.Right[Int](Some(2), true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        ordB.compare(Boundary.Right[Int](Some(2), false), Boundary.Right[Int](Some(1), false)) mustBe (1)

        // 1, +inf
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](None, true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Right[Int](None, false)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](None, true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Right[Int](None, false)) mustBe (-1)

        // +inf, 1
        ordB.compare(Boundary.Right[Int](None, true), Boundary.Right[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, false), Boundary.Right[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, false), Boundary.Right[Int](Some(1), false)) mustBe (1)

        // +inf, +inf
        ordB.compare(Boundary.Right[Int](None, true), Boundary.Right[Int](None, true)) mustBe (0)
        ordB.compare(Boundary.Right[Int](None, true), Boundary.Right[Int](None, false)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, false), Boundary.Right[Int](None, true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](None, false), Boundary.Right[Int](None, false)) mustBe (0)
      }
    }

    "(Boundary.Left, Boundary.Right)" should {
      "compare" in {
        // 1, 1
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(1), true)) mustBe (0)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(1), false)) mustBe (1)

        // 1, 2
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(2), true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(2), true)) mustBe (0)
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(2), false)) mustBe (0)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(2), false)) mustBe (1)

        // 2, 1
        ordB.compare(Boundary.Left[Int](Some(2), true), Boundary.Right[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(2), false), Boundary.Right[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(2), true), Boundary.Right[Int](Some(1), false)) mustBe (1)
        ordB.compare(Boundary.Left[Int](Some(2), false), Boundary.Right[Int](Some(1), false)) mustBe (1)

        // 1, +inf
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](None, true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](Some(1), true), Boundary.Right[Int](None, false)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](None, true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](Some(1), false), Boundary.Right[Int](None, false)) mustBe (-1)

        // -inf, 1
        ordB.compare(Boundary.Left[Int](None, true), Boundary.Right[Int](Some(1), true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, true), Boundary.Right[Int](Some(1), false)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, false), Boundary.Right[Int](Some(1), true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, false), Boundary.Right[Int](Some(1), false)) mustBe (-1)

        // -inf, +inf
        ordB.compare(Boundary.Left[Int](None, true), Boundary.Right[Int](None, true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, true), Boundary.Right[Int](None, false)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, false), Boundary.Right[Int](None, true)) mustBe (-1)
        ordB.compare(Boundary.Left[Int](None, false), Boundary.Right[Int](None, false)) mustBe (-1)
      }
    }

    "(Boundary.Right, Boundary.Left)" should {
      "compare" in {
        // 1, 1
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](Some(1), true)) mustBe (0)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](Some(1), true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](Some(1), false)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](Some(1), false)) mustBe (-1)

        // 1, 2
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](Some(2), true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](Some(2), true)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](Some(2), false)) mustBe (-1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](Some(2), false)) mustBe (-1)

        // 2, 1
        ordB.compare(Boundary.Right[Int](Some(2), true), Boundary.Left[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](Some(2), false), Boundary.Left[Int](Some(1), true)) mustBe (0)
        ordB.compare(Boundary.Right[Int](Some(2), true), Boundary.Left[Int](Some(1), false)) mustBe (0)
        ordB.compare(Boundary.Right[Int](Some(2), false), Boundary.Left[Int](Some(1), false)) mustBe (-1)

        // 1, -inf
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](None, true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](Some(1), true), Boundary.Left[Int](None, false)) mustBe (1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](None, true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](Some(1), false), Boundary.Left[Int](None, false)) mustBe (1)

        // +inf, 1
        ordB.compare(Boundary.Right[Int](None, true), Boundary.Left[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, true), Boundary.Left[Int](Some(1), false)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, false), Boundary.Left[Int](Some(1), true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, false), Boundary.Left[Int](Some(1), false)) mustBe (1)

        // +inf, -inf
        ordB.compare(Boundary.Right[Int](None, true), Boundary.Left[Int](None, true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, true), Boundary.Left[Int](None, false)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, false), Boundary.Left[Int](None, true)) mustBe (1)
        ordB.compare(Boundary.Right[Int](None, false), Boundary.Left[Int](None, false)) mustBe (1)
      }
    }

    "ordered" should {
      "lt" in {
        val t = Table(
          ("ba", "bb", "expected"),
          (Boundary.Left[Int](None, true), Boundary.Right[Int](None, true), true),
          (Boundary.Left[Int](None, false), Boundary.Right[Int](None, false), true),
          (Boundary.Left[Int](None, true), Boundary.Right[Int](None, false), true),
          (Boundary.Left[Int](None, false), Boundary.Right[Int](None, true), true)
        )

        forAll(t) { (ba, bb, expected) =>
          ordB.lt(ba, bb) mustBe (expected)
        }
      }

      "equiv" in {
        val t = Table(
          ("ba", "bb", "expected"),
          (Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(1), true), true),
          (Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(1), false), false),
          (Boundary.Left[Int](Some(1), true), Boundary.Right[Int](Some(1), false), false),
          (Boundary.Left[Int](Some(1), false), Boundary.Right[Int](Some(1), true), false)
        )

        forAll(t) { (ba, bb, expected) =>
          ordB.equiv(ba, bb) mustBe (expected)
        }
      }
    }
  }
