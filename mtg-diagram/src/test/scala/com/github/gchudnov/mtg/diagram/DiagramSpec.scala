package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.diagram.Renderer
import com.github.gchudnov.mtg.diagram.Diagram.Canvas
import com.github.gchudnov.mtg.diagram.Diagram.Span
import com.github.gchudnov.mtg.diagram.Diagram.Tick
import com.github.gchudnov.mtg.diagram.Diagram.Label
import com.github.gchudnov.mtg.diagram.Diagram.Legend
import com.github.gchudnov.mtg.diagram.Diagram.Annotation
import com.github.gchudnov.mtg.diagram.Diagram.View
import java.time.OffsetDateTime
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.github.gchudnov.mtg.Interval
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.github.gchudnov.mtg.Domain

final class DiagramSpec extends AnyWordSpec with Matchers:

  private val canvas: Canvas      = Canvas.make(40, 2)
  private val infView: View[Int]  = View.all[Int]

  "Diagram" when {
    "make" should {
      "diagram no intervals" in {
        val actual   = Diagram.make(List.empty[Interval[Int]], infView, canvas)
        val expected = Diagram.empty

        actual shouldBe expected
      }

      "diagram an empty interval" in {
        val a = Interval.empty[Int]

        val actual   = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(40, 1, List(Span(1, -1, true, true)), List(), List(), List(Legend("∅")), List(Annotation("a")))

        actual shouldBe expected
      }

      "diagram a point" in {
        val a = Interval.point[Int](5) // [5]

        val actual = Diagram.make(List(a), infView, canvas)
        val expected =
          Diagram(40, 1, List(Span(20, 20, true, true)), List(Tick(20)), List(Label(20, "5")), List(Legend("{5}")), List(Annotation("a")))

        actual shouldBe expected
      }

      "diagram two points" in {
        val a = Interval.point[Int](5)  // [5]
        val b = Interval.point[Int](10) // [10]

        val actual = Diagram.make(List(a, b), infView, canvas)
        val expected =
          Diagram(
            40,
            2,
            List(Span(2, 2, true, true), Span(37, 37, true, true)),
            List(Tick(2), Tick(37)),
            List(Label(2, "5"), Label(36, "10")),
            List(Legend("{5}"), Legend("{10}")),
            List(Annotation("a"), Annotation("b")),
          )

        actual shouldBe expected
      }

      "diagram a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val actual = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(
          40,
          1,
          List(Span(2, 37, true, true)),
          List(Tick(2), Tick(37)),
          List(Label(2, "5"), Label(36, "10")),
          List(Legend("[5,10]")),
          List(Annotation("a")),
        )

        actual shouldBe expected
      }

      "diagram a closed interval with a negative boundary" in {
        val a = Interval.closed[Int](-5, 10) // [-5, 10]

        val actual = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(
          40,
          1,
          List(Span(2, 37, true, true)),
          List(Tick(2), Tick(37)),
          List(Label(1, "-5"), Label(36, "10")),
          List(Legend("[-5,10]")),
          List(Annotation("a")),
        )

        actual shouldBe expected
      }

      "diagram an unbounded interval" in {
        val a = Interval.unbounded[Int] // (-∞, +∞)

        val actual = Diagram.make(List(a), infView, canvas)
        val expected =
          Diagram(
            40,
            1,
            List(Span(0, 39, false, false)),
            List(Tick(0), Tick(39)),
            List(Label(0, "-∞"), Label(38, "+∞")),
            List(Legend("(-∞,+∞)")),
            List(Annotation("a")),
          )

        actual shouldBe expected
      }

      "diagram a leftOpen interval" in {
        val a = Interval.leftOpen(5) // (5, +∞)

        val actual = Diagram.make(List(a), infView, canvas)
        val expected =
          Diagram(
            40,
            1,
            List(Span(20, 39, false, false)),
            List(Tick(20), Tick(39)),
            List(Label(20, "5"), Label(38, "+∞")),
            List(Legend("(5,+∞)")),
            List(Annotation("a")),
          )

        actual shouldBe expected
      }

      "diagram a leftClosed interval" in {
        val a = Interval.leftClosed(5) // [5, +∞)

        val actual = Diagram.make(List(a), infView, canvas)
        val expected =
          Diagram(
            40,
            1,
            List(Span(20, 39, true, false)),
            List(Tick(20), Tick(39)),
            List(Label(20, "5"), Label(38, "+∞")),
            List(Legend("[5,+∞)")),
            List(Annotation("a")),
          )

        actual shouldBe expected
      }

      "diagram a rightOpen interval" in {
        val a = Interval.rightOpen(5) // (-∞, 5)

        val actual = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(
          40,
          1,
          List(Span(0, 20, false, false)),
          List(Tick(0), Tick(20)),
          List(Label(0, "-∞"), Label(20, "5")),
          List(Legend("(-∞,5)")),
          List(Annotation("a")),
        )

        actual shouldBe expected
      }

      "diagram a rightClosed interval" in {
        val a = Interval.rightClosed(5) // (-∞, 5]

        val actual = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(
          40,
          1,
          List(Span(0, 20, false, true)),
          List(Tick(0), Tick(20)),
          List(Label(0, "-∞"), Label(20, "5")),
          List(Legend("(-∞,5]")),
          List(Annotation("a")),
        )

        actual shouldBe expected
      }

      "diagram left part of a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val view = View.make(Some(0), Some(7)) // [0, 7]

        val actual = Diagram.make(List(a), view, canvas)
        val expected = Diagram(
          40,
          1,
          List(Span(27, 52, true, true)),
          List(Tick(2), Tick(27), Tick(37), Tick(52)),
          List(Label(2, "0"), Label(27, "5"), Label(37, "7"), Label(51, "10")),
          List(Legend("[5,10]")),
          List(Annotation("a")),
        )

        actual shouldBe expected
      }

      "diagram several intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val actual = Diagram.make(List(a, b, c, d), infView, canvas)
        val expected = Diagram(
          40,
          4,
          List(
            Span(2, 12, true, true),
            Span(12, 25, true, true),
            Span(0, 37, false, true),
            Span(5, 39, false, false),
          ),
          List(Tick(0), Tick(2), Tick(5), Tick(12), Tick(25), Tick(37), Tick(39)),
          List(Label(0, "-∞"), Label(2, "1"), Label(5, "2"), Label(12, "5"), Label(24, "10"), Label(36, "15"), Label(38, "+∞")),
          List(Legend("[1,5]"), Legend("[5,10]"), Legend("(-∞,15]"), Legend("(2,+∞)")),
          List(Annotation("a"), Annotation("b"), Annotation("c"), Annotation("d")),
        )

        actual shouldBe expected
      }

      "diagram several intervals with annotations" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val actual = Diagram.make(List(a, b), infView, canvas, List("a", "b"))
        val expected = Diagram(
          40,
          2,
          List(Span(2, 18, true, true), Span(18, 37, true, true)),
          List(Tick(2), Tick(18), Tick(37)),
          List(Label(2, "1"), Label(18, "5"), Label(36, "10")),
          List(Legend("[1,5]"), Legend("[5,10]")),
          List(Annotation("a"), Annotation("b")),
        )

        actual shouldBe expected
      }

      "diagram several intervals that include an empty one" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(2, 6)
        val c = Interval.empty[Int]

        val actual = Diagram.make(List(a, b, c), infView, canvas)
        val expected = Diagram(
          40,
          3,
          List(Span(2, 30, true, true), Span(9, 37, true, true), Span(1, -1, true, true)),
          List(Tick(2), Tick(9), Tick(30), Tick(37)),
          List(Label(2, "1"), Label(9, "2"), Label(30, "5"), Label(37, "6")),
          List(Legend("[1,5]"), Legend("[2,6]"), Legend("∅")),
          List(Annotation("a"), Annotation("b"), Annotation("c")),
        )

        actual shouldBe expected
      }

      "diagram overlapping intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(2, 6)
        val c = Interval.closed(3, 7)
        val d = Interval.closed(4, 8)
        val e = Interval.closed(5, 9)
        val f = Interval.closed(6, 10)

        val actual = Diagram.make(List(a, b, c, d, e, f), infView, canvas)
        val expected = Diagram(
          40,
          6,
          List(
            Span(2, 18, true, true),
            Span(6, 21, true, true),
            Span(10, 25, true, true),
            Span(14, 29, true, true),
            Span(18, 33, true, true),
            Span(21, 37, true, true),
          ),
          List(Tick(2), Tick(6), Tick(10), Tick(14), Tick(18), Tick(21), Tick(25), Tick(29), Tick(33), Tick(37)),
          List(
            Label(2, "1"),
            Label(6, "2"),
            Label(10, "3"),
            Label(14, "4"),
            Label(18, "5"),
            Label(21, "6"),
            Label(25, "7"),
            Label(29, "8"),
            Label(33, "9"),
            Label(36, "10"),
          ),
          List(Legend("[1,5]"), Legend("[2,6]"), Legend("[3,7]"), Legend("[4,8]"), Legend("[5,9]"), Legend("[6,10]")),
          List(Annotation("a"), Annotation("b"), Annotation("c"), Annotation("d"), Annotation("e"), Annotation("f")),
        )

        actual shouldBe expected
      }

      "diagram offset-date-time" in {
        given offsetDateTimeDomain: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.DAYS)
        val a                                              = Interval.closed(OffsetDateTime.parse("2020-07-02T12:34Z"), OffsetDateTime.parse("2021-07-02T12:34Z"))

        val odtView: View[OffsetDateTime] = View.all[OffsetDateTime]

        val actual = Diagram.make[OffsetDateTime](List(a), odtView, canvas)
        val expected = Diagram(
          40,
          1,
          List(Span(2, 37, true, true)),
          List(Tick(2), Tick(37)),
          List(Label(0, "2020-07-02T12:34Z"), Label(23, "2021-07-02T12:34Z")),
          List(Legend("[2020-07-02T12:34Z,2021-07-02T12:34Z]")),
          List(Annotation("a")),
        )

        actual shouldBe expected
      }
    }
  }
