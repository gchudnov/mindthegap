package com.github.gchudnov.mtg.orderings

import com.github.gchudnov.mtg.TestSpec
import java.time.Instant
import java.time.temporal.ChronoUnit

final class InstantOrderingSpec extends TestSpec:
  "InstantOrdering" when {
    "Instances are ordered" should {
      "return them in the sorted order" in {
        val t0 = Instant.parse("2018-11-30T18:35:24.00Z")

        val xs = List(t0.plus(6, ChronoUnit.DAYS), t0.plus(1, ChronoUnit.DAYS), t0.plus(3, ChronoUnit.DAYS))

        val actual   = xs.sorted
        val expected = List(t0.plus(1, ChronoUnit.DAYS), t0.plus(3, ChronoUnit.DAYS), t0.plus(6, ChronoUnit.DAYS))

        actual mustBe expected
      }
    }
  }
