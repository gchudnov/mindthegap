package com.github.gchudnov.mtg

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

final class EndpointSpec extends TestSpec:

  "Endpoint" when {
    "Endpoint.At" should {
      "construct from T" in {
        val actual   = Endpoint.at(1)
        val expected = Endpoint.At(Value.Finite(1))

        actual.isAt mustBe (true)
        actual.isPred mustBe (false)
        actual.isSucc mustBe (false)

        actual mustBe expected
      }

      "construct from Value.Finite" in {
        val actual   = Endpoint.at(Value.Finite(1))
        val expected = Endpoint.At(Value.Finite(1))

        actual mustBe expected
      }

      "construct from Value.InfNeg" in {
        val actual   = Endpoint.at(Value.InfNeg)
        val expected = Endpoint.At(Value.InfNeg)

        actual mustBe expected
      }

      "construct from Value.InfPos" in {
        val actual   = Endpoint.at(Value.InfPos)
        val expected = Endpoint.At(Value.InfPos)

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = Endpoint.at(Value.InfPos).succ
        val expected = Endpoint.Succ(Endpoint.At(Value.InfPos))

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = Endpoint.at(Value.InfPos).pred
        val expected = Endpoint.Pred(Endpoint.At(Value.InfPos))

        actual mustBe expected
      }

      "x.value" in {
        val x = Endpoint.at(1)

        val actual   = x.value
        val expected = Value.Finite(1)

        actual mustBe expected
      }

      "x.eval" in {
        val x = Endpoint.at(1)

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual mustBe expected
      }

      "x.at" in {
        val x = Endpoint.at(1)

        val actual   = x.at
        val expected = Endpoint.at(1)

        actual mustBe expected
      }
    }

    "Endpoint.Pred" should {
      "construct from Endpoint" in {
        val actual   = Endpoint.pred(Endpoint.at(1))
        val expected = Endpoint.Pred(Endpoint.At(Value.Finite(1)))

        actual.isAt mustBe (false)
        actual.isPred mustBe (true)
        actual.isSucc mustBe (false)

        actual mustBe expected
      }

      "construct from Value" in {
        val actual   = Endpoint.pred(Value.Finite(1))
        val expected = Endpoint.Pred(Endpoint.At(Value.Finite(1)))

        actual mustBe expected
      }

      "construct from T" in {
        val actual   = Endpoint.pred(1)
        val expected = Endpoint.Pred(Endpoint.At(Value.Finite(1)))

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = Endpoint.pred(Value.InfPos).succ
        val expected = Endpoint.At(Value.InfPos)

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = Endpoint.pred(Value.InfPos).pred
        val expected = Endpoint.Pred(Endpoint.Pred(Endpoint.At(Value.InfPos)))

        actual mustBe expected
      }

      "x.eval" in {
        val x = Endpoint.pred(Endpoint.at(1))

        val actual   = x.eval
        val expected = Value.Finite(0)

        actual mustBe expected
      }

      "x.at" in {
        val x = Endpoint.pred(1)

        val actual   = x.at
        val expected = Endpoint.at(0)

        actual mustBe expected
      }

      "Pred(At(Finite(-3)) == Finite(-4)" in {
        val x = Endpoint.pred(Endpoint.at(Value.finite(-3)))

        val actual   = x.eval
        val expected = Value.finite(-4)

        actual mustBe (expected)
      }
    }

    "Endpoint.Succ" should {
      "construct from Endpoint" in {
        val actual   = Endpoint.succ(Endpoint.at(1))
        val expected = Endpoint.Succ(Endpoint.At(Value.Finite(1)))

        actual.isAt mustBe (false)
        actual.isPred mustBe (false)
        actual.isSucc mustBe (true)

        actual mustBe expected
      }

      "construct from Value" in {
        val actual   = Endpoint.succ(Value.Finite(1))
        val expected = Endpoint.Succ(Endpoint.At(Value.Finite(1)))

        actual mustBe expected
      }

      "construct from T" in {
        val actual   = Endpoint.succ(1)
        val expected = Endpoint.Succ(Endpoint.At(Value.Finite(1)))

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = Endpoint.succ(Value.InfPos).succ
        val expected = Endpoint.Succ(Endpoint.Succ(Endpoint.At(Value.InfPos)))

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = Endpoint.succ(Value.InfPos).pred
        val expected = Endpoint.At(Value.InfPos)

        actual mustBe expected
      }

      "x.eval" in {
        val x = Endpoint.succ(Endpoint.at(1))

        val actual   = x.eval
        val expected = Value.Finite(2)

        actual mustBe expected
      }

      "x.at" in {
        val x = Endpoint.succ(1)

        val actual   = x.at
        val expected = Endpoint.at(2)

        actual mustBe expected
      }
    }

    "evaluated" should {
      "get a value from Succ(Succ(x))" in {
        val x = Endpoint.succ(Endpoint.succ(1))

        val actual   = x.eval
        val expected = Value.Finite(3)

        actual mustBe expected
      }

      "get a value from Pred(Pred(x))" in {
        val x = Endpoint.succ(Endpoint.succ(1))

        val actual   = x.eval
        val expected = Value.Finite(3)

        actual mustBe expected
      }

      "get a value from Succ(Succ(Pred(Pred(x))))" in {
        val x = Endpoint.succ(Endpoint.succ(Endpoint.pred(Endpoint.pred(1))))

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual mustBe expected
      }

      "get a value from Succ(Pred(Succ(Pred(x))))" in {
        val x = Endpoint.succ(Endpoint.pred(Endpoint.succ(Endpoint.pred(1))))

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual mustBe expected
      }

      "get innerValue" in {
        val t = Table(
          ("input", "expected"),
          (Endpoint.succ(Endpoint.succ(1)), Value.finite(1)),
          (Endpoint.pred(Endpoint.pred(2)), Value.finite(2)),
          (Endpoint.succ(3), Value.finite(3)),
          (Endpoint.pred(4), Value.finite(4)),
          (Endpoint.at(5), Value.finite(5)),
        )

        forAll(t) { (input, expected) =>
          val actual = input.innerValue
          actual mustBe expected
        }
      }
    }
  }
