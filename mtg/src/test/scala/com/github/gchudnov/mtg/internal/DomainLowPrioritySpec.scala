package com.github.gchudnov.mtg.internal

import org.scalactic.Equality
import org.scalactic.TolerantNumerics
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.OffsetTime
import java.time.LocalTime
import java.time.LocalDate
import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Domain

final class DomainLowPrioritySpec extends TestSpec:

  "DomainLowPriority" when {

    "Int" should {
      val valT: Domain[Int] = summon[Domain[Int]]

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (10, 9, 11),
          (0, -1, 1),
          (-10, -11, -9),
        )

        forAll(t) { (x: Int, xp: Int, xn: Int) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (10, 20, 11),
          (0, 10, 11),
          (-10, 10, 21),
        )

        forAll(t) { (x: Int, y: Int, expected: Int) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (10, 20, -1),
          (20, 10, 1),
          (10, 10, 0),
        )

        forAll(t) { (x: Int, y: Int, expected: Int) =>
          val actual = valT.compare(x, y)

          actual mustEqual expected
        }
      }
    }

    "Long" should {
      val valT: Domain[Long] = summon[Domain[Long]]

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (10L, 9L, 11L),
          (0L, -1L, 1L),
          (-10L, -11L, -9L),
        )

        forAll(t) { (x: Long, xp: Long, xn: Long) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (10L, 20L, 11L),
          (0L, 10L, 11L),
          (-10L, 10L, 21L),
        )

        forAll(t) { (x: Long, y: Long, expected: Long) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (10L, 20L, -1),
          (20L, 10L, 1),
          (10L, 10L, 0),
        )

        forAll(t) { (x: Long, y: Long, expected: Int) =>
          val actual = valT.compare(x, y)

          actual mustEqual expected
        }
      }
    }

    "Double" should {
      given doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(0.001)
      given valT: Domain[Double]       = Domain.makeFractional(0.001)

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (10.0, 9.999, 10.001),
          (0.0, -0.001, 0.001),
          (-10.0, -10.001, -9.999),
        )

        forAll(t) { (x: Double, xp: Double, xn: Double) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          assert(actualXp === xp)
          assert(actualXn === xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          assert(actual === expected)
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (10.0, 20.0, 10001),
          (0.0, 10.0, 10001),
          (-10.0, 10.0, 20001),
        )

        forAll(t) { (x: Double, y: Double, expected: Int) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (10.0, 20.0, -1),
          (20.0, 10.0, 1),
          (10.0, 10.0, 0),
        )

        forAll(t) { (x: Double, y: Double, expected: Int) =>
          val actual = valT.compare(x, y)

          actual mustEqual expected
        }
      }
    }

    "OffsetDateTime" should {
      val valT: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.DAYS)

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (OffsetDateTime.parse("2022-07-02T00:00Z"), OffsetDateTime.parse("2022-07-01T00:00Z"), OffsetDateTime.parse("2022-07-03T00:00Z")),
          (OffsetDateTime.parse("2022-07-03T00:00Z"), OffsetDateTime.parse("2022-07-02T00:00Z"), OffsetDateTime.parse("2022-07-04T00:00Z")),
          (OffsetDateTime.parse("2022-07-04T00:00Z"), OffsetDateTime.parse("2022-07-03T00:00Z"), OffsetDateTime.parse("2022-07-05T00:00Z")),
        )

        forAll(t) { (x: OffsetDateTime, xp: OffsetDateTime, xn: OffsetDateTime) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (OffsetDateTime.parse("2022-07-02T00:00Z"), OffsetDateTime.parse("2022-07-10T00:00Z"), 9L),
          (OffsetDateTime.parse("2022-07-02T00:00Z"), OffsetDateTime.parse("2022-07-03T00:00Z"), 2L),
          (OffsetDateTime.parse("2022-07-02T00:00Z"), OffsetDateTime.parse("2022-07-02T00:00Z"), 1L),
        )

        forAll(t) { (x: OffsetDateTime, y: OffsetDateTime, expected: Long) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (OffsetDateTime.parse("2022-07-02T00:00Z"), OffsetDateTime.parse("2022-07-10T00:00Z"), -1),
          (OffsetDateTime.parse("2022-07-10T00:00Z"), OffsetDateTime.parse("2022-07-02T00:00Z"), 1),
          (OffsetDateTime.parse("2022-07-02T00:00Z"), OffsetDateTime.parse("2022-07-02T00:00Z"), 0),
        )

        forAll(t) { (x: OffsetDateTime, y: OffsetDateTime, expected: Int) =>
          val actual = valT.compare(x, y)

          actual.sign mustEqual expected
        }
      }
    }

    "OffsetTime" should {
      val valT: Domain[OffsetTime] = Domain.makeOffsetTime(ChronoUnit.MINUTES)

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (OffsetTime.parse("18:19:00.000Z"), OffsetTime.parse("18:18:00.000Z"), OffsetTime.parse("18:20:00.000Z")),
          (OffsetTime.parse("18:20:00.000Z"), OffsetTime.parse("18:19:00.000Z"), OffsetTime.parse("18:21:00.000Z")),
          (OffsetTime.parse("18:21:00.000Z"), OffsetTime.parse("18:20:00.000Z"), OffsetTime.parse("18:22:00.000Z")),
        )

        forAll(t) { (x: OffsetTime, xp: OffsetTime, xn: OffsetTime) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (OffsetTime.parse("18:19:00.000Z"), OffsetTime.parse("18:21:00.000Z"), 3L),
          (OffsetTime.parse("18:19:00.000Z"), OffsetTime.parse("18:20:00.000Z"), 2L),
          (OffsetTime.parse("18:19:00.000Z"), OffsetTime.parse("18:19:00.000Z"), 1L),
        )

        forAll(t) { (x: OffsetTime, y: OffsetTime, expected: Long) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (OffsetTime.parse("18:19:00.000Z"), OffsetTime.parse("18:21:00.000Z"), -1),
          (OffsetTime.parse("18:21:00.000Z"), OffsetTime.parse("18:19:00.000Z"), 1),
          (OffsetTime.parse("18:19:00.000Z"), OffsetTime.parse("18:19:00.000Z"), 0),
        )

        forAll(t) { (x: OffsetTime, y: OffsetTime, expected: Int) =>
          val actual = valT.compare(x, y)

          actual.sign mustEqual expected
        }
      }
    }

    "Instant" should {
      val valT: Domain[Instant] = Domain.makeInstant(ChronoUnit.DAYS)

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (Instant.parse("2022-07-02T00:00:00Z"), Instant.parse("2022-07-01T00:00:00Z"), Instant.parse("2022-07-03T00:00:00Z")),
          (Instant.parse("2022-07-03T00:00:00Z"), Instant.parse("2022-07-02T00:00:00Z"), Instant.parse("2022-07-04T00:00:00Z")),
          (Instant.parse("2022-07-04T00:00:00Z"), Instant.parse("2022-07-03T00:00:00Z"), Instant.parse("2022-07-05T00:00:00Z")),
        )

        forAll(t) { (x: Instant, xp: Instant, xn: Instant) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (Instant.parse("2022-07-02T00:00:00Z"), Instant.parse("2022-07-10T00:00:00Z"), 9L),
          (Instant.parse("2022-07-02T00:00:00Z"), Instant.parse("2022-07-03T00:00:00Z"), 2L),
          (Instant.parse("2022-07-02T00:00:00Z"), Instant.parse("2022-07-02T00:00:00Z"), 1L),
        )

        forAll(t) { (x: Instant, y: Instant, expected: Long) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (Instant.parse("2022-07-02T00:00:00Z"), Instant.parse("2022-07-10T00:00:00Z"), -1),
          (Instant.parse("2022-07-10T00:00:00Z"), Instant.parse("2022-07-02T00:00:00Z"), 1),
          (Instant.parse("2022-07-02T00:00:00Z"), Instant.parse("2022-07-02T00:00:00Z"), 0),
        )

        forAll(t) { (x: Instant, y: Instant, expected: Int) =>
          val actual = valT.compare(x, y)

          actual.sign mustEqual expected
        }
      }
    }

    "LocalDateTime" should {
      val valT: Domain[LocalDateTime] = Domain.makeLocalDateTime(ChronoUnit.DAYS)

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (
            LocalDateTime.parse("2019-02-03T00:00:00.000"),
            LocalDateTime.parse("2019-02-02T00:00"),
            LocalDateTime.parse("2019-02-04T00:00"),
          ),
          (
            LocalDateTime.parse("2019-02-04T00:00:00.000"),
            LocalDateTime.parse("2019-02-03T00:00"),
            LocalDateTime.parse("2019-02-05T00:00"),
          ),
          (LocalDateTime.parse("2019-02-05T00:00:00.000"), LocalDateTime.parse("2019-02-04T00:00"), LocalDateTime.parse("2019-02-06T00:00")),
        )

        forAll(t) { (x: LocalDateTime, xp: LocalDateTime, xn: LocalDateTime) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (LocalDateTime.parse("2019-02-03T00:00:00.000"), LocalDateTime.parse("2019-02-10T00:00"), 8),
          (LocalDateTime.parse("2019-02-03T00:00:00.000"), LocalDateTime.parse("2019-02-04T00:00"), 2),
          (LocalDateTime.parse("2019-02-03T00:00:00.000"), LocalDateTime.parse("2019-02-03T00:00"), 1),
        )

        forAll(t) { (x: LocalDateTime, y: LocalDateTime, expected: Int) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (LocalDateTime.parse("2019-02-03T00:00:00.000"), LocalDateTime.parse("2019-02-10T00:00"), -1),
          (LocalDateTime.parse("2019-02-10T00:00:00.000"), LocalDateTime.parse("2019-02-03T00:00"), 1),
          (LocalDateTime.parse("2019-02-03T00:00:00.000"), LocalDateTime.parse("2019-02-03T00:00"), 0),
        )

        forAll(t) { (x: LocalDateTime, y: LocalDateTime, expected: Int) =>
          val actual = valT.compare(x, y)

          actual.sign mustEqual expected
        }
      }
    }

    "LocalDate" should {
      val valT: Domain[LocalDate] = Domain.makeLocalDate(ChronoUnit.DAYS)

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (LocalDate.parse("2019-02-03"), LocalDate.parse("2019-02-02"), LocalDate.parse("2019-02-04")),
          (LocalDate.parse("2019-02-04"), LocalDate.parse("2019-02-03"), LocalDate.parse("2019-02-05")),
          (LocalDate.parse("2019-02-05"), LocalDate.parse("2019-02-04"), LocalDate.parse("2019-02-06")),
        )

        forAll(t) { (x: LocalDate, xp: LocalDate, xn: LocalDate) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (LocalDate.parse("2019-02-03"), LocalDate.parse("2019-02-10"), 8L),
          (LocalDate.parse("2019-02-03"), LocalDate.parse("2019-02-04"), 2L),
          (LocalDate.parse("2019-02-03"), LocalDate.parse("2019-02-03"), 1L),
        )

        forAll(t) { (x: LocalDate, y: LocalDate, expected: Long) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (LocalDate.parse("2019-02-03"), LocalDate.parse("2019-02-10"), -1),
          (LocalDate.parse("2019-02-10"), LocalDate.parse("2019-02-03"), 1),
          (LocalDate.parse("2019-02-03"), LocalDate.parse("2019-02-03"), 0),
        )

        forAll(t) { (x: LocalDate, y: LocalDate, expected: Int) =>
          val actual = valT.compare(x, y)

          actual.sign mustEqual expected
        }
      }
    }

    "LocalTime" should {
      val valT: Domain[LocalTime] = Domain.makeLocalTime(ChronoUnit.MINUTES)

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (LocalTime.parse("18:19:00"), LocalTime.parse("18:18:00"), LocalTime.parse("18:20:00")),
          (LocalTime.parse("18:20:00"), LocalTime.parse("18:19:00"), LocalTime.parse("18:21:00")),
          (LocalTime.parse("18:21:00"), LocalTime.parse("18:20:00"), LocalTime.parse("18:22:00")),
        )

        forAll(t) { (x: LocalTime, xp: LocalTime, xn: LocalTime) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (LocalTime.parse("18:19:00"), LocalTime.parse("18:21:00"), 3L),
          (LocalTime.parse("18:19:00"), LocalTime.parse("18:20:00"), 2L),
          (LocalTime.parse("18:19:00"), LocalTime.parse("18:19:00"), 1L),
        )

        forAll(t) { (x: LocalTime, y: LocalTime, expected: Long) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (LocalTime.parse("18:19:00"), LocalTime.parse("18:21:00"), -1),
          (LocalTime.parse("18:21:00"), LocalTime.parse("18:19:00"), 1),
          (LocalTime.parse("18:19:00"), LocalTime.parse("18:19:00"), 0),
        )

        forAll(t) { (x: LocalTime, y: LocalTime, expected: Int) =>
          val actual = valT.compare(x, y)

          actual.sign mustEqual expected
        }
      }
    }

    "ZonedDateTime" should {
      val valT: Domain[ZonedDateTime] = Domain.makeZonedDateTime(ChronoUnit.DAYS)

      "pred and succ" in {
        val t = Table(
          ("x", "xp", "xn"),
          (
            ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"),
            ZonedDateTime.parse("2019-02-02T00:00Z[Europe/London]"),
            ZonedDateTime.parse("2019-02-04T00:00Z[Europe/London]"),
          ),
          (
            ZonedDateTime.parse("2019-02-04T00:00Z[Europe/London]"),
            ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"),
            ZonedDateTime.parse("2019-02-05T00:00Z[Europe/London]"),
          ),
          (
            ZonedDateTime.parse("2019-02-05T00:00Z[Europe/London]"),
            ZonedDateTime.parse("2019-02-04T00:00Z[Europe/London]"),
            ZonedDateTime.parse("2019-02-06T00:00Z[Europe/London]"),
          ),
        )

        forAll(t) { (x: ZonedDateTime, xp: ZonedDateTime, xn: ZonedDateTime) =>
          val actualXp = valT.pred(x)
          val actualXn = valT.succ(x)

          actualXp mustEqual (xp)
          actualXn mustEqual (xn)

          // pred(next(a)) == a
          val actual   = valT.pred(valT.succ(x))
          val expected = x

          actual mustEqual expected
        }
      }

      "count" in {
        val t = Table(
          ("x", "y", "expected"),
          (ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"), ZonedDateTime.parse("2019-02-05T00:00Z[Europe/London]"), 3L),
          (ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"), ZonedDateTime.parse("2019-02-04T00:00Z[Europe/London]"), 2L),
          (ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"), ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"), 1L),
        )

        forAll(t) { (x: ZonedDateTime, y: ZonedDateTime, expected: Long) =>
          val actual = valT.count(start = x, end = y)

          actual mustEqual expected
        }
      }

      "compare" in {
        val t = Table(
          ("x", "y", "expected"),
          (ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"), ZonedDateTime.parse("2019-02-05T00:00Z[Europe/London]"), -1),
          (ZonedDateTime.parse("2019-02-05T00:00Z[Europe/London]"), ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"), 1),
          (ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"), ZonedDateTime.parse("2019-02-03T00:00Z[Europe/London]"), 0),
        )

        forAll(t) { (x: ZonedDateTime, y: ZonedDateTime, expected: Int) =>
          val actual = valT.compare(x, y)

          actual.sign mustEqual expected
        }
      }
    }
  }
