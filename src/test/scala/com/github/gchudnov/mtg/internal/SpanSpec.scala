package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class SpanSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Span" when {
    "calc" should {
      "∅ if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.span(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.span(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "[a-, b+] if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 30)

        actual mustBe expected
      }

      "[b-, a+] if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 30)

        actual mustBe expected
      }

      "[a-, b+] if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[b-, b+] if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[b-, b+] if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[a-, a+] if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(5, 10)

        actual mustBe expected
      }

      "[b-, a+] if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[b-, a+] if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[a-, b+] if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[a-, b+] in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[a-, b+] in A overlaps B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(7, 15)

        val actual   = a.span(b)
        val expected = Interval.closed(5, 15)

        actual mustBe expected
      }

      "[a-, b+] in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[a-, a+] if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }
    }

    "A, B" should {

      /**
       * Commutative Property
       */
      "A # B = B # A" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          xx.span(yy).canonical mustBe yy.span(xx).canonical
        }
      }
    }

    "A, B, C" should {

      /**
       * Associative Property
       */
      "(A # B) # C = A # (B # C)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2)), ((oz1, iz1), (oz2, iz2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)
          val zz = Interval.make(oz1, iz1, oz2, iz2)

          ((xx.span(yy)).span(zz)).canonical mustBe xx.span(yy.span(zz)).canonical
        }
      }
    }
  }
