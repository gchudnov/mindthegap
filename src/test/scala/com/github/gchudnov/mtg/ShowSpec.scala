package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Show.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration

import java.time.Instant

final class ShowSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  "Show" when {

    "show" should {

      "represent an Empty interval" in {
        val value    = Interval.empty[Int]
        val actual   = value.show
        val expected = "∅"

        actual mustBe expected
      }

      "represent a Degenerate interval" in {
        val value    = Interval.degenerate(1)
        val actual   = value.show
        val expected = "{1}"

        actual mustBe expected
      }

      "represent a Proper interval" in {
        val t = Table(
          ("x", "expected"),
          (Interval.proper(Some(1), Some(2), true, true), "[1,2]"),
          (Interval.proper(Some(1), Some(2), true, false), "[1,2)"),
          (Interval.proper(Some(1), Some(2), false, true), "(1,2]"),
          (Interval.proper(Some(1), Some(2), false, false), "(1,2)"),
          (Interval.proper(None, Some(2), true, true), "[-∞,2]"),
          (Interval.proper(None, Some(2), true, false), "[-∞,2)"),
          (Interval.proper(None, Some(2), false, true), "(-∞,2]"),
          (Interval.proper(None, Some(2), false, false), "(-∞,2)"),
          (Interval.proper(Some(1), None, true, true), "[1,+∞]"),
          (Interval.proper(Some(1), None, true, false), "[1,+∞)"),
          (Interval.proper(Some(1), None, false, true), "(1,+∞]"),
          (Interval.proper(Some(1), None, false, false), "(1,+∞)"),
          (Interval.proper[Nothing](None, None, true, true), "[-∞,+∞]"),
          (Interval.proper[Nothing](None, None, true, false), "[-∞,+∞)"),
          (Interval.proper[Nothing](None, None, false, true), "(-∞,+∞]"),
          (Interval.proper[Nothing](None, None, false, false), "(-∞,+∞)")
        )

        forAll(t) { (x, expected) =>
          val actual = x.show
          actual mustBe expected
        }
      }
    }
  }
