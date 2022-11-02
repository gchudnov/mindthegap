package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Value
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IntersectionSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordI: Ordering[Interval[Int]] = summon[Ordering[Interval[Int]]]

  "Intersection" when {

    "a.intersection(b)" should {

      "∅ if A and B are empty" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual.isEmpty mustBe(true)
        actual mustBe expected
      }

      "∅ if A is empty" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual.isEmpty mustBe(true)
        actual mustBe expected
      }

      "∅ if B is empty" in {
        val a = Interval.closed(1, 10)
        val b = Interval.empty[Int]

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual.isEmpty mustBe(true)
        actual mustBe expected
      }

      "∅ if A before B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(20, 30)

        val actual   = a.intersection(b)
        val expected = Interval.make(Value.finite(20), Value.finite(10))

        actual.isEmpty mustBe true
        actual mustBe expected
      }

      "∅ if A after B" in {
        val a = Interval.closed(20, 30)
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.make(Value.finite(20), Value.finite(10))

        actual.isEmpty mustBe true
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] if A starts B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(1, 5)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] if A during B" in {
        val a = Interval.closed(5, 7)
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 7)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] if A finishes B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 10)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] if A equals B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(5, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 10)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] if A is-overlapped-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 7)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] if A is-met-by B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.intersection(b)
        val expected = Interval.point(5)

        actual.isPoint mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] if A is-started-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(1, 5)

        val actual   = a.intersection(b)
        val expected = Interval.closed(1, 5)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] in A meets B" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual   = a.intersection(b)
        val expected = Interval.point(5)

        actual.isPoint mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] in A overlaps B" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(7, 15)

        val actual   = a.intersection(b)
        val expected = Interval.closed(7, 10)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] in A is-finished-by B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(7, 10)

        val actual   = a.intersection(b)
        val expected = Interval.closed(7, 10)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "[max(a-, b-), min(a+, b+)] if A contains B" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 7)

        val actual   = a.intersection(b)
        val expected = Interval.closed(5, 7)

        actual.isProper mustBe(true)
        actual mustBe expected
      }

      "∅ if A = unbounded, B = empty" in {
        val a = Interval.unbounded[Int]
        val b = Interval.empty[Int]

        val actual   = a.intersection(b)
        val expected = Interval.empty[Int]

        actual.isEmpty mustBe(true)
        actual mustBe expected
      }

      "B if A = unbounded, B = non-empty" in {
        val a = Interval.unbounded[Int]
        val b = Interval.closed(1, 2)

        val actual   = a.intersection(b)
        val expected = Interval.closed(1, 2)

        actual.isProper mustBe(true)
        actual mustBe expected
      }
    }

    "negative intervals" should {
      "empty & proper is empty" in {
        forAll(genEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          val actual = xx.intersection(yy).canonical
          val expected = yy.intersection(xx).canonical

          actual.isEmpty mustBe true
          expected.isEmpty mustBe true

          actual mustBe expected
        }
      }

      "empty & empty is empty" in {
        forAll(genEmptyIntArgs, genEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.isEmpty mustBe (true)
          yy.isEmpty mustBe (true)

          val actual = xx.intersection(yy).canonical
          val expected = yy.intersection(xx).canonical

          actual.isEmpty mustBe true
          expected.isEmpty mustBe true

          actual mustBe expected
        }
      }

      "proper & proper is empty IF disjoint" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          whenever(xx.isDisjoint(yy)) {
            val actual = xx.intersection(yy).canonical
            val expected = yy.intersection(xx).canonical

            actual.isEmpty mustBe true
            expected.isEmpty mustBe true

            actual mustBe expected
          }
        }
      }

      "proper & proper is proper IF !disjoint" in {
        forAll(genNonEmptyIntArgs, genNonEmptyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          xx.nonEmpty mustBe (true)
          yy.nonEmpty mustBe (true)

          whenever(!xx.isDisjoint(yy)) {
            val actual = xx.intersection(yy).canonical
            val expected = yy.intersection(xx).canonical

            actual.isEmpty mustBe false
            expected.isEmpty mustBe false

            actual mustBe expected
          }
        }        
      }      

      "[2, 3] if [2, 1] & [4, 3]" in {
        // [2, 1]
        val a = Interval.make(Value.finite(2), Value.finite(1))
        a.isEmpty mustBe (true)

        // [4, 3]
        val b = Interval.make(Value.finite(4), Value.finite(3))
        b.isEmpty mustBe (true)

        val actual   = a.intersection(b)
        val expected = Interval.make(Value.finite(4), Value.finite(1)) // [2, 3]

        actual mustBe expected
      }

      "[2, 1] if [2, 1] & [3, 4]" in {
        // [2, 1]
        val a = Interval.make(Value.finite(2), Value.finite(1))
        a.isEmpty mustBe (true)

        // [3, 4]
        val b = Interval.make(Value.finite(3), Value.finite(4))
        b.isEmpty mustBe (false)

        val actual   = a.intersection(b)
        val expected = Interval.make(Value.finite(3), Value.finite(1)) // [3, 1]

        actual mustBe expected
      }      
    }

    "A, B" should {
      "A & B = B & A" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.intersection(yy).canonical
          val expected = yy.intersection(xx).canonical

          actual mustBe expected
        }
      }

      // TODO: (-inf, +inf) if A = (-inf, +inf], B = [-inf, +inf)
      // TODO: things break with inf values, fix it
      // // given the gap c, [-inf, c-) || (c+, +inf] must be the intersection
      // val cLhs = Interval.make[Int](None, true, zz.left.effectiveValue, true)
      // val cRhs = Interval.make[Int](zz.right.effectiveValue, true, None, true)

      // val cc = cLhs.gap(cRhs)
      // cc.canonical mustBe zz.canonical

      "(2, +∞) & (3, 5) = (3, 5) & (2, +∞)" in {
        val a = Interval.leftOpen(2)  // (2, +∞]
        val b = Interval.closed(3, 5) // (3, 5)

        val c1 = a.intersection(b).canonical
        val c2 = b.intersection(a).canonical

        c1 mustBe c2
        c2 mustBe c1
      }
    }

    "A, B, C" should {
      "(A & B) & C = A & (B & C)" in {
        forAll(genAnyIntArgs, genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY, argsZ) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)
          val zz = Interval.make(argsZ.left, argsZ.right)

          val actual   = ((xx.intersection(yy)).intersection(zz)).canonical
          val expected = xx.intersection(yy.intersection(zz)).canonical

          actual mustBe expected
        }
      }
    }

    "Interval" should {
      "Interval.intersection(a, b)" in {
        val a = Interval.rightClosed(2) // (-∞, 2]
        val b = Interval.closed(1, 10)  // [1, 10]

        val expected = Interval.closed(1, 2) // [1, 2]

        val c1 = Interval.intersection(a, b).canonical
        val c2 = Interval.intersection(b, a).canonical

        c1 mustBe c2
        c2 mustBe c1

        c1 mustBe expected
      }
    }
  }
