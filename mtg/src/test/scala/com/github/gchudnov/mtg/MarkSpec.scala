package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

final class MarkSpec extends TestSpec:

  "Mark" when {
    "Mark.At" should {
      "construct from T" in {
        val actual   = Mark.at(1)
        val expected = Mark.At(Value.Finite(1))

        actual.isAt mustBe (true)
        actual.isPred mustBe (false)
        actual.isSucc mustBe (false)

        actual mustBe expected
      }

      "construct from Value.Finite" in {
        val actual   = Mark.at(Value.Finite(1))
        val expected = Mark.At(Value.Finite(1))

        actual mustBe expected
      }

      "construct from Value.InfNeg" in {
        val actual   = Mark.at(Value.InfNeg)
        val expected = Mark.At(Value.InfNeg)

        actual mustBe expected
      }

      "construct from Value.InfPos" in {
        val actual   = Mark.at(Value.InfPos)
        val expected = Mark.At(Value.InfPos)

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = Mark.at(Value.InfPos).succ
        val expected = Mark.Succ(Mark.At(Value.InfPos))

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = Mark.at(Value.InfPos).pred
        val expected = Mark.Pred(Mark.At(Value.InfPos))

        actual mustBe expected
      }

      "x.value" in {
        val x = Mark.at(1)

        val actual   = x.value
        val expected = Value.Finite(1)

        actual mustBe expected
      }

      "x.eval" in {
        val x = Mark.at(1)

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual mustBe expected
      }

      "x.at" in {
        val x = Mark.at(1)

        val actual   = x.at
        val expected = Mark.at(1)

        actual mustBe expected
      }
    }

    "Mark.Pred" should {
      "construct from Mark" in {
        val actual   = Mark.pred(Mark.at(1))
        val expected = Mark.Pred(Mark.At(Value.Finite(1)))

        actual.isAt mustBe (false)
        actual.isPred mustBe (true)
        actual.isSucc mustBe (false)

        actual mustBe expected
      }

      "construct from Value" in {
        val actual   = Mark.pred(Value.Finite(1))
        val expected = Mark.Pred(Mark.At(Value.Finite(1)))

        actual mustBe expected
      }

      "construct from T" in {
        val actual   = Mark.pred(1)
        val expected = Mark.Pred(Mark.At(Value.Finite(1)))

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = Mark.pred(Value.InfPos).succ
        val expected = Mark.At(Value.InfPos)

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = Mark.pred(Value.InfPos).pred
        val expected = Mark.Pred(Mark.Pred(Mark.At(Value.InfPos)))

        actual mustBe expected
      }

      "x.eval" in {
        val x = Mark.pred(Mark.at(1))

        val actual   = x.eval
        val expected = Value.Finite(0)

        actual mustBe expected
      }

      "x.at" in {
        val x = Mark.pred(1)

        val actual   = x.at
        val expected = Mark.at(0)

        actual mustBe expected
      }

      "Pred(At(Finite(-3)) == Finite(-4)" in {
        val x = Mark.pred(Mark.at(Value.finite(-3)))

        val actual   = x.eval
        val expected = Value.finite(-4)

        actual mustBe (expected)
      }
    }

    "Mark.Succ" should {
      "construct from Mark" in {
        val actual   = Mark.succ(Mark.at(1))
        val expected = Mark.Succ(Mark.At(Value.Finite(1)))

        actual.isAt mustBe (false)
        actual.isPred mustBe (false)
        actual.isSucc mustBe (true)

        actual mustBe expected
      }

      "construct from Value" in {
        val actual   = Mark.succ(Value.Finite(1))
        val expected = Mark.Succ(Mark.At(Value.Finite(1)))

        actual mustBe expected
      }

      "construct from T" in {
        val actual   = Mark.succ(1)
        val expected = Mark.Succ(Mark.At(Value.Finite(1)))

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = Mark.succ(Value.InfPos).succ
        val expected = Mark.Succ(Mark.Succ(Mark.At(Value.InfPos)))

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = Mark.succ(Value.InfPos).pred
        val expected = Mark.At(Value.InfPos)

        actual mustBe expected
      }

      "x.eval" in {
        val x = Mark.succ(Mark.at(1))

        val actual   = x.eval
        val expected = Value.Finite(2)

        actual mustBe expected
      }

      "x.at" in {
        val x = Mark.succ(1)

        val actual   = x.at
        val expected = Mark.at(2)

        actual mustBe expected
      }
    }

    "evaluated" should {
      "get a value from Succ(Succ(x))" in {
        val x = Mark.succ(Mark.succ(1))

        val actual   = x.eval
        val expected = Value.Finite(3)

        actual mustBe expected
      }

      "get a value from Pred(Pred(x))" in {
        val x = Mark.succ(Mark.succ(1))

        val actual   = x.eval
        val expected = Value.Finite(3)

        actual mustBe expected
      }

      "get a value from Succ(Succ(Pred(Pred(x))))" in {
        val x = Mark.succ(Mark.succ(Mark.pred(Mark.pred(1))))

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual mustBe expected
      }

      "get a value from Succ(Pred(Succ(Pred(x))))" in {
        val x = Mark.succ(Mark.pred(Mark.succ(Mark.pred(1))))

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual mustBe expected
      }

      "get innerValue" in {
        val t = Table(
          ("input", "expected"),
          (Mark.succ(Mark.succ(1)), Value.finite(1)),
          (Mark.pred(Mark.pred(2)), Value.finite(2)),
          (Mark.succ(3), Value.finite(3)),
          (Mark.pred(4), Value.finite(4)),
          (Mark.at(5), Value.finite(5))
        )

        forAll(t) { (input, expected) =>
          val actual = input.innerValue
          actual mustBe expected
        }
      }
    }
  }
