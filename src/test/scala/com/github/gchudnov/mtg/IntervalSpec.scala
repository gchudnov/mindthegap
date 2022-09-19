package com.github.gchudnov.mtg
import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Domains.given
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant

final class IntervalSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  "Interval" when {

    "make" should {

      /**
       * {{{
       *   Given that a < b:
       *
       *   - Empty      | [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
       *   - Degenerate | [a, a] = {a}
       *   - Proper     | otherwise
       * }}}
       */
      "create intervals" in {
        forAll(genOneIntTuple) { case ((ox, oy), ix, iy) =>
          val actual = Interval.make(ox, oy, ix, iy)

          actual match
            case Empty =>
              actual.isEmpty mustBe (true)
              actual.isDegenrate mustBe (false)
              actual.isProper mustBe (false)

              bOrd.gt(LeftBoundary(ox, ix), RightBoundary(oy, iy)) mustBe(true)

            case ab @ Degenerate(_) =>
              actual.isEmpty mustBe (false)
              actual.isDegenrate mustBe (true)
              actual.isProper mustBe (false)

              bOrd.equiv(LeftBoundary(ox, ix), RightBoundary(oy, iy)) mustBe(true)              

            case Proper(_, _) =>
              actual.isEmpty mustBe (false)
              actual.isDegenrate mustBe (false)
              actual.isProper mustBe (true)

              bOrd.lt(LeftBoundary(ox, ix), RightBoundary(oy, iy)) mustBe(true)
        }
      }

      "edge cases" in {
        Interval.make(Some(0), Some(0), true, false).isEmpty mustBe (true)
      }
    }
  }
