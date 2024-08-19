package com.github.gchudnov.mtg.diagram.internal

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.time.temporal.*
import java.time.*

class InputFormatSpec extends AnyWordSpec with Matchers {
  "InputFormat" when {
    "OffsetDateTime" should {
      "pattern" in {
        val actual = InputFormat.offsetDateTimeInputDate.pattern
        val expected = "YYYY-MM-DD HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = OffsetDateTime.ofInstant(Instant.parse("2024-08-19T12:34:56.000Z"), ZoneOffset.UTC)
        
        val actual = InputFormat.offsetDateTimeInputDate.format(value)
        val expected = "2024-08-19 12:34:56.000"

        actual shouldEqual expected
      }
    }

    "Instant" should {
      "pattern" in {
        val actual = InputFormat.instantInputDate.pattern
        val expected = "YYYY-MM-DD HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = Instant.parse("2024-08-19T12:34:56.000Z")
        
        val actual = InputFormat.instantInputDate.format(value)
        val expected = "2024-08-19 12:34:56.000"

        actual shouldEqual expected
      }
    }

    "LocalDateTime" should {
      "pattern" in {
        val actual = InputFormat.localDateTimeInputDate.pattern
        val expected = "YYYY-MM-DD HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = LocalDateTime.parse("2024-08-19T12:34:56.000")
        
        val actual = InputFormat.localDateTimeInputDate.format(value)
        val expected = "2024-08-19 12:34:56.000"

        actual shouldEqual expected
      }
    }

    "LocalDate" should {
      "pattern" in {
        val actual = InputFormat.localDateInputDate.pattern
        val expected = "YYYY-MM-DD"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = LocalDate.parse("2024-08-19")
        
        val actual = InputFormat.localDateInputDate.format(value)
        val expected = "2024-08-19"

        actual shouldEqual expected
      }
    }
  }
}