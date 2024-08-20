package com.github.gchudnov.mtg.diagram

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.time.OffsetDateTime
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.LocalDate
import com.github.gchudnov.mtg.diagram.Diagram

class MermaidRendererSpec extends AnyWordSpec with Matchers:
  "MermaidRenderer" when {
    "OffsetDateTime" should {
      "render an empty diagram" in {
        val r = MermaidRenderer.make[OffsetDateTime]

        val d = Diagram.empty[OffsetDateTime]
        r.render(d)

        val actual = r.result.trim

        val expected = """
                         |gantt
                         |  title       
                         |  dateFormat  YYYY-MM-DD HH:mm:ss.SSS
                         |  axisFormat  %d.%m.%Y %H:%M:%S
                         |""".stripMargin.trim

        actual shouldEqual expected
      }

      "render a single interval" in {
        val r = MermaidRenderer.make[OffsetDateTime]

        val d = Diagram.empty[OffsetDateTime]
          .addInterval(OffsetDateTime.parse("2021-01-01T00:00:00Z"), OffsetDateTime.parse("2021-01-02T00:00:00Z"))
        r.render(d)

        

        val actual = r.result.trim

        val expected = """
                         |gantt
                         |  title       
                         |  dateFormat  YYYY-MM-DD HH:mm:ss.SSS
                         |  axisFormat  %d.%m.%Y %H:%M:%S
                         |
                         |  section 
                         |  task  :2021-01-01T00:00:00Z, 2021-01-02T00:00:00Z
                         |""".stripMargin.trim

        actual shouldEqual expected
      }
    }
  }
