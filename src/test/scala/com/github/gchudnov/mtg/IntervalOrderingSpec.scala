package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Domains
import com.github.gchudnov.mtg.TestSpec

final class IntervalOrderingSpec extends TestSpec:
  import Domains.integralDomain

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]
  given iOrd: Ordering[Interval[Int]] = IntervalOrdering.intervalOrdering[Int]

  "IntervalOrdering" when {
    "two intervals with before (b) relation" should {
      "be ordered" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(20, 30)

        List(b, a).sorted mustBe List(a, b)
      }
    }

    "two intervals with meet (m) relation" should {
      "be ordered" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(20, 30)

        List(b, a).sorted mustBe List(a, b)
      }
    }

    "two intervals with overlaps (m) relation" should {
      "be ordered" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(5, 15)

        List(b, a).sorted mustBe List(a, b)
      }
    }

    "empty and non-empty intervals" should {
      "be compared" in {
        val a = Interval.closed(0, 10)
        val b = Interval.empty[Int]

        iOrd.compare(a, b) mustBe (1)
        iOrd.compare(b, a) mustBe (1)
      }
    }
  }
