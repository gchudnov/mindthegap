package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class DiagramSpec extends AnyWordSpec with Matchers:

  "Diagram" when {
    "make" should {
      "no intervals" in {

        val actual = Diagram.make[Int](
          name = "no-intervals",
          intervals = List.empty[List[Interval[Int]]],
          annotations = List.empty[List[String]],
          sectionNames = List.empty[String],
        )

        actual.name shouldBe "no-intervals"
        actual.sections shouldBe List.empty
      }

      "one explicit section with two intervals and two annotations" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual = Diagram.make[Int](
          name = "one-section",
          intervals = List(List(a, b)),
          annotations = List(List("a", "b")),
          sectionNames = List("section-1"),
        )

        actual.name shouldBe "one-section"
        actual.sections.size shouldBe 1
        actual.sections.head.name shouldBe "section-1"
        actual.sections.head.intervals shouldBe List(a, b)
        actual.sections.head.annotations shouldBe List("a", "b")
      }

      "one implicit section with two intervals and no annotations" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual = Diagram.make[Int](
          name = "one-section",
          intervals = List(a, b),
        )

        actual.name shouldBe "one-section"
        actual.sections.size shouldBe 1
        actual.sections.head.name shouldBe ""
        actual.sections.head.intervals shouldBe List(a, b)
        actual.sections.head.annotations shouldBe List("a", "b")
      }
    }
  }
