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

        val d = Diagram
          .empty[OffsetDateTime]
          .withTitle("title")
          .withSection { s =>
            s
              .withTitle("section")
              .addInterval(OffsetDateTime.parse("2021-01-01T00:00:00Z"), OffsetDateTime.parse("2021-01-02T00:00:00Z"), "task")
          }

        r.render(d)

        val actual = r.result.trim

        val expected = """
                         |gantt
                         |  title       title
                         |  dateFormat  YYYY-MM-DD HH:mm:ss.SSS
                         |  axisFormat  %d.%m.%Y %H:%M:%S
                         |
                         |  section section
                         |  task  :2021-01-01 00:00:00.000, 2021-01-02 00:00:00.000
                         |""".stripMargin.trim

        actual shouldEqual expected
      }

      "render several intervals in several sections" in {
        val r = MermaidRenderer.make[OffsetDateTime]

        val d = Diagram
          .empty[OffsetDateTime]
          .withTitle("title")
          .withSection { s =>
            s
              .withTitle("section1")
              .addInterval(OffsetDateTime.parse("2021-01-01T00:00:00Z"), OffsetDateTime.parse("2021-01-02T00:00:00Z"), "task1")
              .addInterval(OffsetDateTime.parse("2021-01-03T00:00:00Z"), OffsetDateTime.parse("2021-01-04T00:00:00Z"), "task2")
          }
          .withSection { s =>
            s
              .withTitle("section2")
              .addInterval(OffsetDateTime.parse("2021-01-05T00:00:00Z"), OffsetDateTime.parse("2021-01-06T00:00:00Z"), "task3")
              .addInterval(OffsetDateTime.parse("2021-01-07T00:00:00Z"), OffsetDateTime.parse("2021-01-08T00:00:00Z"), "task4")
          }

        r.render(d)

        val actual = r.result.trim

        val expected = """
                         |gantt
                         |  title       title
                         |  dateFormat  YYYY-MM-DD HH:mm:ss.SSS
                         |  axisFormat  %d.%m.%Y %H:%M:%S
                         |
                         |  section section1
                         |  task1  :2021-01-01 00:00:00.000, 2021-01-02 00:00:00.000
                         |  task2  :2021-01-03 00:00:00.000, 2021-01-04 00:00:00.000
                         |
                         |  section section2
                         |  task3  :2021-01-05 00:00:00.000, 2021-01-06 00:00:00.000
                         |  task4  :2021-01-07 00:00:00.000, 2021-01-08 00:00:00.000
                         |""".stripMargin.trim

        actual shouldEqual expected
      }
    }
  }
