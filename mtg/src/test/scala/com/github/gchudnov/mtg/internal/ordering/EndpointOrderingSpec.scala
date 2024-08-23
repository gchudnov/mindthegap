package com.github.gchudnov.mtg.internal.ordering

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

        ordE.compare(a, a) shouldBe (0)
        ordE.compare(a, b) shouldBe (-1)
        ordE.compare(a, c) shouldBe (1)
        ordE.compare(b, a) shouldBe (1)
        ordE.compare(b, b) shouldBe (0)
        ordE.compare(b, c) shouldBe (1)
        ordE.compare(c, a) shouldBe (-1)
        ordE.compare(c, b) shouldBe (-1)
        ordE.compare(c, c) shouldBe (0)
      }

      "compare value with infinity" in {
        val a = Endpoint.at(Value.finite(1))
        val b = Endpoint.at[Int](Value.infNeg)
        val c = Endpoint.at[Int](Value.infPos)

        ordE.compare(a, a) shouldBe (0)
        ordE.compare(a, b) shouldBe (1)
        ordE.compare(a, c) shouldBe (-1)
        ordE.compare(b, a) shouldBe (-1)
        ordE.compare(b, b) shouldBe (0)
        ordE.compare(b, c) shouldBe (-1)
        ordE.compare(c, a) shouldBe (1)
        ordE.compare(c, b) shouldBe (1)
        ordE.compare(c, c) shouldBe (0)
      }

      "compare (lt) value with infinity" in {
        val a = Endpoint.at(Value.finite(1))
        val b = Endpoint.at[Int](Value.infNeg)
        val c = Endpoint.at[Int](Value.infPos)

        ordE.lt(a, a) shouldBe (false)
        ordE.lt(a, b) shouldBe (false)
        ordE.lt(a, c) shouldBe (true)
        ordE.lt(b, a) shouldBe (true)
        ordE.lt(b, b) shouldBe (false)
        ordE.lt(b, c) shouldBe (true)
        ordE.lt(c, a) shouldBe (false)
        ordE.lt(c, b) shouldBe (false)
        ordE.lt(c, c) shouldBe (false)
      }
    }

    "complex values" should {
      "order them" in {
        val xs = List[Endpoint[Int]](Endpoint.at(1), Endpoint.succ(Endpoint.succ(1)), Endpoint.pred(1), Endpoint.pred(Endpoint.pred(1)))

        val actual = xs.sorted
        val expected =
          List[Endpoint[Int]](Endpoint.pred(Endpoint.pred(1)), Endpoint.pred(1), Endpoint.at(1), Endpoint.succ(Endpoint.succ(1)))

        actual should contain theSameElementsAs (expected)
      }
    }
  }
