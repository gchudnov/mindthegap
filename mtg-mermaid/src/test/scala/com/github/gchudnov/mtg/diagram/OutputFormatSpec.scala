package com.github.gchudnov.mtg.diagram

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.time.temporal.*
import java.time.*

final class OutputFormatSpec extends AnyWordSpec with Matchers {
  "OutputFormatFormat" when {
    "OffsetDateTime" should {
      "pattern" in {
        val actual = com.github.gchudnov.mtg.diagram.OutputFormat.offsetDateTimeOutputFormat.pattern
        val expected = "%d.%m.%Y %H:%M:%S"

        actual shouldEqual expected
      }
    }

    "OffsetTime" should {
      "pattern" in {
        val actual = com.github.gchudnov.mtg.diagram.OutputFormat.offsetTimeOutputFormat.pattern
        val expected = "%H:%M:%S"

        actual shouldEqual expected
      }
    }

    "Instant" should {
      "pattern" in {
        val actual = com.github.gchudnov.mtg.diagram.OutputFormat.instantOutputFormat.pattern
        val expected = "%d.%m.%Y %H:%M:%S"

        actual shouldEqual expected
      }
    }

    "LocalDateTime" should {
      "pattern" in {
        val actual = com.github.gchudnov.mtg.diagram.OutputFormat.localDateTimeOutputFormat.pattern
        val expected = "%d.%m.%Y %H:%M:%S"

        actual shouldEqual expected
      }
    }

    "LocalDate" should {
      "pattern" in {
        val actual = com.github.gchudnov.mtg.diagram.OutputFormat.localDateOutputFormat.pattern
        val expected = "%d.%m.%Y"

        actual shouldEqual expected
      }
    }

    "LocalTime" should {
      "pattern" in {
        val actual = com.github.gchudnov.mtg.diagram.OutputFormat.localTimeOutputFormat.pattern
        val expected = "%H:%M:%S"

        actual shouldEqual expected
      }
    }

    "ZonedDateTime" should {
      "pattern" in {
        val actual = com.github.gchudnov.mtg.diagram.OutputFormat.zonedDateTimeOutputFormat.pattern
        val expected = "%d.%m.%Y %H:%M:%S"

        actual shouldEqual expected
      }
    }
  }
}