package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*
import com.github.gchudnov.mtg.Value

final class SpanSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Span" when {
    "a.span(b)" should {

      "b.span(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val zz = xx.span(yy)

          whenever(zz.nonEmpty) {
            val ww = yy.span(xx)

            zz.canonical mustBe ww.canonical

            if xx.nonEmpty && yy.nonEmpty then
              // [min(a-, b-), max(a+, b+)]
              // given span `c`, `c` intersection with `a` = `a` AND `c` intersects with `b` = `b`
              zz.intersection(xx).canonical mustBe xx.canonical
              zz.intersection(yy).canonical mustBe yy.canonical
          }
        }
      }
    }

    "a.span(b)" should {

      "∅ if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.span(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "B if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "A if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 30)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 30)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(5, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] in A overlaps B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(7, 15)

        val actual   = a.span(b)
        val expected = Interval.closed(5, 15)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-, b-), max(a+, b+)] if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        val actual   = a.span(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }
    }

    "negative intervals" should {
      "[4, 1] if [2, 1] # [4, 3]" in {
        // [2, 1]
        val a = Interval.make(Value.finite(2), Value.finite(1))
        a.isEmpty mustBe(true)

        // [4, 3]
        val b = Interval.make(Value.finite(4), Value.finite(3))
        b.isEmpty mustBe(true)

        val actual   = a.span(b)
        val expected = Interval.make(Value.finite(4), Value.finite(1)) // [4, 1]

        actual mustBe expected
      }

      "[3, 4] if [2, 1] # [3, 4]" in {
        // [2, 1]
        val a = Interval.make(Value.finite(2), Value.finite(1))
        a.isEmpty mustBe(true)

        // [3, 4]
        val b = Interval.make(Value.finite(3), Value.finite(4))
        b.isEmpty mustBe(false)

        val actual   = a.span(b)
        val expected = Interval.make(Value.finite(3), Value.finite(4)) // [3, 4]

        actual mustBe expected
      }
    }

    "A, B" should {
      "A # B = B # A" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.span(yy).canonical
          val expected = yy.span(xx).canonical

          actual mustBe expected
        }
      }
    }

    "A, B, C" should {
      "(A # B) # C = A # (B # C)" in {
        forAll(genAnyIntArgs, genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY, argsZ) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)
          val zz = Interval.make(argsZ.left, argsZ.right)

          val actual   = ((xx.span(yy)).span(zz)).canonical
          val expected = xx.span(yy.span(zz)).canonical

          actual mustBe expected
        }
      }
    }

    "Interval" should {
      "Interval.span(a, b)" in {
        val a = Interval.closed(1, 5)  // [1, 5]
        val b = Interval.closed(7, 10) // [7, 10]

        val expected = Interval.closed(1, 10) // [1, 10]

        val c1 = Interval.span(a, b).canonical
        val c2 = Interval.span(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }
  }
