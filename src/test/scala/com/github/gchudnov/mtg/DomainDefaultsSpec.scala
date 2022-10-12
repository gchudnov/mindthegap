package com.github.gchudnov.mtg

import org.scalactic.Equality
import org.scalactic.TolerantNumerics

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final class DomainDefaultsSpec extends TestSpec:

  "Domains" when {

    "Int" should {
      "calc predecessor and successor values" in {
        val valT: Domain[Int] = summon[Domain[Int]]

        val x: Int = 10

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (9)
        xn mustEqual (11)
      }
    }

    "Long" should {
      "calc predecessor and successor values" in {
        val valT: Domain[Long] = summon[Domain[Long]]

        val x: Long = 10L

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (9L)
        xn mustEqual (11L)
      }
    }

    "Double" should {
      "calc predecessor and successor values" in {
        given doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(0.001)
        given valT: Domain[Double]       = Domain.makeFractional(0.001)

        val x: Double = 10.0

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        assert(xp === 9.999)
        assert(xn === 10.001)
      }
    }

    "OffsetDateTime" should {
      "calc predecessor and successor values" in {
        val valT: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.DAYS)

        val x: OffsetDateTime = OffsetDateTime.parse("2017-07-03T17:05:29.771Z").truncatedTo(ChronoUnit.DAYS)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (OffsetDateTime.parse("2017-07-02T00:00Z"))
        xn mustEqual (OffsetDateTime.parse("2017-07-04T00:00Z"))
      }
    }

    "Instant" should {
      "calc predecessor and successor values" in {
        val valT: Domain[Instant] = Domain.makeInstant(ChronoUnit.DAYS)

        val x: Instant = Instant.parse("2017-07-03T17:05:29Z").truncatedTo(ChronoUnit.DAYS)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (Instant.parse("2017-07-02T00:00:00Z"))
        xn mustEqual (Instant.parse("2017-07-04T00:00:00Z"))
      }
    }
  }
