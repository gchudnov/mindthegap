package com.github.gchudnov.mtg.ordering

import com.github.gchudnov.mtg.Endpoint
import com.github.gchudnov.mtg.Value
import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.TestSpec

final class MarkOrderingSpec extends TestSpec:

  given ordM: Ordering[Endpoint[Int]] = summon[Domain[Int]].ordEndpoint

  "MarkOrdering" when {
    "ordM.compare(a, b)" should {
      "compare" in {
        val a = Endpoint.at(1)
        val b = Endpoint.succ(a)
        val c = Endpoint.pred(a)

        ordM.compare(a, a) mustBe (0)
        ordM.compare(a, b) mustBe (-1)
        ordM.compare(a, c) mustBe (1)
        ordM.compare(b, a) mustBe (1)
        ordM.compare(b, b) mustBe (0)
        ordM.compare(b, c) mustBe (1)
        ordM.compare(c, a) mustBe (-1)
        ordM.compare(c, b) mustBe (-1)
        ordM.compare(c, c) mustBe (0)
      }

      "compare value with infinity" in {
        val a = Endpoint.at(Value.finite(1))
        val b = Endpoint.at[Int](Value.infNeg)
        val c = Endpoint.at[Int](Value.infPos)

        ordM.compare(a, a) mustBe (0)
        ordM.compare(a, b) mustBe (1)
        ordM.compare(a, c) mustBe (-1)
        ordM.compare(b, a) mustBe (-1)
        ordM.compare(b, b) mustBe (0)
        ordM.compare(b, c) mustBe (-1)
        ordM.compare(c, a) mustBe (1)
        ordM.compare(c, b) mustBe (1)
        ordM.compare(c, c) mustBe (0)
      }

      "compare (lt) value with infinity" in {
        val a = Endpoint.at(Value.finite(1))
        val b = Endpoint.at[Int](Value.infNeg)
        val c = Endpoint.at[Int](Value.infPos)

        ordM.lt(a, a) mustBe (false)
        ordM.lt(a, b) mustBe (false)
        ordM.lt(a, c) mustBe (true)
        ordM.lt(b, a) mustBe (true)
        ordM.lt(b, b) mustBe (false)
        ordM.lt(b, c) mustBe (true)
        ordM.lt(c, a) mustBe (false)
        ordM.lt(c, b) mustBe (false)
        ordM.lt(c, c) mustBe (false)
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
