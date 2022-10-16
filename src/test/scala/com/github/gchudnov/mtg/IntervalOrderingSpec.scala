package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec

final class IntervalOrderingSpec extends TestSpec:

  "IntervalOrdering" when {
    "two intervals with before-relation" should {
      "be ordered" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(20, 30)

        List(b, a).sorted mustBe List(a, b)
      }
    }

    "two intervals with meet-relation" should {
      "be ordered" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(20, 30)

        List(b, a).sorted mustBe List(a, b)
      }
    }

    "two intervals with overlaps-relation" should {
      "be ordered" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(5, 15)

        List(b, a).sorted mustBe List(a, b)
      }
    }

    "empty intervals" should {
      "be compared" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        summon[Ordering[Interval[Int]]].compare(a, b) mustBe 0
      }
    }

    "empty and non-empty intervals" should {
      "be compared" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(0, 10)

        summon[Ordering[Interval[Int]]].compare(a, b) mustBe -1
      }
    }

    "non-empty and empty intervals" should {
      "be compared" in {
        val a = Interval.closed(0, 10)
        val b = Interval.empty[Int]

        summon[Ordering[Interval[Int]]].compare(a, b) mustBe 1
      }
    }
  }
