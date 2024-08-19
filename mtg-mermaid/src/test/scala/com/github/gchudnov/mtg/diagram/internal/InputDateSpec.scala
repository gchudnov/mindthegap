package com.github.gchudnov.mtg.diagram.internal

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.time.temporal.*
import java.time.*

class InputDateSpec extends AnyWordSpec with Matchers {
  "InputDate" when {
    "OffsetDateTime" should {
      "pattern" in {
        val actual = InputDate.offsetDateTimeInputDate.pattern
        val expected = "YYYY-MM-DD HH:mm:ss.SSS"

        actual shouldEqual expected
      }
      
      "format" in {
        val value = OffsetDateTime.ofInstant(Instant.parse("2024-08-19T12:34:56.000Z"), ZoneOffset.UTC)
        
        val actual = InputDate.offsetDateTimeInputDate.format(value)
        val expected = "2024-08-19 12:34:56.000"

        actual shouldEqual expected
      }
    }
  }
}