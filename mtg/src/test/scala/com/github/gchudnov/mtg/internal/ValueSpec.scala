package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Domain

final class ValueSpec extends TestSpec:

  "Value" when {

    "constructed" should {
      "construct Value.Finite" in {
        val actual   = Value.finite(1)
        val expected = Value.Finite(1)

        actual.isFinite shouldBe (true)
        actual.isInfNeg shouldBe (false)
        actual.isInfPos shouldBe (false)
        actual.isInf shouldBe (false)

        actual shouldBe expected
      }

      "construct Value.InfNeg" in {
        val actual   = Value.infNeg
        val expected = Value.InfNeg

        actual.isFinite shouldBe (false)
        actual.isInfNeg shouldBe (true)
        actual.isInfPos shouldBe (false)
        actual.isInf shouldBe (true)

        actual shouldBe expected
      }

      "construct Value.InfPos" in {
        val actual   = Value.infPos
        val expected = Value.InfPos

        actual.isFinite shouldBe (false)
        actual.isInfNeg shouldBe (false)
        actual.isInfPos shouldBe (true)
        actual.isInf shouldBe (true)

        actual shouldBe expected
      }
    }

    "Value.Finite(x)" should {
      "check the properties" in {
        val x = Value.Finite(1)

        x.isFinite shouldBe (true)
        x.isInfNeg shouldBe (false)
        x.isInfPos shouldBe (false)
        x.isInf shouldBe (false)

        x match
          case Value.Finite(y) =>
            y shouldBe (1)
          case other =>
            fail(s"expected to match with Value.Finite(y), got ${other}")
      }

      "x.succ" in {
        val x = Value.Finite(1)
        x.succ shouldBe Value.Finite(2)
      }

      "x.pred" in {
        val x = Value.Finite(1)
        x.pred shouldBe Value.Finite(0)
      }
    }

    "Value.InfNeg" should {
      "check the properties" in {
        val x = Value.InfNeg

        x.isFinite shouldBe (false)
        x.isInfNeg shouldBe (true)
        x.isInfPos shouldBe (false)
        x.isInf shouldBe (true)

        x match
          case Value.InfNeg =>
          // no-op
          case other =>
            fail(s"expected to match with Value.InfNeg, got ${other}")
      }

      "x.succ" in {
        val x = Value.InfNeg
        x.succ[Int] shouldBe Value.InfNeg
      }

      "x.pred" in {
        val x = Value.InfNeg
        x.pred[Int] shouldBe Value.InfNeg
      }
    }

    "Value.InfPos" should {
      "check the properties" in {
        val x = Value.InfPos

        x.isFinite shouldBe (false)
        x.isInfNeg shouldBe (false)
        x.isInfPos shouldBe (true)
        x.isInf shouldBe (true)

        x match
          case Value.InfPos =>
          // no-op
          case other =>
            fail(s"expected to match with Value.InfPos, got ${other}")
      }

      "x.succ" in {
        val x = Value.InfPos
        x.succ[Int] shouldBe Value.InfPos
      }

      "x.pred" in {
        val x = Value.InfPos
        x.pred[Int] shouldBe Value.InfPos
      }
    }

    "get" should {
      "return the value if finite" in {
        val x = Value.Finite(1)
        x.get shouldBe (1)
      }

      "throws an exception if negative infinity" in {
        val x = Value.InfNeg
        assertThrows[NoSuchElementException] {
          x.get
        }
      }

      "throws an exception if positive infinity" in {
        val x = Value.InfPos
        assertThrows[NoSuchElementException] {
          x.get
        }
      }
    }
  }
