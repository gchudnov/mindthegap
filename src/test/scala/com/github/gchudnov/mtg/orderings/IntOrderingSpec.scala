package com.github.gchudnov.mtg.orderings

import com.github.gchudnov.mtg.TestSpec

final class IntOrderingSpec extends TestSpec:
  "IntOrdering" when {
    "Ints are ordered" should {
      "return them in a sorted order" in {
        val xs = List(6, 3, 2, 4, 1, 5)

        val actual   = xs.sorted
        val expected = List(1, 2, 3, 4, 5, 6)

        actual mustBe expected
      }
    }
  }
