package com.github.gchudnov.mtg

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

final class EndpointSpec extends TestSpec:

  "Endpoint" when {
    "Endpoint.At" should {
      "construct from T" in {
        val actual   = internal.Endpoint.at(1)
        val expected = internal.Endpoint.At(internal.Value.Finite(1))

        actual.isAt mustBe (true)
        actual.isPred mustBe (false)
        actual.isSucc mustBe (false)

        actual mustBe expected
      }

      "construct from Value.Finite" in {
        val actual   = internal.Endpoint.at(internal.Value.Finite(1))
        val expected = internal.Endpoint.At(internal.Value.Finite(1))

        actual mustBe expected
      }

      "construct from Value.InfNeg" in {
        val actual   = internal.Endpoint.at(internal.Value.InfNeg)
        val expected = internal.Endpoint.At(internal.Value.InfNeg)

        actual mustBe expected
      }

      "construct from Value.InfPos" in {
        val actual   = internal.Endpoint.at(internal.Value.InfPos)
        val expected = internal.Endpoint.At(internal.Value.InfPos)

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = internal.Endpoint.at(internal.Value.InfPos).succ
        val expected = internal.Endpoint.Succ(internal.Endpoint.At(internal.Value.InfPos))

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = internal.Endpoint.at(internal.Value.InfPos).pred
        val expected = internal.Endpoint.Pred(internal.Endpoint.At(internal.Value.InfPos))

        actual mustBe expected
      }

      "x.value" in {
        val x = internal.Endpoint.at(1)

        val actual   = x.value
        val expected = internal.Value.Finite(1)

        actual mustBe expected
      }

      "x.eval" in {
        val x = internal.Endpoint.at(1)

        val actual   = x.eval
        val expected = internal.Value.Finite(1)

        actual mustBe expected
      }

      "x.at" in {
        val x = internal.Endpoint.at(1)

        val actual   = x.at
        val expected = internal.Endpoint.at(1)

        actual mustBe expected
      }
    }

    "Endpoint.Pred" should {
      "construct from Endpoint" in {
        val actual   = internal.Endpoint.pred(internal.Endpoint.at(1))
        val expected = internal.Endpoint.Pred(internal.Endpoint.At(internal.Value.Finite(1)))

        actual.isAt mustBe (false)
        actual.isPred mustBe (true)
        actual.isSucc mustBe (false)

        actual mustBe expected
      }

      "construct from Value" in {
        val actual   = internal.Endpoint.pred(internal.Value.Finite(1))
        val expected = internal.Endpoint.Pred(internal.Endpoint.At(internal.Value.Finite(1)))

        actual mustBe expected
      }

      "construct from T" in {
        val actual   = internal.Endpoint.pred(1)
        val expected = internal.Endpoint.Pred(internal.Endpoint.At(internal.Value.Finite(1)))

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = internal.Endpoint.pred(internal.Value.InfPos).succ
        val expected = internal.Endpoint.At(internal.Value.InfPos)

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = internal.Endpoint.pred(internal.Value.InfPos).pred
        val expected = internal.Endpoint.Pred(internal.Endpoint.Pred(internal.Endpoint.At(internal.Value.InfPos)))

        actual mustBe expected
      }

      "x.eval" in {
        val x = internal.Endpoint.pred(internal.Endpoint.at(1))

        val actual   = x.eval
        val expected = internal.Value.Finite(0)

        actual mustBe expected
      }

      "x.at" in {
        val x = internal.Endpoint.pred(1)

        val actual   = x.at
        val expected = internal.Endpoint.at(0)

        actual mustBe expected
      }

      "Pred(At(Finite(-3)) == Finite(-4)" in {
        val x = internal.Endpoint.pred(internal.Endpoint.at(internal.Value.finite(-3)))

        val actual   = x.eval
        val expected = internal.Value.finite(-4)

        actual mustBe (expected)
      }
    }

    "Endpoint.Succ" should {
      "construct from Endpoint" in {
        val actual   = internal.Endpoint.succ(internal.Endpoint.at(1))
        val expected = internal.Endpoint.Succ(internal.Endpoint.At(internal.Value.Finite(1)))

        actual.isAt mustBe (false)
        actual.isPred mustBe (false)
        actual.isSucc mustBe (true)

        actual mustBe expected
      }

      "construct from Value" in {
        val actual   = internal.Endpoint.succ(internal.Value.Finite(1))
        val expected = internal.Endpoint.Succ(internal.Endpoint.At(internal.Value.Finite(1)))

        actual mustBe expected
      }

      "construct from T" in {
        val actual   = internal.Endpoint.succ(1)
        val expected = internal.Endpoint.Succ(internal.Endpoint.At(internal.Value.Finite(1)))

        actual mustBe expected
      }

      "x.succ" in {
        val actual   = internal.Endpoint.succ(internal.Value.InfPos).succ
        val expected = internal.Endpoint.Succ(internal.Endpoint.Succ(internal.Endpoint.At(internal.Value.InfPos)))

        actual mustBe expected
      }

      "x.pred" in {
        val actual   = internal.Endpoint.succ(internal.Value.InfPos).pred
        val expected = internal.Endpoint.At(internal.Value.InfPos)

        actual mustBe expected
      }

      "x.eval" in {
        val x = internal.Endpoint.succ(internal.Endpoint.at(1))

        val actual   = x.eval
        val expected = internal.Value.Finite(2)

        actual mustBe expected
      }

      "x.at" in {
        val x = internal.Endpoint.succ(1)

        val actual   = x.at
        val expected = internal.Endpoint.at(2)

        actual mustBe expected
      }
    }

    "evaluated" should {
      "get a value from Succ(Succ(x))" in {
        val x = internal.Endpoint.succ(internal.Endpoint.succ(1))

        val actual   = x.eval
        val expected = internal.Value.Finite(3)

        actual mustBe expected
      }

      "get a value from Pred(Pred(x))" in {
        val x = internal.Endpoint.succ(internal.Endpoint.succ(1))

        val actual   = x.eval
        val expected = internal.Value.Finite(3)

        actual mustBe expected
      }

      "get a value from Succ(Succ(Pred(Pred(x))))" in {
        val x = internal.Endpoint.succ(internal.Endpoint.succ(internal.Endpoint.pred(internal.Endpoint.pred(1))))

        val actual   = x.eval
        val expected = internal.Value.Finite(1)

        actual mustBe expected
      }

      "get a value from Succ(Pred(Succ(Pred(x))))" in {
        val x = internal.Endpoint.succ(internal.Endpoint.pred(internal.Endpoint.succ(internal.Endpoint.pred(1))))

        val actual   = x.eval
        val expected = internal.Value.Finite(1)

        actual mustBe expected
      }

      "get innerValue" in {
        val t = Table(
          ("input", "expected"),
          (internal.Endpoint.succ(internal.Endpoint.succ(1)), internal.Value.finite(1)),
          (internal.Endpoint.pred(internal.Endpoint.pred(2)), internal.Value.finite(2)),
          (internal.Endpoint.succ(3), internal.Value.finite(3)),
          (internal.Endpoint.pred(4), internal.Value.finite(4)),
          (internal.Endpoint.at(5), internal.Value.finite(5)),
        )

        forAll(t) { (input, expected) =>
          val actual = input.innerValue
          actual mustBe expected
        }
      }
    }
  }
