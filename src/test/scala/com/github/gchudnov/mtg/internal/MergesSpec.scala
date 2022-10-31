package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Merges, IsMergedBy
 */
final class MergesSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Merges" when {
    import IntervalRelAssert.*

    "a.merges(b)" should {
      "b.merges(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.merges(yy) && xx.nonEmpty && yy.nonEmpty) {
            yy.merges(xx) mustBe true

            assertOneOf(
              Set(
                Rel.Meets,
                Rel.IsMetBy,
                Rel.Overlaps,
                Rel.IsOverlapedBy,
                Rel.During,
                Rel.Contains,
                Rel.Starts,
                Rel.IsStartedBy,
                Rel.Finishes,
                Rel.IsFinishedBy,
                Rel.EqualsTo,
                Rel.Before,
                Rel.After
              )
            )(xx, yy)

            // a.intersects(b) OR a.isAdjacent(b)
            (xx.intersects(yy) || xx.isAdjacent(yy)) mustBe (true)
          }
        }
      }

      "b.merges(a) if (a.isEmpty OR b.isEmpty)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.merges(yy) && (xx.isEmpty || yy.isEmpty)) {
            yy.merges(xx) mustBe true

            val rs = findRelations(xx, yy)
            rs.isEmpty mustBe (true)

            xx.intersects(yy) mustBe (false)
            xx.isAdjacent(yy) mustBe (false)
          }
        }
      }
    }

    "a.merges(b) AND b.merges(a)" should {
      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.merges(yy)
          val expected = yy.merges(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].merges(Interval.empty[Int]) mustBe (true)
        Interval.empty[Int].merges(Interval.point(1)) mustBe (true)
        Interval.empty[Int].merges(Interval.closed(1, 4)) mustBe (true)
        Interval.empty[Int].merges(Interval.open(1, 4)) mustBe (true)
        Interval.empty[Int].merges(Interval.unbounded[Int]) mustBe (true)

        // Point
        Interval.point(5).merges(Interval.empty[Int]) mustBe (true)
        Interval.point(5).merges(Interval.point(5)) mustBe (true)
        Interval.point(5).merges(Interval.point(6)) mustBe (true)
        Interval.point(5).merges(Interval.open(5, 10)) mustBe (true)

        // Proper
        Interval.closed(4, 10).merges(Interval.empty[Int]) mustBe (true)
        Interval.closed(4, 10).merges(Interval.point(11)) mustBe (true)
        Interval.open(4, 10).merges(Interval.open(5, 12)) mustBe (true)
        Interval.open(5, 12).isMergedBy(Interval.open(4, 10)) mustBe (true)
        Interval.open(4, 7).merges(Interval.open(5, 8)) mustBe (true)
      }
    }
  }
