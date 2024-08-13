package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.internal.Endpoint
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

  val ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "Intersects" when {
    import IntervalRelAssert.*

    "a.intersects(b)" should {
      "b.intersects(a)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.intersects(yy)) {
            yy.intersects(xx) shouldBe true

            // inverse relation is the same
            xx.isIntersectedBy(yy) shouldBe true
            yy.isIntersectedBy(xx) shouldBe true

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

            (ordE.lteq(a1, b2) && ordE.lteq(b1, a2)) shouldBe (true)
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

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].intersects(Interval.empty[Int]) shouldBe (false)
        Interval.empty[Int].intersects(Interval.point(0)) shouldBe (false)
        Interval.empty[Int].intersects(Interval.closed(0, 1)) shouldBe (false)

        // Point
        Interval.point(5).intersects(Interval.point(5)) shouldBe (true)
        Interval.point(5).intersects(Interval.point(6)) shouldBe (false)
        Interval.point(5).intersects(Interval.empty[Int]) shouldBe (false)

        // Proper
        Interval.open(4, 7).intersects(Interval.open(4, 7)) shouldBe (true)
        Interval.open(0, 5).intersects(Interval.open(0, 5)) shouldBe (true)
        Interval.closed(0, 5).intersects(Interval.closed(1, 6)) shouldBe (true)
        Interval.closed(1, 6).intersects(Interval.closed(0, 5)) shouldBe (true)
        Interval.leftOpenRightClosed(0, 5).intersects(Interval.leftOpenRightClosed(1, 15)) shouldBe (true)
        Interval.leftClosedRightOpen(0, 5).intersects(Interval.leftClosedRightOpen(0, 5)) shouldBe (true)

        // Infinity
        // [5, +inf)  (-inf, 10)
        Interval.leftClosed(5).intersects(Interval.rightOpen(10)) shouldBe (true)

        // [doc]
        Interval.empty[Int].intersects(Interval.empty[Int]) shouldBe (false)
        Interval.point(5).intersects(Interval.point(5)) shouldBe (true)
        Interval.closed(0, 5).intersects(Interval.closed(1, 6)) shouldBe (true)
      }
    }
  }
