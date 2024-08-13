package com.github.gchudnov.mtg.internal

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.github.gchudnov.mtg.Interval

final class ShowSpec extends AnyWordSpec with Matchers:

  "Show" when {
    "show" should {

      // TODO: extract to IntervalSpec

      "represent an Empty interval" in {
        val i = Interval.empty[Int]

        val actual   = i.toString
        val expected = "∅"

        actual shouldBe expected
      }

      "represent a Point interval" in {
        val i = Interval.point(1)

        val actual   = i.toString
        val expected = "{1}"

        actual shouldBe expected
      }

      "represent a Proper interval" in {
        // NOTE: it is not possible to have closed boundaries for infinity: [-∞,+∞]
        val t = Table(
          ("x", "expected"),
          (Interval.proper(Endpoint.at(1), Endpoint.at(2)), "[1,2]"),
          (Interval.proper(Endpoint.at(1), Endpoint.pred(3)), "[1,3)"),
          (Interval.proper(Endpoint.succ(1), Endpoint.at(3)), "(1,3]"),
          (Interval.proper(Endpoint.succ(1), Endpoint.pred(4)), "(1,4)"),
          (Interval.proper(Endpoint.at(Value.InfNeg), Endpoint.at(2)), "(-∞,2]"),
          (Interval.proper(Endpoint.succ(Value.InfNeg), Endpoint.pred(2)), "(-∞,2)"),
          (Interval.proper(Endpoint.at(1), Endpoint.at(Value.InfPos)), "[1,+∞)"),
          (Interval.proper(Endpoint.succ(1), Endpoint.pred(Value.InfPos)), "(1,+∞)"),
          (Interval.proper[Int](Endpoint.at(Value.InfNeg), Endpoint.at(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Endpoint.at(Value.InfNeg), Endpoint.pred(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Endpoint.succ(Value.InfNeg), Endpoint.at(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Endpoint.succ(Value.InfNeg), Endpoint.pred(Value.InfPos)), "(-∞,+∞)"),
        )

        forAll(t) { (xx, expected) =>
          val actual = xx.toString
          actual shouldBe expected
        }
      }
    }
  }
