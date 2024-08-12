package com.github.gchudnov.mtg.internal.rel

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Endpoint
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Intersects
 */
final class IntersectsSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordM: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "Intersects" when {
    import IntervalRelAssert.*

    "a.intersects(b)" should {
      "b.intersects(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.intersects(yy)) {
            yy.intersects(xx) mustBe true

            assertOneOf(
              Set(
                Rel.Meets,
                Rel.IsMetBy,
                Rel.Overlaps,
                Rel.IsOverlappedBy,
                Rel.During,
                Rel.Contains,
                Rel.Starts,
                Rel.IsStartedBy,
                Rel.Finishes,
                Rel.IsFinishedBy,
                Rel.EqualsTo,
              )
            )(xx, yy)

            // a- <= b+ && b- <= a+
            val a1 = argsX.left
            val b1 = argsY.left

            val a2 = argsX.right
            val b2 = argsY.right

            (ordM.lteq(a1, b2) && ordM.lteq(b1, a2)) mustBe (true)
          }
        }
      }
    }

    "a.intersects(b) AND b.intersects(a)" should {

      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.intersects(yy)
          val expected = yy.intersects(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].intersects(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].intersects(Interval.point(0)) mustBe (false)
        Interval.empty[Int].intersects(Interval.closed(0, 1)) mustBe (false)

        // Point
        Interval.point(5).intersects(Interval.point(5)) mustBe (true)
        Interval.point(5).intersects(Interval.point(6)) mustBe (false)
        Interval.point(5).intersects(Interval.empty[Int]) mustBe (false)

        // Proper
        Interval.open(4, 7).intersects(Interval.open(4, 7)) mustBe (true)
        Interval.open(0, 5).intersects(Interval.open(0, 5)) mustBe (true)
        Interval.closed(0, 5).intersects(Interval.closed(1, 6)) mustBe (true)
        Interval.closed(1, 6).intersects(Interval.closed(0, 5)) mustBe (true)
        Interval.leftOpenRightClosed(0, 5).intersects(Interval.leftOpenRightClosed(1, 15)) mustBe (true)
        Interval.leftClosedRightOpen(0, 5).intersects(Interval.leftClosedRightOpen(0, 5)) mustBe (true)

        // Infinity
        // [5, +inf)  (-inf, 10)
        Interval.leftClosed(5).intersects(Interval.rightOpen(10)) mustBe (true)

        // [doc]
        Interval.empty[Int].intersects(Interval.empty[Int]) mustBe (false)
        Interval.point(5).intersects(Interval.point(5)) mustBe (true)
        Interval.closed(0, 5).intersects(Interval.closed(1, 6)) mustBe (true)
      }
    }
  }
