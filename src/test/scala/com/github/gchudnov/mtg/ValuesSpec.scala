package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Values.*
import com.github.gchudnov.mtg.Values.given
import org.scalactic.TolerantNumerics
import org.scalactic.Equality
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.time.Instant

final class ValuesSpec extends TestSpec:

  "Values" when {

    "Int" should {
      "get predecessor and successor values" in {
        val valT: Value[Int] = summon[Value[Int]]

        val x: Int = 10

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (9)
        xn mustEqual (11)
      }
    }

    "Long" should {
      "get predecessor and successor values" in {
        val valT: Value[Long] = summon[Value[Long]]

        val x: Long = 10L

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (9L)
        xn mustEqual (11L)
      }
    }

    "Double" should {
      "get predecessor and successor values" in {
        given doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(0.001)
        given valT: Value[Double]        = fractionalValue(0.001)

        val x: Double = 10.0

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        assert(xp === 9.999)
        assert(xn === 10.001)
      }
    }

    "OffsetDateTime" should {
      "get predecessor and successor values" in {
        val valT: Value[OffsetDateTime] = offsetDateTimeValue(ChronoUnit.DAYS)

        val x: OffsetDateTime = OffsetDateTime.parse("2017-07-03T17:05:29.771Z").truncatedTo(ChronoUnit.DAYS)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (OffsetDateTime.parse("2017-07-02T00:00Z"))
        xn mustEqual (OffsetDateTime.parse("2017-07-04T00:00Z"))
      }
    }

    "Instant" should {
      "get predecessor and successor values" in {
        val valT: Value[Instant] = instantValue(ChronoUnit.DAYS)

        val x: Instant = Instant.parse("2017-07-03T17:05:29Z").truncatedTo(ChronoUnit.DAYS)

        val xp = valT.pred(x)
        val xn = valT.succ(x)

        xp mustEqual (Instant.parse("2017-07-02T00:00:00Z"))
        xn mustEqual (Instant.parse("2017-07-04T00:00:00Z"))
      }
    }
  }
