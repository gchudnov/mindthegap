package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec

final class IntervalOrderingSpec extends TestSpec:

  val ordM: Ordering[Mark[Int]]     = summon[Ordering[Mark[Int]]]
  val ordI: Ordering[Interval[Int]] = summon[Ordering[Interval[Int]]]

  "IntervalOrdering" when {

    "the same interval represented in different ways" should {
      "be equivalent" in {
        // (3, 5)
        val a = Interval.make(Mark.succ(3), Mark.pred(5))

        // [4, 4]
        val b = Interval.make(Mark.at(4), Mark.at(4))

        // (3, 4]
        val c = Interval.make(Mark.succ(3), Mark.at(4))

        // [4, 5)
        val d = Interval.make(Mark.at(4), Mark.pred(5))

        ordI.equiv(a, a) mustBe (true)
        ordI.equiv(a, b) mustBe (true)
        ordI.equiv(a, c) mustBe (true)
        ordI.equiv(a, d) mustBe (true)

        ordI.equiv(b, a) mustBe (true)
        ordI.equiv(b, b) mustBe (true)
        ordI.equiv(b, c) mustBe (true)
        ordI.equiv(b, d) mustBe (true)

        ordI.equiv(c, a) mustBe (true)
        ordI.equiv(c, b) mustBe (true)
        ordI.equiv(c, c) mustBe (true)
        ordI.equiv(c, d) mustBe (true)

        ordI.equiv(d, a) mustBe (true)
        ordI.equiv(d, b) mustBe (true)
        ordI.equiv(d, c) mustBe (true)
        ordI.equiv(d, d) mustBe (true)
      }
    }

    "a collection of intervals" should {
      "be ordered" in {
        val pr5_15    = Interval.closed(5, 15)
        val pr10_20   = Interval.closed(0, 10)
        val pr20_30   = Interval.closed(20, 30)
        val inf       = Interval.unbounded[Int]
        val negInf    = Interval.empty[Int]
        val neg20_10  = Interval.closed(10, 20).swap
        val neg_10_20 = Interval.closed(-20, -10).swap

        val actual   = List(pr5_15, pr10_20, pr20_30, inf, negInf, neg20_10, neg_10_20).sorted
        val expected = List(inf, neg_10_20, pr10_20, pr5_15, pr20_30, neg20_10, negInf)

        actual must contain theSameElementsInOrderAs (expected)
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

        summon[Ordering[Interval[Int]]].compare(a, b) mustBe 1
      }
    }

    "non-empty and empty intervals" should {
      "be compared" in {
        val a = Interval.closed(0, 10)
        val b = Interval.empty[Int]

        summon[Ordering[Interval[Int]]].compare(a, b) mustBe -1
      }
    }
  }
