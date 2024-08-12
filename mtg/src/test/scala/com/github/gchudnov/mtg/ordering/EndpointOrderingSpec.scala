package com.github.gchudnov.mtg.ordering

import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.internal.Value
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec

final class EndpointOrderingSpec extends TestSpec:

  given ordE: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "EndpointOrdering" when {
    "ordE.compare(a, b)" should {
      "compare" in {
        val a = Endpoint.at(1)
        val b = Endpoint.succ(a)
        val c = Endpoint.pred(a)

        ordE.compare(a, a) mustBe (0)
        ordE.compare(a, b) mustBe (-1)
        ordE.compare(a, c) mustBe (1)
        ordE.compare(b, a) mustBe (1)
        ordE.compare(b, b) mustBe (0)
        ordE.compare(b, c) mustBe (1)
        ordE.compare(c, a) mustBe (-1)
        ordE.compare(c, b) mustBe (-1)
        ordE.compare(c, c) mustBe (0)
      }

      "compare value with infinity" in {
        val a = Endpoint.at(Value.finite(1))
        val b = Endpoint.at[Int](Value.infNeg)
        val c = Endpoint.at[Int](Value.infPos)

        ordE.compare(a, a) mustBe (0)
        ordE.compare(a, b) mustBe (1)
        ordE.compare(a, c) mustBe (-1)
        ordE.compare(b, a) mustBe (-1)
        ordE.compare(b, b) mustBe (0)
        ordE.compare(b, c) mustBe (-1)
        ordE.compare(c, a) mustBe (1)
        ordE.compare(c, b) mustBe (1)
        ordE.compare(c, c) mustBe (0)
      }

      "compare (lt) value with infinity" in {
        val a = Endpoint.at(Value.finite(1))
        val b = Endpoint.at[Int](Value.infNeg)
        val c = Endpoint.at[Int](Value.infPos)

        ordE.lt(a, a) mustBe (false)
        ordE.lt(a, b) mustBe (false)
        ordE.lt(a, c) mustBe (true)
        ordE.lt(b, a) mustBe (true)
        ordE.lt(b, b) mustBe (false)
        ordE.lt(b, c) mustBe (true)
        ordE.lt(c, a) mustBe (false)
        ordE.lt(c, b) mustBe (false)
        ordE.lt(c, c) mustBe (false)
      }
    }

    "complex values" should {
      "order them" in {
        val xs = List[Endpoint[Int]](Endpoint.at(1), Endpoint.succ(Endpoint.succ(1)), Endpoint.pred(1), Endpoint.pred(Endpoint.pred(1)))

        val actual   = xs.sorted
        val expected = List[Endpoint[Int]](Endpoint.pred(Endpoint.pred(1)), Endpoint.pred(1), Endpoint.at(1), Endpoint.succ(Endpoint.succ(1)))

        actual must contain theSameElementsAs (expected)
      }
    }
  }
