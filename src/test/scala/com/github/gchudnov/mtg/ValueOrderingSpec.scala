package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec

final class ValueOrderingSpec extends TestSpec:

  val ordV: Ordering[Value[Int]] = summon[Ordering[Value[Int]]]

  "ValueOrdering" when {
    "ordV.compare(a, b)" should {
      "return an expected results" in {
        ordV.compare(Value.InfNeg, Value.InfNeg) mustBe (0)
        ordV.compare(Value.InfNeg, Value.InfPos) mustBe (-1)
        ordV.compare(Value.InfNeg, Value.Finite(0)) mustBe (-1)

        ordV.compare(Value.InfPos, Value.InfNeg) mustBe (1)
        ordV.compare(Value.InfPos, Value.InfPos) mustBe (0)
        ordV.compare(Value.InfPos, Value.Finite(0)) mustBe (1)

        ordV.compare(Value.Finite(0), Value.InfNeg) mustBe (1)
        ordV.compare(Value.Finite(0), Value.InfPos) mustBe (-1)
        ordV.compare(Value.Finite(0), Value.Finite(0)) mustBe (0)

      }
    }

    "sort" should {
      "order values" in {
        val xs = List(Value.InfPos, Value.Finite(1), Value.Finite(-1), Value.Finite(0), Value.InfNeg)

        val actual   = xs.sorted
        val expected = List(Value.InfNeg, Value.Finite(-1), Value.Finite(0), Value.Finite(1), Value.InfPos)

        actual must contain theSameElementsAs (expected)
      }
    }

    "lt" should {
      "compare" in {
        ordV.lt(Value.InfNeg, Value.InfNeg) mustBe (false)
        ordV.lt(Value.InfNeg, Value.InfPos) mustBe (true)
        ordV.lt(Value.InfNeg, Value.Finite(0)) mustBe (true)

        ordV.lt(Value.InfPos, Value.InfNeg) mustBe (false)
        ordV.lt(Value.InfPos, Value.InfPos) mustBe (false)
        ordV.lt(Value.InfPos, Value.Finite(0)) mustBe (false)

        ordV.lt(Value.Finite(0), Value.InfNeg) mustBe (false)
        ordV.lt(Value.Finite(0), Value.InfPos) mustBe (true)
        ordV.lt(Value.Finite(0), Value.Finite(0)) mustBe (false)
      }
    }
  }
