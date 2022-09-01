package com.github.gchudnov.mtg

import java.time.Instant

final class IntervalSpec extends TestSpec:

  // final case class InstantInterval(x1: Option[Instant], x2: Option[Instant]) extends Interval[Instant]
  // final case class IntInterval(x1: Option[Int], x2: Option[Int]) extends Interval[Int]

  "Interval" when {
    "several intervals" should {
      "be joined in a sequence" in {
        // val xs: List[Interval[Int]] = List(
        //   Empty,
        //   Degenerate(1),
        //   Open(1, 2),
        //   Closed(3, 4),
        //   LeftClosedRightOpen(5, 6),
        //   LeftOpenRightClosed(7, 8),
        //   LeftOpen(9),
        //   LeftClosed(1),
        //   RightOpen(3),
        //   RightClosed(4),
        //   Unbounded
        // )

        // xs.size mustBe 11
      }
    }
  }
