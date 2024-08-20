package com.github.gchudnov.mtg.diagram.internal

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.time.temporal.*
import java.time.*

final class OutputFormatSpec extends AnyWordSpec with Matchers {
  "OutputFormatFormat" when {
    "OffsetDateTime" should {
      "pattern" in {
        val actual = OutputFormat.offsetDateTimeOutputFormat.pattern
        val expected = "%d.%m.%Y %H:%M:%S"

        actual shouldEqual expected
      }
    }

    "OffsetTime" should {
      "pattern" in {
        val actual = OutputFormat.offsetTimeOutputFormat.pattern
        val expected = "%H:%M:%S"

        actual shouldEqual expected
      }
    }

    "Instant" should {
      "pattern" in {
        val actual = OutputFormat.instantOutputFormat.pattern
        val expected = "%d.%m.%Y %H:%M:%S"

        actual shouldEqual expected
      }
    }

    "LocalDateTime" should {
      "pattern" in {
        val actual = OutputFormat.localDateTimeOutputFormat.pattern
        val expected = "%d.%m.%Y %H:%M:%S"

        actual shouldEqual expected
      }
    }

    "LocalDate" should {
      "pattern" in {
        val actual = OutputFormat.localDateOutputFormat.pattern
        val expected = "%d.%m.%Y"

        actual shouldEqual expected
      }
    }

    "LocalTime" should {
      "pattern" in {
        val actual = OutputFormat.localTimeOutputFormat.pattern
        val expected = "%H:%M:%S"

        actual shouldEqual expected
      }
    }

    "ZonedDateTime" should {
      "pattern" in {
        val actual = OutputFormat.zonedDateTimeOutputFormat.pattern
        val expected = "%d.%m.%Y %H:%M:%S"

        actual shouldEqual expected
      }
    }
  }
}