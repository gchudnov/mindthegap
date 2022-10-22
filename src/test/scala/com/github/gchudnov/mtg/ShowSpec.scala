package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant

final class ShowSpec extends TestSpec:

  import com.github.gchudnov.mtg.Show.given

  "Show" when {
    "show" should {

      "represent an Empty interval" in {
        val value    = Interval.empty[Int]
        val actual   = value.show
        val expected = "∅"

        actual mustBe expected
      }

      "represent a Point interval" in {
        val value    = Interval.point(1)
        val actual   = value.show
        val expected = "{1}"

        actual mustBe expected
      }

      "represent a Proper interval" in {
        val t = Table(
          ("x", "expected"),
          (Interval.proper(Some(1), true, Some(2), true), "[1,2]"),
          (Interval.proper(Some(1), true, Some(3), false), "[1,3)"),
          (Interval.proper(Some(1), false, Some(3), true), "(1,3]"),
          (Interval.proper(Some(1), false, Some(4), false), "(1,4)"),
          (Interval.proper(None, true, Some(2), true), "[-∞,2]"),
          (Interval.proper(None, true, Some(2), false), "[-∞,2)"),
          (Interval.proper(None, false, Some(2), true), "(-∞,2]"),
          (Interval.proper(None, false, Some(2), false), "(-∞,2)"),
          (Interval.proper(Some(1), true, None, true), "[1,+∞]"),
          (Interval.proper(Some(1), true, None, false), "[1,+∞)"),
          (Interval.proper(Some(1), false, None, true), "(1,+∞]"),
          (Interval.proper(Some(1), false, None, false), "(1,+∞)"),
          (Interval.proper[Int](None, true, None, true), "[-∞,+∞]"),
          (Interval.proper[Int](None, true, None, false), "[-∞,+∞)"),
          (Interval.proper[Int](None, false, None, true), "(-∞,+∞]"),
          (Interval.proper[Int](None, false, None, false), "(-∞,+∞)")
        )

        forAll(t) { (x, expected) =>
          val actual = x.show
          actual mustBe expected
        }
      }
    }
  }
