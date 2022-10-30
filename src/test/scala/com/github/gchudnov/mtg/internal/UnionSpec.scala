package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

final class UnionSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Union" when {
    // "a.union(b)" should {
    //   "b.union(a)" in {
    //     forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
    //       val xx = Interval.make(argsX.left, argsX.right)
    //       val yy = Interval.make(argsY.left, argsY.right)

    //       val zz = xx.union(yy)

    //       whenever(zz.nonEmpty) {
    //         val ww = yy.union(xx)

    //         zz.canonical mustBe ww.canonical

    //         xx.merges(yy) mustBe (true)
    //         yy.merges(xx) mustBe (true)
    //       }
    //     }
    //   }
    // }

    // "a.union(b) AND b.union(a)" should {
    //   "equal" in {
    //     forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
    //       val xx = Interval.make(argsX.left, argsX.right)
    //       val yy = Interval.make(argsY.left, argsY.right)

    //       val actual   = xx.union(yy).canonical
    //       val expected = yy.union(xx).canonical

    //       actual mustBe expected
    //     }
    //   }

    //   "∅ if A and B are empty" in {
    //     val a = Interval.empty[Int]
    //     val b = Interval.empty[Int]

    //     val actual   = a.union(b)
    //     val expected = Interval.empty[Int]

    //     actual mustBe expected
    //   }

    //   "B if A is empty" in {
    //     val a = Interval.empty[Int]
    //     val b = Interval.closed(1, 10)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "A if B is empty" in {
    //     val a = Interval.closed(1, 10)
    //     val b = Interval.empty[Int]

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "∅ if A before B" in {
    //     val a = Interval.closed(1, 10)
    //     val b = Interval.closed(20, 30)

    //     val actual   = a.union(b)
    //     val expected = Interval.empty[Int]

    //     actual mustBe expected
    //   }

    //   "∅ if A after B" in {
    //     val a = Interval.closed(20, 30)
    //     val b = Interval.closed(1, 10)

    //     val actual   = a.union(b)
    //     val expected = Interval.empty[Int]

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] if A starts B" in {
    //     val a = Interval.closed(1, 5)
    //     val b = Interval.closed(1, 10)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] if A during B" in {
    //     val a = Interval.closed(5, 7)
    //     val b = Interval.closed(1, 10)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] if A finishes B" in {
    //     val a = Interval.closed(5, 10)
    //     val b = Interval.closed(1, 10)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] if A equals B" in {
    //     val a = Interval.closed(5, 10)
    //     val b = Interval.closed(5, 10)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(5, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] if A is-overlapped-by B" in {
    //     val a = Interval.closed(5, 10)
    //     val b = Interval.closed(1, 7)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] if A is-met-by B" in {
    //     val a = Interval.closed(5, 10)
    //     val b = Interval.closed(1, 5)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] if A is-started-by B" in {
    //     val a = Interval.closed(1, 10)
    //     val b = Interval.closed(1, 5)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] in A meets B" in {
    //     val a = Interval.closed(1, 5)
    //     val b = Interval.closed(5, 10)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] in A overlaps B" in {
    //     val a = Interval.closed(5, 10)
    //     val b = Interval.closed(7, 15)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(5, 15)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] in A is-finished-by B" in {
    //     val a = Interval.closed(1, 10)
    //     val b = Interval.closed(7, 10)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }

    //   "[min(a-,b-), max(a+,b+)] if A contains B" in {
    //     val a = Interval.closed(1, 10)
    //     val b = Interval.closed(5, 7)

    //     val actual   = a.union(b)
    //     val expected = Interval.closed(1, 10)

    //     actual mustBe expected
    //   }
    // }

    // "Interval" should {
    //   "Interval.union(a, b)" in {
    //     val a = Interval.closed(1, 5)  // [1, 5]
    //     val b = Interval.closed(3, 10) // [7, 10]

    //     val expected = Interval.closed(1, 10) // [1, 10]

    //     val c1 = Interval.union(a, b).canonical
    //     val c2 = Interval.union(b, a).canonical

    //     c1 mustBe c2
    //     c2 mustBe c1

    //     c1 mustBe expected
    //   }
    // }
  }
