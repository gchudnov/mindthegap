package com.github.gchudnov.mtg

final class ValueSpec extends TestSpec:

  "Value" when {

    "constructed" should {
      "construct Value.Finite" in {
        val actual   = internal.Value.finite(1)
        val expected = internal.Value.Finite(1)

        actual.isFinite mustBe (true)
        actual.isInfNeg mustBe (false)
        actual.isInfPos mustBe (false)

        actual mustBe expected
      }

      "construct Value.InfNeg" in {
        val actual   = internal.Value.infNeg
        val expected = internal.Value.InfNeg

        actual.isFinite mustBe (false)
        actual.isInfNeg mustBe (true)
        actual.isInfPos mustBe (false)

        actual mustBe expected
      }

      "construct Value.InfPos" in {
        val actual   = internal.Value.infPos
        val expected = internal.Value.InfPos

        actual.isFinite mustBe (false)
        actual.isInfNeg mustBe (false)
        actual.isInfPos mustBe (true)

        actual mustBe expected
      }
    }

    "Value.Finite(x)" should {
      "check the properties" in {
        val x = internal.Value.Finite(1)

        x.isFinite mustBe (true)
        x.isInfNeg mustBe (false)
        x.isInfPos mustBe (false)

        x match
          case internal.Value.Finite(y) =>
            y mustBe (1)
          case other =>
            fail(s"expected to match with Value.Finite(y), got ${other}")
      }

      "x.succ" in {
        val x = internal.Value.Finite(1)
        x.succ mustBe internal.Value.Finite(2)
      }

      "x.pred" in {
        val x = internal.Value.Finite(1)
        x.pred mustBe internal.Value.Finite(0)
      }
    }

    "Value.InfNeg" should {
      "check the properties" in {
        val x = internal.Value.InfNeg

        x.isFinite mustBe (false)
        x.isInfNeg mustBe (true)
        x.isInfPos mustBe (false)

        x match
          case internal.Value.InfNeg =>
          // no-op
          case other =>
            fail(s"expected to match with Value.InfNeg, got ${other}")
      }

      "x.succ" in {
        val x = internal.Value.InfNeg
        x.succ[Int] mustBe internal.Value.InfNeg
      }

      "x.pred" in {
        val x = internal.Value.InfNeg
        x.pred[Int] mustBe internal.Value.InfNeg
      }
    }

    "Value.InfPos" should {
      "check the properties" in {
        val x = internal.Value.InfPos

        x.isFinite mustBe (false)
        x.isInfNeg mustBe (false)
        x.isInfPos mustBe (true)

        x match
          case internal.Value.InfPos =>
          // no-op
          case other =>
            fail(s"expected to match with Value.InfPos, got ${other}")
      }

      "x.succ" in {
        val x = internal.Value.InfPos
        x.succ[Int] mustBe internal.Value.InfPos
      }

      "x.pred" in {
        val x = internal.Value.InfPos
        x.pred[Int] mustBe internal.Value.InfPos
      }
    }
  }
