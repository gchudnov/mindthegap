package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.diagram.AsciiCanvas
import com.github.gchudnov.mtg.diagram.Diagram
import com.github.gchudnov.mtg.diagram.Renderer
import com.github.gchudnov.mtg.diagram.Viewport
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final class AsciiDiagramSpec extends AnyWordSpec with Matchers:

  private val canvas: AsciiCanvas = AsciiCanvas.make(40, 2)

  "AsciiDiagram" when {
    "make" should {
      "diagram no intervals" in {
        val input                   = Diagram.empty[Int]
        val viewport: Viewport[Int] = Viewport.all[Int]

        val expected = AsciiDiagram.empty
        val actual   = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram an empty interval" in {
        val a = Interval.empty[Int]

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.all[Int]

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(1, -1, true, true)),
          ticks = List.empty[AsciiTick],
          labels = List.empty[AsciiLabel],
          legends = List(AsciiLegend("∅")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram a point" in {
        val a = Interval.point[Int](5) // [5]

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(20, 20, true, true)),
          ticks = List(AsciiTick(2), AsciiTick(20), AsciiTick(37)),
          labels = List(AsciiLabel(2, "4"), AsciiLabel(20, "5"), AsciiLabel(37, "6")),
          legends = List(AsciiLegend("{5}")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram two points" in {
        val a = Interval.point[Int](5)  // [5]
        val b = Interval.point[Int](10) // [10]

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))
        val viewport: Viewport[Int] = Viewport.make(List(a, b))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 2,
          List(AsciiSpan(2, 2, true, true), AsciiSpan(37, 37, true, true)),
          ticks = List(AsciiTick(2), AsciiTick(37)),
          labels = List(AsciiLabel(2, "5"), AsciiLabel(36, "10")),
          legends = List(AsciiLegend("{5}"), AsciiLegend("{10}")),
          annotations = List(AsciiAnnotation("a"), AsciiAnnotation("b")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(2, 37, true, true)),
          ticks = List(AsciiTick(2), AsciiTick(37)),
          labels = List(AsciiLabel(2, "5"), AsciiLabel(36, "10")),
          legends = List(AsciiLegend("[5,10]")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram a closed interval with a negative boundary" in {
        val a = Interval.closed[Int](-5, 10) // [-5, 10]

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(2, 37, true, true)),
          ticks = List(AsciiTick(2), AsciiTick(37)),
          labels = List(AsciiLabel(1, "-5"), AsciiLabel(36, "10")),
          legends = List(AsciiLegend("[-5,10]")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram an unbounded interval" in {
        val a = Interval.unbounded[Int] // (-∞, +∞)

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(0, 39, false, false)),
          ticks = List(AsciiTick(0), AsciiTick(39)),
          labels = List(AsciiLabel(0, "-∞"), AsciiLabel(38, "+∞")),
          legends = List(AsciiLegend("(-∞,+∞)")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram a leftOpen interval" in {
        val a = Interval.leftOpen(5) // (5, +∞)

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(20, 39, false, false)),
          ticks = List(AsciiTick(2), AsciiTick(20), AsciiTick(37), AsciiTick(39)),
          labels = List(AsciiLabel(2, "4"), AsciiLabel(20, "5"), AsciiLabel(37, "6"), AsciiLabel(38, "+∞")),
          legends = List(AsciiLegend("(5,+∞)")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram a leftClosed interval" in {
        val a = Interval.leftClosed(5) // [5, +∞)

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(20, 39, true, false)),
          ticks = List(AsciiTick(2), AsciiTick(20), AsciiTick(37), AsciiTick(39)),
          labels = List(AsciiLabel(2, "4"), AsciiLabel(20, "5"), AsciiLabel(37, "6"), AsciiLabel(38, "+∞")),
          legends = List(AsciiLegend("[5,+∞)")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram a rightOpen interval" in {
        val a = Interval.rightOpen(5) // (-∞, 5)

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(0, 20, false, false)),
          ticks = List(AsciiTick(0), AsciiTick(2), AsciiTick(20), AsciiTick(37)),
          labels = List(AsciiLabel(0, "-∞"), AsciiLabel(2, "4"), AsciiLabel(20, "5"), AsciiLabel(37, "6")),
          legends = List(AsciiLegend("(-∞,5)")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram a rightClosed interval" in {
        val a = Interval.rightClosed(5) // (-∞, 5]

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(0, 20, false, true)),
          ticks = List(AsciiTick(0), AsciiTick(2), AsciiTick(20), AsciiTick(37)),
          labels = List(AsciiLabel(0, "-∞"), AsciiLabel(2, "4"), AsciiLabel(20, "5"), AsciiLabel(37, "6")),
          legends = List(AsciiLegend("(-∞,5]")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram left part of a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val view                    = Viewport.make(Some(0), Some(7)) // [0, 7]
        val viewport: Viewport[Int] = Viewport.make(List(a))

        val input = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 1,
          List(AsciiSpan(27, 52, true, true)),
          ticks = List(AsciiTick(2), AsciiTick(27), AsciiTick(37), AsciiTick(52)),
          labels = List(AsciiLabel(2, "0"), AsciiLabel(27, "5"), AsciiLabel(37, "7"), AsciiLabel(51, "10")),
          legends = List(AsciiLegend("[5,10]")),
          annotations = List(AsciiAnnotation("a")),
        )
        val actual = AsciiDiagram.make(input, view, canvas)

        actual shouldBe expected
      }

      "diagram several intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d"))
        val viewport: Viewport[Int] = Viewport.make(List(a, b, c, d))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 4,
          List(AsciiSpan(2, 12, true, true), AsciiSpan(12, 25, true, true), AsciiSpan(0, 37, false, true), AsciiSpan(5, 39, false, false)),
          ticks = List(AsciiTick(0), AsciiTick(2), AsciiTick(5), AsciiTick(12), AsciiTick(25), AsciiTick(37), AsciiTick(39)),
          labels = List(
            AsciiLabel(0, "-∞"),
            AsciiLabel(2, "1"),
            AsciiLabel(5, "2"),
            AsciiLabel(12, "5"),
            AsciiLabel(24, "10"),
            AsciiLabel(36, "15"),
            AsciiLabel(38, "+∞"),
          ),
          legends = List(AsciiLegend("[1,5]"), AsciiLegend("[5,10]"), AsciiLegend("(-∞,15]"), AsciiLegend("(2,+∞)")),
          annotations = List(AsciiAnnotation("a"), AsciiAnnotation("b"), AsciiAnnotation("c"), AsciiAnnotation("d")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram several intervals with annotations" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))
        val viewport: Viewport[Int] = Viewport.make(List(a, b))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 2,
          List(AsciiSpan(2, 18, true, true), AsciiSpan(18, 37, true, true)),
          ticks = List(AsciiTick(2), AsciiTick(18), AsciiTick(37)),
          labels = List(AsciiLabel(2, "1"), AsciiLabel(18, "5"), AsciiLabel(36, "10")),
          legends = List(AsciiLegend("[1,5]"), AsciiLegend("[5,10]")),
          annotations = List(AsciiAnnotation("a"), AsciiAnnotation("b")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram several intervals that include an empty one" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(2, 6)
        val c = Interval.empty[Int]

        val input                   = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))
        val viewport: Viewport[Int] = Viewport.make(List(a, b, c))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 3,
          List(AsciiSpan(2, 30, true, true), AsciiSpan(9, 37, true, true), AsciiSpan(1, -1, true, true)),
          ticks = List(AsciiTick(2), AsciiTick(9), AsciiTick(30), AsciiTick(37)),
          labels = List(AsciiLabel(2, "1"), AsciiLabel(9, "2"), AsciiLabel(30, "5"), AsciiLabel(37, "6")),
          legends = List(AsciiLegend("[1,5]"), AsciiLegend("[2,6]"), AsciiLegend("∅")),
          annotations = List(AsciiAnnotation("a"), AsciiAnnotation("b"), AsciiAnnotation("c")),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }

      "diagram overlapping intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(2, 6)
        val c = Interval.closed(3, 7)
        val d = Interval.closed(4, 8)
        val e = Interval.closed(5, 9)
        val f = Interval.closed(6, 10)

        val input = Diagram
          .empty[Int]
          .withSection(
            _.addInterval(a, "a")
              .addInterval(b, "b")
              .addInterval(c, "c")
              .addInterval(d, "d")
              .addInterval(e, "e")
              .addInterval(f, "f")
          )
        val viewport: Viewport[Int] = Viewport.make(List(a, b, c, d, e, f))

        val expected = AsciiDiagram(
          title = "",
          width = 40,
          height = 6,
          List(
            AsciiSpan(2, 18, true, true),
            AsciiSpan(6, 21, true, true),
            AsciiSpan(10, 25, true, true),
            AsciiSpan(14, 29, true, true),
            AsciiSpan(18, 33, true, true),
            AsciiSpan(21, 37, true, true),
          ),
          ticks = List(
            AsciiTick(2),
            AsciiTick(6),
            AsciiTick(10),
            AsciiTick(14),
            AsciiTick(18),
            AsciiTick(21),
            AsciiTick(25),
            AsciiTick(29),
            AsciiTick(33),
            AsciiTick(37),
          ),
          labels = List(
            AsciiLabel(2, "1"),
            AsciiLabel(6, "2"),
            AsciiLabel(10, "3"),
            AsciiLabel(14, "4"),
            AsciiLabel(18, "5"),
            AsciiLabel(21, "6"),
            AsciiLabel(25, "7"),
            AsciiLabel(29, "8"),
            AsciiLabel(33, "9"),
            AsciiLabel(36, "10"),
          ),
          legends = List(
            AsciiLegend("[1,5]"),
            AsciiLegend("[2,6]"),
            AsciiLegend("[3,7]"),
            AsciiLegend("[4,8]"),
            AsciiLegend("[5,9]"),
            AsciiLegend("[6,10]"),
          ),
          annotations = List(
            AsciiAnnotation("a"),
            AsciiAnnotation("b"),
            AsciiAnnotation("c"),
            AsciiAnnotation("d"),
            AsciiAnnotation("e"),
            AsciiAnnotation("f"),
          ),
        )
        val actual = AsciiDiagram.make(input, viewport, canvas)

        actual shouldBe expected
      }
    }
  }
