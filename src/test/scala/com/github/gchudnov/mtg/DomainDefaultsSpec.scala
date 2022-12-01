package com.github.gchudnov.mtg

import org.scalactic.Equality
import org.scalactic.TolerantNumerics

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final class DomainDefaultsSpec extends TestSpec:

  "Domains" when {

    "Int" should {
      "pred and succ" in {
        val valT: Domain[Int] = summon[Domain[Int]]

        val x: Int = 10

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (9)
        xn mustEqual (11)

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[Int] = summon[Domain[Int]]

        val start: Int = 10
        val end: Int   = 20

        val actual   = valT.count(start, end)
        val expected = 10

        actual mustEqual expected
      }
    }

    "Long" should {
      "pred and succ" in {
        val valT: Domain[Long] = summon[Domain[Long]]

        val x: Long = 10L

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (9L)
        xn mustEqual (11L)

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[Long] = summon[Domain[Long]]

        val start: Long = -10L
        val end: Long   = 10L

        val actual   = valT.count(start, end)
        val expected = 20

        actual mustEqual expected
      }
    }

    "Double" should {
      "pred and succ" in {
        given doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(0.001)
        given valT: Domain[Double]       = Domain.makeFractional(0.001)

        val x: Double = 10.0

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        assert(xp === 9.999)
        assert(xn === 10.001)

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        assert(actual === expected)
      }

      "count" in {
        val valT: Domain[Double] = Domain.makeFractional(1.0)

        val start: Double = -10.0
        val end: Double   = 10.0

        val actual   = valT.count(start, end)
        val expected = 20

        actual mustEqual expected
      }
    }

    "OffsetDateTime" should {
      "pred and succ" in {
        val valT: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.DAYS)

        val x: OffsetDateTime = OffsetDateTime.parse("2022-07-03T17:05:29.771Z").truncatedTo(ChronoUnit.DAYS)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (OffsetDateTime.parse("2022-07-02T00:00Z"))
        xn mustEqual (OffsetDateTime.parse("2022-07-04T00:00Z"))

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.DAYS)

        val start: OffsetDateTime = OffsetDateTime.parse("2022-07-02T00:00Z").truncatedTo(ChronoUnit.DAYS)
        val end: OffsetDateTime   = OffsetDateTime.parse("2022-07-10T00:00Z").truncatedTo(ChronoUnit.DAYS)

        val actual   = valT.count(start, end)
        val expected = 8

        actual mustEqual expected
      }
    }

    "Instant" should {
      "pred and succ" in {
        val valT: Domain[Instant] = Domain.makeInstant(ChronoUnit.DAYS)

        val x: Instant = Instant.parse("2022-07-03T17:05:29Z").truncatedTo(ChronoUnit.DAYS)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (Instant.parse("2022-07-02T00:00:00Z"))
        xn mustEqual (Instant.parse("2022-07-04T00:00:00Z"))

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[Instant] = Domain.makeInstant(ChronoUnit.DAYS)

        val start: Instant = Instant.parse("2022-07-02T00:00:00Z").truncatedTo(ChronoUnit.DAYS)
        val end: Instant   = Instant.parse("2022-07-10T00:00:00Z").truncatedTo(ChronoUnit.DAYS)

        val actual   = valT.count(start, end)
        val expected = 8

        actual
      }
    }
  }
