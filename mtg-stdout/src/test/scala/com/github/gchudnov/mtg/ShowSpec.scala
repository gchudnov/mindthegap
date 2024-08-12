package com.github.gchudnov.mtg

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.github.gchudnov.mtg.Show.*

final class ShowSpec extends AnyWordSpec with Matchers:

  "Show" when {
    "show" should {

      "represent an Empty interval" in {
        val value = Interval.empty[Int]

        val actual   = value.asString
        val expected = "∅"

        actual mustBe expected
      }

      "represent a Point interval" in {
        val value = Interval.point(1)

        val actual   = value.asString
        val expected = "{1}"

        actual mustBe expected
      }

      "represent a Proper interval" in {
        // NOTE: it is not possible to have closed boundaries for infinity: [-∞,+∞]
        val t = Table(
          ("x", "expected"),
          (Interval.proper(internal.Endpoint.at(1), internal.Endpoint.at(2)), "[1,2]"),
          (Interval.proper(internal.Endpoint.at(1), internal.Endpoint.pred(3)), "[1,3)"),
          (Interval.proper(internal.Endpoint.succ(1), internal.Endpoint.at(3)), "(1,3]"),
          (Interval.proper(internal.Endpoint.succ(1), internal.Endpoint.pred(4)), "(1,4)"),
          (Interval.proper(internal.Endpoint.at(internal.Value.InfNeg), internal.Endpoint.at(2)), "(-∞,2]"),
          (Interval.proper(internal.Endpoint.succ(internal.Value.InfNeg), internal.Endpoint.pred(2)), "(-∞,2)"),
          (Interval.proper(internal.Endpoint.at(1), internal.Endpoint.at(internal.Value.InfPos)), "[1,+∞)"),
          (Interval.proper(internal.Endpoint.succ(1), internal.Endpoint.pred(internal.Value.InfPos)), "(1,+∞)"),
          (Interval.proper[Int](internal.Endpoint.at(internal.Value.InfNeg), internal.Endpoint.at(internal.Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](internal.Endpoint.at(internal.Value.InfNeg), internal.Endpoint.pred(internal.Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](internal.Endpoint.succ(internal.Value.InfNeg), internal.Endpoint.at(internal.Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](internal.Endpoint.succ(internal.Value.InfNeg), internal.Endpoint.pred(internal.Value.InfPos)), "(-∞,+∞)"),
        )

        forAll(t) { (xx, expected) =>
          val actual = xx.asString
          actual mustBe expected
        }
      }
    }
  }
