package com.github.gchudnov.mtg.diagram.internal

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.time.temporal.*
import java.time.*

final class InputFormatSpec extends AnyWordSpec with Matchers {
  "InputFormat" when {
    "OffsetDateTime" should {
      "pattern" in {
        val actual = InputFormat.offsetDateTimeInputFormat.pattern
        val expected = "YYYY-MM-DD HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = OffsetDateTime.ofInstant(Instant.parse("2024-08-19T12:34:56.000Z"), ZoneOffset.UTC)
        
        val actual = InputFormat.offsetDateTimeInputFormat.format(value)
        val expected = "2024-08-19 12:34:56.000"

        actual shouldEqual expected
      }
    }

    "OffsetTime" should {
      "pattern" in {
        val actual = InputFormat.offsetTimeInputFormat.pattern
        val expected = "HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = OffsetTime.ofInstant(Instant.parse("2024-08-19T12:34:56.000Z"), ZoneOffset.UTC)
        
        val actual = InputFormat.offsetTimeInputFormat.format(value)
        val expected = "12:34:56.000"

        actual shouldEqual expected
      }
    }

    "Instant" should {
      "pattern" in {
        val actual = InputFormat.instantInputFormat.pattern
        val expected = "YYYY-MM-DD HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = Instant.parse("2024-08-19T12:34:56.000Z")
        
        val actual = InputFormat.instantInputFormat.format(value)
        val expected = "2024-08-19 12:34:56.000"

        actual shouldEqual expected
      }
    }

    "LocalDateTime" should {
      "pattern" in {
        val actual = InputFormat.localDateTimeInputFormat.pattern
        val expected = "YYYY-MM-DD HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = LocalDateTime.parse("2024-08-19T12:34:56.000")
        
        val actual = InputFormat.localDateTimeInputFormat.format(value)
        val expected = "2024-08-19 12:34:56.000"

        actual shouldEqual expected
      }
    }

    "LocalDate" should {
      "pattern" in {
        val actual = InputFormat.localDateInputFormat.pattern
        val expected = "YYYY-MM-DD"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = LocalDate.parse("2024-08-19")
        
        val actual = InputFormat.localDateInputFormat.format(value)
        val expected = "2024-08-19"

        actual shouldEqual expected
      }
    }

    "LocalTime" should {
      "pattern" in {
        val actual = InputFormat.localTimeInputFormat.pattern
        val expected = "HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = LocalTime.parse("12:34:56.000")
        
        val actual = InputFormat.localTimeInputFormat.format(value)
        val expected = "12:34:56.000"

        actual shouldEqual expected
      }
    }

    "ZonedDateTime" should {
      "pattern" in {
        val actual = InputFormat.zonedDateTimeInputFormat.pattern
        val expected = "YYYY-MM-dd HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = ZonedDateTime.parse("2024-08-19T12:34:56.000Z")
        
        val actual = InputFormat.zonedDateTimeInputFormat.format(value)
        val expected = "2024-08-19 12:34:56.000"

        actual shouldEqual expected
      }
    }
  }
}