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
          (Interval.proper(Mark.at(1), Mark.at(2)), "[1,2]"),
          (Interval.proper(Mark.at(1), Mark.pred(3)), "[1,3)"),
          (Interval.proper(Mark.succ(1), Mark.at(3)), "(1,3]"),
          (Interval.proper(Mark.succ(1), Mark.pred(4)), "(1,4)"),
          (Interval.proper(Mark.at(Value.InfNeg), Mark.at(2)), "(-∞,2]"),
          (Interval.proper(Mark.succ(Value.InfNeg), Mark.pred(2)), "(-∞,2)"),
          (Interval.proper(Mark.at(1), Mark.at(Value.InfPos)), "[1,+∞)"),
          (Interval.proper(Mark.succ(1), Mark.pred(Value.InfPos)), "(1,+∞)"),
          (Interval.proper[Int](Mark.at(Value.InfNeg), Mark.at(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Mark.at(Value.InfNeg), Mark.pred(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Mark.succ(Value.InfNeg), Mark.at(Value.InfPos)), "(-∞,+∞)"),
          (Interval.proper[Int](Mark.succ(Value.InfNeg), Mark.pred(Value.InfPos)), "(-∞,+∞)")
        )

        forAll(t) { (xx, expected) =>
          val actual = xx.asString
          actual mustBe expected
        }
      }
    }
  }
