package com.github.gchudnov.mtg.internal

import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Domain

final class EndpointSpec extends TestSpec:

  "Endpoint" when {
    "Endpoint.At" should {
      "construct from T" in {
        val actual   = Endpoint.at(1)
        val expected = Endpoint.At(Value.Finite(1))

        actual.isAt shouldBe (true)
        actual.isPred shouldBe (false)
        actual.isSucc shouldBe (false)

        actual shouldBe expected
      }

      "construct from Value.Finite" in {
        val actual   = Endpoint.at(Value.Finite(1))
        val expected = Endpoint.At(Value.Finite(1))

        actual shouldBe expected
      }

      "construct from Value.InfNeg" in {
        val actual   = Endpoint.at(Value.InfNeg)
        val expected = Endpoint.At(Value.InfNeg)

        actual shouldBe expected
      }

      "construct from Value.InfPos" in {
        val actual   = Endpoint.at(Value.InfPos)
        val expected = Endpoint.At(Value.InfPos)

        actual shouldBe expected
      }

      "x.succ" in {
        val actual   = Endpoint.at(Value.InfPos).succ
        val expected = Endpoint.Succ(Endpoint.At(Value.InfPos))

        actual shouldBe expected
      }

      "x.pred" in {
        val actual   = Endpoint.at(Value.InfPos).pred
        val expected = Endpoint.Pred(Endpoint.At(Value.InfPos))

        actual shouldBe expected
      }

      "x.value" in {
        val x = Endpoint.at(1)

        val actual   = x.value
        val expected = Value.Finite(1)

        actual shouldBe expected
      }

      "x.eval" in {
        val x = Endpoint.at(1)

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual shouldBe expected
      }

      "x.at" in {
        val x = Endpoint.at(1)

        val actual   = x.at
        val expected = Endpoint.at(1)

        actual shouldBe expected
      }
    }

    "Endpoint.Pred" should {
      "construct from Endpoint" in {
        val actual   = Endpoint.pred(Endpoint.at(1))
        val expected = Endpoint.Pred(Endpoint.At(Value.Finite(1)))

        actual.isAt shouldBe (false)
        actual.isPred shouldBe (true)
        actual.isSucc shouldBe (false)

        actual shouldBe expected
      }

      "construct from Value" in {
        val actual   = Endpoint.pred(Value.Finite(1))
        val expected = Endpoint.Pred(Endpoint.At(Value.Finite(1)))

        actual shouldBe expected
      }

      "construct from T" in {
        val actual   = Endpoint.pred(1)
        val expected = Endpoint.Pred(Endpoint.At(Value.Finite(1)))

        actual shouldBe expected
      }

      "x.succ" in {
        val actual   = Endpoint.pred(Value.InfPos).succ
        val expected = Endpoint.At(Value.InfPos)

        actual shouldBe expected
      }

      "x.pred" in {
        val actual   = Endpoint.pred(Value.InfPos).pred
        val expected = Endpoint.Pred(Endpoint.Pred(Endpoint.At(Value.InfPos)))

        actual shouldBe expected
      }

      "x.eval" in {
        val x = Endpoint.pred(Endpoint.at(1))

        val actual   = x.eval
        val expected = Value.Finite(0)

        actual shouldBe expected
      }

      "x.at" in {
        val x = Endpoint.pred(1)

        val actual   = x.at
        val expected = Endpoint.at(0)

        actual shouldBe expected
      }

      "Pred(At(Finite(-3)) == Finite(-4)" in {
        val x = Endpoint.pred(Endpoint.at(Value.finite(-3)))

        val actual   = x.eval
        val expected = Value.finite(-4)

        actual shouldBe (expected)
      }
    }

    "Endpoint.Succ" should {
      "construct from Endpoint" in {
        val actual   = Endpoint.succ(Endpoint.at(1))
        val expected = Endpoint.Succ(Endpoint.At(Value.Finite(1)))

        actual.isAt shouldBe (false)
        actual.isPred shouldBe (false)
        actual.isSucc shouldBe (true)

        actual shouldBe expected
      }

      "construct from Value" in {
        val actual   = Endpoint.succ(Value.Finite(1))
        val expected = Endpoint.Succ(Endpoint.At(Value.Finite(1)))

        actual shouldBe expected
      }

      "construct from T" in {
        val actual   = Endpoint.succ(1)
        val expected = Endpoint.Succ(Endpoint.At(Value.Finite(1)))

        actual shouldBe expected
      }

      "x.succ" in {
        val actual   = Endpoint.succ(Value.InfPos).succ
        val expected = Endpoint.Succ(Endpoint.Succ(Endpoint.At(Value.InfPos)))

        actual shouldBe expected
      }

      "x.pred" in {
        val actual   = Endpoint.succ(Value.InfPos).pred
        val expected = Endpoint.At(Value.InfPos)

        actual shouldBe expected
      }

      "x.eval" in {
        val x = Endpoint.succ(Endpoint.at(1))

        val actual   = x.eval
        val expected = Value.Finite(2)

        actual shouldBe expected
      }

      "x.at" in {
        val x = Endpoint.succ(1)

        val actual   = x.at
        val expected = Endpoint.at(2)

        actual shouldBe expected
      }
    }

    "evaluated" should {
      "get a value from Succ(Succ(x))" in {
        val x = Endpoint.succ(Endpoint.succ(1))

        val actual   = x.eval
        val expected = Value.Finite(3)

        actual shouldBe expected
      }

      "get a value from Pred(Pred(x))" in {
        val x = Endpoint.succ(Endpoint.succ(1))

        val actual   = x.eval
        val expected = Value.Finite(3)

        actual shouldBe expected
      }

      "get a value from Succ(Succ(Pred(Pred(x))))" in {
        val x = Endpoint.succ(Endpoint.succ(Endpoint.pred(Endpoint.pred(1))))

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual shouldBe expected
      }

      "get a value from Succ(Pred(Succ(Pred(x))))" in {
        val x = Endpoint.succ(Endpoint.pred(Endpoint.succ(Endpoint.pred(1))))

        val actual   = x.eval
        val expected = Value.Finite(1)

        actual shouldBe expected
      }

      "unwrap" in {
        val t = Table(
          ("input", "expected"),
          (Endpoint.succ(Endpoint.succ(1)), Value.finite(1)),
          (Endpoint.pred(Endpoint.pred(2)), Value.finite(2)),
          (Endpoint.succ(3), Value.finite(3)),
          (Endpoint.pred(4), Value.finite(4)),
          (Endpoint.at(5), Value.finite(5)),
        )

        forAll(t) { (input, expected) =>
          val actual = input.unwrap
          actual shouldBe expected
        }
      }
    }
  }
