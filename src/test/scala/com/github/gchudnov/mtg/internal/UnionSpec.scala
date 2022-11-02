package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class UnionSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Union" when {

    "a.union(b)" should {

      "∅ if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.union(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "B if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "A if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "∅ if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.union(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "∅ if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.union(b)
        val expected = Interval.empty[Int]

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.union(b)
        val expected = Interval.closed(5, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] in A overlaps B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(7, 15)

        val actual   = a.union(b)
        val expected = Interval.closed(5, 15)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }

      "[min(a-,b-), max(a+,b+)] if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        val actual   = a.union(b)
        val expected = Interval.closed(1, 10)

        actual mustBe expected
      }
    }

    "negative intervals" should {
      // TODO: ADD MORE TESTS

      "[9, 1] if [2, 1] U [4, 3]" in {
        // [2, 1]
        val a = Interval.make(Value.finite(2), Value.finite(1))
        a.isEmpty mustBe(true)

        // [4, 3]
        val b = Interval.make(Value.finite(4), Value.finite(3))
        b.isEmpty mustBe(true)

        val actual   = a.union(b)
        val expected = Interval.make(Value.finite(9), Value.finite(1)) // [9, 1]

        actual mustBe expected
      }

      "[8, 1] if [2, 1] U [3, 4]" in {
        // [2, 1]
        val a = Interval.make(Value.finite(2), Value.finite(1))
        a.isEmpty mustBe(true)

        // [3, 4]
        val b = Interval.make(Value.finite(3), Value.finite(4))
        b.isEmpty mustBe(false)

        val actual   = a.union(b)
        val expected = Interval.make(Value.finite(8), Value.finite(1)) // [8, 1]

        actual mustBe expected
      }
    }

    "A, B" should {
      "A U B = B U A" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.union(yy).canonical
          val expected = yy.union(xx).canonical

          actual mustBe expected
        }
      }
    }

    "Interval" should {
      "Interval.union(a, b)" in {
        val a = Interval.closed(1, 5)  // [1, 5]
        val b = Interval.closed(3, 10) // [7, 10]

        val expected = Interval.closed(1, 10) // [1, 10]

        val c1 = Interval.union(a, b).canonical
        val c2 = Interval.union(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }
  }
