package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Intervals

final class MinusSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Minus" when {
    "a.minus(b)" should {
      "∅ if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.minus(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.minus(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "A if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.minus(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[a-, a+] if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.minus(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[a-, a+] if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.minus(b)
        val expected = Interval.closed(20, 30)

        actual mustBe expected
      }

      "∅ if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.minus(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.minus(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.minus(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.minus(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "[max(succ(b+), a-), a+] if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.minus(b)
        val expected = Interval.closed(8, 10)

        actual mustBe expected
      }

      "[max(succ(b+), a-), a+] if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.minus(b)
        val expected = Interval.closed(6, 10)

        actual mustBe expected
      }

      "[max(succ(b+), a-), a+] if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.minus(b)
        val expected = Interval.closed(6, 10)

        actual mustBe expected
      }

      "[a-, min(pred(b-), a+)] in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.minus(b)
        val expected = Interval.closed(1, 4)

        actual mustBe expected
      }

      "[a-, min(pred(b-), a+)] in A overlaps B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 15)

        val actual   = a.minus(b)
        val expected = Interval.closed(1, 4)

        actual mustBe expected
      }

      "[a-, min(pred(b-), a+)] in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.minus(b)
        val expected = Interval.closed(1, 6)

        actual mustBe expected
      }

      "undefined if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        assertThrows[UnsupportedOperationException] {
          a.minus(b)
        }
      }
    }

    "Interval" should {
      "Interval.minus(a, b)" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = Interval.minus(a, b)
        val expected = Interval.closed(1, 6)

        actual mustBe expected
      }
    }

    "Intervals.minus(a, b)" should {
      "return two intervals if A contains B" in {
        val a = Interval.closed(1, 15)
        val b = Interval.closed(5, 10)

        val actual   = Intervals.minus(a, b)
        val expected = List(Interval.closed(1, 4), Interval.closed(11, 15))

        actual must contain theSameElementsAs (expected)
      }
    }
  }