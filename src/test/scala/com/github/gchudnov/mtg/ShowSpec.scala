package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Domains.given
import com.github.gchudnov.mtg.Show.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant

final class ShowSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

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
          (Interval.proper(Some(1), Some(3), true, false), "[1,3)"),
          (Interval.proper(Some(1), Some(3), false, true), "(1,3]"),
          (Interval.proper(Some(1), Some(4), false, false), "(1,4)"),
          (Interval.proper(None, Some(2), true, true), "[-∞,2]"),
          (Interval.proper(None, Some(2), true, false), "[-∞,2)"),
          (Interval.proper(None, Some(2), false, true), "(-∞,2]"),
          (Interval.proper(None, Some(2), false, false), "(-∞,2)"),
          (Interval.proper(Some(1), None, true, true), "[1,+∞]"),
          (Interval.proper(Some(1), None, true, false), "[1,+∞)"),
          (Interval.proper(Some(1), None, false, true), "(1,+∞]"),
          (Interval.proper(Some(1), None, false, false), "(1,+∞)"),
          (Interval.proper[Int](None, None, true, true), "[-∞,+∞]"),
          (Interval.proper[Int](None, None, true, false), "[-∞,+∞)"),
          (Interval.proper[Int](None, None, false, true), "(-∞,+∞]"),
          (Interval.proper[Int](None, None, false, false), "(-∞,+∞)")
        )

        forAll(t) { (x, expected) =>
          val actual = x.show
          actual mustBe expected
        }
      }
    }

    "prepare" should {
      "prepare bounded and unbounded intervals for rendering" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val data = Show.prepare(List(a, b, c, d))

        data mustBe List("....", ".....")
      }

      "prepare unbounded interval for rendering" in {
        val a = Interval.unbounded[Int]

        val data = Show.prepare(List(a))

        data mustBe List("....", ".....")
      }      
    }
  }
