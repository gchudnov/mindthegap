package com.github.gchudnov.mtg

import org.scalactic.Equality
import org.scalactic.TolerantNumerics

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.OffsetTime
import java.time.LocalTime
import java.time.LocalDate

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

        val x: Int = 10
        val y: Int = 20

        val actual   = valT.count(start = x, end = y)
        val expected = 10

        actual mustEqual expected
      }

      "compare" in {
        val valT: Domain[Int] = summon[Domain[Int]]

        val x = 10
        val y = 20

        val actual = valT.compare(x, y)

        (actual < 0) mustBe true
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

        val x: Long = -10L
        val y: Long = 10L

        val actual   = valT.count(start = x, end = y)
        val expected = 20

        actual mustEqual expected
      }

      "compare" in {
        val valT: Domain[Long] = summon[Domain[Long]]

        val x = 2L
        val y = 1L

        val actual = valT.compare(x, y)

        (actual > 0) mustBe true
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

        val x: Double = -10.0
        val y: Double = 10.0

        val actual   = valT.count(start = x, end = y)
        val expected = 20

        actual mustEqual expected
      }

      "compare" in {
        given valT: Domain[Double] = Domain.makeFractional(0.1)

        val x = 1.0
        val y = 1.0

        val actual = valT.compare(x, y)

        (actual == 0) mustBe true
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

        val x: OffsetDateTime = OffsetDateTime.parse("2022-07-02T00:00Z").truncatedTo(ChronoUnit.DAYS)
        val y: OffsetDateTime = OffsetDateTime.parse("2022-07-10T00:00Z").truncatedTo(ChronoUnit.DAYS)

        val actual   = valT.count(start = x, end = y)
        val expected = 8

        actual mustEqual expected
      }

      "compare" in {
        val valT: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.DAYS)

        val x: OffsetDateTime = OffsetDateTime.parse("2022-07-02T00:00Z").truncatedTo(ChronoUnit.DAYS)
        val y: OffsetDateTime = OffsetDateTime.parse("2022-07-10T00:00Z").truncatedTo(ChronoUnit.DAYS)

        val actual = valT.compare(x, y)
        (actual < 0) mustBe true
      }
    }

    "OffsetTime" should {
      "pred and succ" in {
        val valT: Domain[OffsetTime] = Domain.makeOffsetTime(ChronoUnit.MINUTES)

        val x: OffsetTime = OffsetTime.parse("18:20:28.661Z").truncatedTo(ChronoUnit.MINUTES)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (OffsetTime.parse("18:19:00.000Z"))
        xn mustEqual (OffsetTime.parse("18:21:00.000Z"))

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[OffsetTime] = Domain.makeOffsetTime(ChronoUnit.MINUTES)

        val x: OffsetTime = OffsetTime.parse("18:19:00.000Z").truncatedTo(ChronoUnit.MINUTES)
        val y: OffsetTime = OffsetTime.parse("18:21:00.000Z").truncatedTo(ChronoUnit.MINUTES)

        val actual   = valT.count(start = x, end = y)
        val expected = 2

        actual mustEqual expected
      }

      "compare" in {
        val valT: Domain[OffsetTime] = Domain.makeOffsetTime(ChronoUnit.MINUTES)

        val x: OffsetTime = OffsetTime.parse("18:19:00.000Z").truncatedTo(ChronoUnit.MINUTES)
        val y: OffsetTime = OffsetTime.parse("18:21:00.000Z").truncatedTo(ChronoUnit.MINUTES)

        val actual = valT.compare(x, y)
        (actual < 0) mustBe true
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

        val x: Instant = Instant.parse("2022-07-02T00:00:00Z").truncatedTo(ChronoUnit.DAYS)
        val y: Instant = Instant.parse("2022-07-10T00:00:00Z").truncatedTo(ChronoUnit.DAYS)

        val actual   = valT.count(start = x, end = y)
        val expected = 8

        actual
      }

      "compare" in {
        val valT: Domain[Instant] = Domain.makeInstant(ChronoUnit.DAYS)

        val x: Instant = Instant.parse("2022-06-10T00:00:00Z").truncatedTo(ChronoUnit.DAYS)
        val y: Instant = Instant.parse("2022-07-10T00:00:00Z").truncatedTo(ChronoUnit.DAYS)

        val actual = valT.compare(x, y)

        (actual < 0) mustBe true
      }
    }

    "LocalDateTime" should {
      "pred and succ" in {
        val valT: Domain[LocalDateTime] = Domain.makeLocalDateTime(ChronoUnit.DAYS)

        val x: LocalDateTime = LocalDateTime.parse("2019-02-03T00:00:00.000").truncatedTo(ChronoUnit.DAYS)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (LocalDateTime.parse("2019-02-02T00:00"))
        xn mustEqual (LocalDateTime.parse("2019-02-04T00:00"))

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[LocalDateTime] = Domain.makeLocalDateTime(ChronoUnit.DAYS)

        val x: LocalDateTime = LocalDateTime.parse("2022-07-02T00:00").truncatedTo(ChronoUnit.DAYS)
        val y: LocalDateTime = LocalDateTime.parse("2022-07-10T00:00").truncatedTo(ChronoUnit.DAYS)

        val actual   = valT.count(start = x, end = y)
        val expected = 8

        actual mustEqual expected
      }

      "compare" in {
        val valT: Domain[LocalDateTime] = Domain.makeLocalDateTime(ChronoUnit.DAYS)

        val x: LocalDateTime = LocalDateTime.parse("2022-07-02T00:00").truncatedTo(ChronoUnit.DAYS)
        val y: LocalDateTime = LocalDateTime.parse("2022-07-10T00:00").truncatedTo(ChronoUnit.DAYS)

        val actual = valT.compare(x, y)
        (actual < 0) mustBe true
      }
    }

    "LocalDate" should {
      "pred and succ" in {
        val valT: Domain[LocalDate] = Domain.makeLocalDate(ChronoUnit.DAYS)

        val x: LocalDate = LocalDate.parse("2023-02-03")

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (LocalDate.parse("2023-02-02"))
        xn mustEqual (LocalDate.parse("2023-02-04"))

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[LocalDate] = Domain.makeLocalDate(ChronoUnit.DAYS)

        val x: LocalDate = LocalDate.parse("2023-02-03")
        val y: LocalDate = LocalDate.parse("2023-02-04")

        val actual   = valT.count(start = x, end = y)
        val expected = 1

        actual mustEqual expected
      }

      "compare" in {
        val valT: Domain[LocalDate] = Domain.makeLocalDate(ChronoUnit.DAYS)

        val x: LocalDate = LocalDate.parse("2023-02-03")
        val y: LocalDate = LocalDate.parse("2023-02-04")

        val actual = valT.compare(x, y)
        (actual < 0) mustBe true
      }
    }

    "LocalTime" should {
      "pred and succ" in {
        val valT: Domain[LocalTime] = Domain.makeLocalTime(ChronoUnit.MINUTES)

        val x: LocalTime = LocalTime.parse("18:20:28.661").truncatedTo(ChronoUnit.MINUTES)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (LocalTime.parse("18:19:00"))
        xn mustEqual (LocalTime.parse("18:21:00"))

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[LocalTime] = Domain.makeLocalTime(ChronoUnit.MINUTES)

        val x: LocalTime = LocalTime.parse("18:19:00").truncatedTo(ChronoUnit.MINUTES)
        val y: LocalTime = LocalTime.parse("18:21:00").truncatedTo(ChronoUnit.MINUTES)

        val actual   = valT.count(start = x, end = y)
        val expected = 2

        actual mustEqual expected
      }

      "compare" in {
        val valT: Domain[LocalTime] = Domain.makeLocalTime(ChronoUnit.MINUTES)

        val x: LocalTime = LocalTime.parse("18:19:00").truncatedTo(ChronoUnit.MINUTES)
        val y: LocalTime = LocalTime.parse("18:21:00").truncatedTo(ChronoUnit.MINUTES)

        val actual = valT.compare(x, y)
        (actual < 0) mustBe true
      }
    }

    "ZonedDateTime" should {
      "pred and succ" in {
        val valT: Domain[ZonedDateTime] = Domain.makeZonedDateTime(ChronoUnit.DAYS)

        val x: ZonedDateTime = ZonedDateTime.parse("2019-02-03T00:00:00.000Z[Europe/London]").truncatedTo(ChronoUnit.DAYS)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (ZonedDateTime.parse("2019-02-02T00:00Z[Europe/London]"))
        xn mustEqual (ZonedDateTime.parse("2019-02-04T00:00Z[Europe/London]"))

        val actual   = valT.pred(valT.succ(x))
        val expected = x

        actual mustEqual expected
      }

      "count" in {
        val valT: Domain[ZonedDateTime] = Domain.makeZonedDateTime(ChronoUnit.DAYS)

        val x: ZonedDateTime = ZonedDateTime.parse("2022-07-02T00:00Z[Europe/London]").truncatedTo(ChronoUnit.DAYS)
        val y: ZonedDateTime = ZonedDateTime.parse("2022-07-10T00:00Z[Europe/London]").truncatedTo(ChronoUnit.DAYS)

        val actual   = valT.count(start = x, end = y)
        val expected = 8

        actual mustEqual expected
      }

      "compare" in {
        val valT: Domain[ZonedDateTime] = Domain.makeZonedDateTime(ChronoUnit.DAYS)

        val x: ZonedDateTime = ZonedDateTime.parse("2022-07-02T00:00Z[Europe/London]").truncatedTo(ChronoUnit.DAYS)
        val y: ZonedDateTime = ZonedDateTime.parse("2022-07-10T00:00Z[Europe/London]").truncatedTo(ChronoUnit.DAYS)

        val actual = valT.compare(x, y)
        (actual < 0) mustBe true
      }
    }
  }
