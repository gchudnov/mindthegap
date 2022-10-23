package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class GapSpec extends TestSpec:

  "Gap" when {
    "calc" should {
      "∅ if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "[a+, b-] if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.gap(b)
        val expected = Interval.closed(10, 20)

        actual mustBe expected
      }

      "[b+, a-] if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.closed(10, 20)

        actual mustBe expected
      }

      "∅ if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "[a-, b+] if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.gap(b)
        val expected = Interval.point(5)

        actual mustBe expected
      }

      "∅ if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "[b-, a+] in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.gap(b)
        val expected = Interval.point(5)

        actual mustBe expected
      }

      "∅ in A overlaps B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(7, 15)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        val actual   = a.gap(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }
    }
  }
