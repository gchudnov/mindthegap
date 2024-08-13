package com.github.gchudnov.mtg.internal.ordering

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.TestSpec

final class ValueOrderingSpec extends TestSpec:

  given ordV: Ordering[Value[Int]] = summon[Domain[Int]].ordValue

  "ValueOrdering" when {
    "ordV.compare(a, b)" should {
      "return the expected results" in {
        ordV.compare(Value.InfNeg, Value.InfNeg) shouldBe (0)
        ordV.compare(Value.InfNeg, Value.InfPos) shouldBe (-1)
        ordV.compare(Value.InfNeg, Value.Finite(0)) shouldBe (-1)

        ordV.compare(Value.InfPos, Value.InfNeg) shouldBe (1)
        ordV.compare(Value.InfPos, Value.InfPos) shouldBe (0)
        ordV.compare(Value.InfPos, Value.Finite(0)) shouldBe (1)

        ordV.compare(Value.Finite(0), Value.InfNeg) shouldBe (1)
        ordV.compare(Value.Finite(0), Value.InfPos) shouldBe (-1)
        ordV.compare(Value.Finite(0), Value.Finite(0)) shouldBe (0)

      }
    }

    "sort" should {
      "order values" in {
        val xs = List(Value.InfPos, Value.Finite(1), Value.Finite(-1), Value.Finite(0), Value.InfNeg)

        val actual   = xs.sorted
        val expected = List(Value.InfNeg, Value.Finite(-1), Value.Finite(0), Value.Finite(1), Value.InfPos)

        actual should contain theSameElementsAs (expected)
      }
    }

    "lt" should {
      "compare" in {
        ordV.lt(Value.InfNeg, Value.InfNeg) shouldBe (false)
        ordV.lt(Value.InfNeg, Value.InfPos) shouldBe (true)
        ordV.lt(Value.InfNeg, Value.Finite(0)) shouldBe (true)

        ordV.lt(Value.InfPos, Value.InfNeg) shouldBe (false)
        ordV.lt(Value.InfPos, Value.InfPos) shouldBe (false)
        ordV.lt(Value.InfPos, Value.Finite(0)) shouldBe (false)

        ordV.lt(Value.Finite(0), Value.InfNeg) shouldBe (false)
        ordV.lt(Value.Finite(0), Value.InfPos) shouldBe (true)
        ordV.lt(Value.Finite(0), Value.Finite(0)) shouldBe (false)
      }
    }
  }
