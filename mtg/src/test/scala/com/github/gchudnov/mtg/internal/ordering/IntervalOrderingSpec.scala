package com.github.gchudnov.mtg.internal.ordering

import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class IntervalOrderingSpec extends TestSpec:

  val ordI: Ordering[Interval[Int]] = summon[Ordering[Interval[Int]]]

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "IntervalOrdering" when {

    "3 intervals are ordered, the relation" should {
      "be transitive" in {
        forAll(genAnyIntArgs, genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY, argsZ) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)
          val zz = Interval.make(argsZ.left, argsZ.right)

          whenever(ordI.lt(xx, yy) && ordI.lt(yy, zz)) {
            ordI.lt(xx, zz) shouldBe (true)
          }
        }
      }
    }

    "the same interval represented in different ways" should {
      "be equivalent" in {
        // (3, 5)
        val a = Interval.make(Endpoint.succ(3), Endpoint.pred(5))

        // [4, 4]
        val b = Interval.make(Endpoint.at(4), Endpoint.at(4))

        // (3, 4]
        val c = Interval.make(Endpoint.succ(3), Endpoint.at(4))

        // [4, 5)
        val d = Interval.make(Endpoint.at(4), Endpoint.pred(5))

        ordI.equiv(a, a) shouldBe (true)
        ordI.equiv(a, b) shouldBe (true)
        ordI.equiv(a, c) shouldBe (true)
        ordI.equiv(a, d) shouldBe (true)

        ordI.equiv(b, a) shouldBe (true)
        ordI.equiv(b, b) shouldBe (true)
        ordI.equiv(b, c) shouldBe (true)
        ordI.equiv(b, d) shouldBe (true)

        ordI.equiv(c, a) shouldBe (true)
        ordI.equiv(c, b) shouldBe (true)
        ordI.equiv(c, c) shouldBe (true)
        ordI.equiv(c, d) shouldBe (true)

        ordI.equiv(d, a) shouldBe (true)
        ordI.equiv(d, b) shouldBe (true)
        ordI.equiv(d, c) shouldBe (true)
        ordI.equiv(d, d) shouldBe (true)
      }
    }

    "collection of predefined intervals" should {

      /**
       * {{{
       *   (-∞,+∞)
       *   ∅ = [-10,-20]
       *   [0,10]
       *   [5,15]
       *   ∅ = [20,10]
       *   [20,30]
       *   ∅ = (+∞,-∞)
       * }}}
       */
      "be ordered" in {
        val a = Interval.closed(5, 15)
        val b = Interval.closed(0, 10)
        val c = Interval.closed(20, 30)
        val d = Interval.unbounded[Int]
        val e = Interval.empty[Int]
        val f = Interval.closed(10, 20).swap
        val g = Interval.closed(-20, -10).swap

        val actual   = List(a, b, c, d, e, f, g).sorted
        val expected = List(d, g, b, a, f, c, e)

        (actual should contain).theSameElementsInOrderAs(expected)
      }
    }

    "empty intervals" should {
      "be compared" in {
        val a = Interval.empty[Int]
        val b = Interval.empty[Int]

        ordI.compare(a, b) shouldBe 0
      }
    }

    "empty and non-empty intervals" should {
      "be compared" in {
        val a = Interval.empty[Int]
        val b = Interval.closed(0, 10)

        ordI.compare(a, b) shouldBe 1
      }
    }

    "non-empty and empty intervals" should {
      "be compared" in {
        val a = Interval.closed(0, 10)
        val b = Interval.empty[Int]

        ordI.compare(a, b) shouldBe -1
      }
    }

    "point intervals" should {
      "be sorted if we have two finite points" in {
        val a = Interval.point(79)
        val b = Interval.point(51)

        val input = List(a, b)

        val actual   = input.sorted
        val expected = List(b, a)

        actual shouldBe expected
      }

      "be sorted if we have finite and infinite points" in {
        val a = Interval.point[Int](Value.infNeg)
        val b = Interval.point(79)
        val c = Interval.point(51)
        val d = Interval.point[Int](Value.infPos)

        val input = List(a, b, d, c)

        val actual   = input.sorted
        val expected = List(a, c, b, d)

        actual shouldBe (expected)
      }
    }

    "compare(a, b)" should {
      "be 0 for equal intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(1, 5)

        ordI.compare(a, b) shouldBe 0
        ordI.compare(b, a) shouldBe 0
      }

      "be defined for intervals with the same left but different right boundary" in {
        val a = Interval.closed(2, 5)
        val b = Interval.closed(2, 3)

        ordI.compare(a, b) shouldBe 1
        ordI.compare(b, a) shouldBe -1
      }

      "be defined when one interval is included in the other one" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(3, 7)

        ordI.compare(a, b) shouldBe -1
        ordI.compare(b, a) shouldBe 1
      }
    }

    "ordering [doc]" should {
      "provide the expected results" in {
        val a = Interval.closed(0, 10)  // [0, 10]
        val b = Interval.closed(20, 30) // [20, 30]

        val actual   = List(b, a).sorted // List(a, b)  // [0, 10], [20, 30]
        val expected = List(a, b)

        actual shouldBe expected
      }
    }
  }
