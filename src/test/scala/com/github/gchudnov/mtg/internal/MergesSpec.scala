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
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.merges(yy)) {
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
          }
        }
      }
    }

    "a.merges(b) AND b.merges(a)" should {
      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          val actual   = xx.merges(yy)
          val expected = yy.merges(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].merges(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].merges(Interval.point(1)) mustBe (false)
        Interval.empty[Int].merges(Interval.closed(1, 4)) mustBe (false)
        Interval.empty[Int].merges(Interval.open(1, 4)) mustBe (false)
        Interval.empty[Int].merges(Interval.unbounded[Int]) mustBe (false)

        // Point
        Interval.point(5).merges(Interval.empty[Int]) mustBe (false)
        Interval.point(5).merges(Interval.point(5)) mustBe (true)
        Interval.point(5).merges(Interval.point(6)) mustBe (true)
        Interval.point(5).merges(Interval.open(5, 10)) mustBe (true)

        // Proper
        Interval.open(4, 10).merges(Interval.open(5, 12)) mustBe (true)
        Interval.open(5, 12).isMergedBy(Interval.open(4, 10)) mustBe (true)
        Interval.open(4, 7).merges(Interval.open(5, 8)) mustBe (true)
      }
    }
  }
