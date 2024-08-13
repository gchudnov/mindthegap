package com.github.gchudnov.mtg.relations

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain
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
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.merges(yy) && xx.nonEmpty && yy.nonEmpty) {
            yy.merges(xx) shouldBe true

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
                Rel.Before,
                Rel.After,
              )
            )(xx, yy)

            // a.intersects(b) OR a.isAdjacent(b)
            (xx.intersects(yy) || xx.isAdjacent(yy)) shouldBe (true)
          }
        }
      }

      "b.merges(a) if (a.isEmpty OR b.isEmpty)" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          whenever(xx.merges(yy) && (xx.isEmpty || yy.isEmpty)) {
            yy.merges(xx) shouldBe true

            val rs = findRelations(xx, yy)
            val es = if xx.canonical == yy.canonical then Set(Rel.EqualsTo) else Set.empty[Rel]
            rs should contain theSameElementsAs (es)

            xx.intersects(yy) shouldBe (false)
            xx.isAdjacent(yy) shouldBe (false)
          }
        }
      }
    }

    "a.merges(b) AND b.merges(a)" should {
      "equal" in {
        forAll(genAnyIntArgs, genAnyIntArgs) { case (argsX, argsY) =>
          val xx = Interval.make(argsX.left, argsX.right)
          val yy = Interval.make(argsY.left, argsY.right)

          val actual   = xx.merges(yy)
          val expected = yy.merges(xx)

          actual shouldBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].merges(Interval.empty[Int]) shouldBe (true)
        Interval.empty[Int].merges(Interval.point(1)) shouldBe (true)
        Interval.empty[Int].merges(Interval.closed(1, 4)) shouldBe (true)
        Interval.empty[Int].merges(Interval.open(1, 4)) shouldBe (true)
        Interval.empty[Int].merges(Interval.unbounded[Int]) shouldBe (true)

        // Point
        Interval.point(5).merges(Interval.empty[Int]) shouldBe (true)
        Interval.point(5).merges(Interval.point(5)) shouldBe (true)
        Interval.point(5).merges(Interval.point(6)) shouldBe (true)
        Interval.point(5).merges(Interval.open(5, 10)) shouldBe (true)

        // Proper
        Interval.closed(4, 10).merges(Interval.empty[Int]) shouldBe (true)
        Interval.closed(4, 10).merges(Interval.point(11)) shouldBe (true)
        Interval.open(4, 10).merges(Interval.open(5, 12)) shouldBe (true)
        Interval.open(5, 12).isMergedBy(Interval.open(4, 10)) shouldBe (true)
        Interval.open(4, 7).merges(Interval.open(5, 8)) shouldBe (true)

        // [doc]
        Interval.point(5).merges(Interval.point(6)) shouldBe (true)
        Interval.closed(4, 10).merges(Interval.closed(5, 12)) shouldBe (true)
      }
    }
  }
