package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
import com.github.gchudnov.mtg.Diagram.Legend
import com.github.gchudnov.mtg.Diagram.View

final class DiagramSpec extends TestSpec:

  private val canvas: Canvas  = Canvas.make(40, 2)
  private val view: View[Int] = View.empty
  private val theme: Theme    = Theme.default

  "Diagram" when {
    "make" should {
      "diagram a point" in {
        val a = Interval.degenerate[Int](5) // [5]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(20, 20, 0, true, true)), List(Tick(20)), List(Label(20, "5")), List(Legend("{5}")))

        actual mustBe expected
      }

      "diagram two points" in {
        val a = Interval.degenerate[Int](5)  // [5]
        val b = Interval.degenerate[Int](10) // [10]

        val actual = Diagram.make(List(a, b), view, canvas)
        val expected =
          Diagram(
            40,
            2,
            List(Span(2, 2, 0, true, true), Span(37, 37, 1, true, true)),
            List(Tick(2), Tick(37)),
            List(Label(2, "5"), Label(36, "10")),
            List(Legend("{5}"), Legend("{10}"))
          )

        actual mustBe expected
      }

      "diagram a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 37, 0, true, true)), List(Tick(2), Tick(37)), List(Label(2, "5"), Label(36, "10")), List(Legend("[5,10]")))

        actual mustBe expected
      }

      "diagram a closed interval with a negative boundary" in {
        val a = Interval.closed[Int](-5, 10) // [-5, 10]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 37, 0, true, true)), List(Tick(2), Tick(37)), List(Label(1, "-5"), Label(36, "10")), List(Legend("[-5,10]")))

        actual mustBe expected
      }

      "diagram an unbounded interval" in {
        val a = Interval.unbounded[Int] // (-∞, +∞)

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(0, 39, 0, false, false)), List(Tick(0), Tick(39)), List(Label(0, "-∞"), Label(38, "+∞")), List(Legend("(-∞,+∞)")))

        actual mustBe expected
      }

      "diagram a leftOpen interval" in {
        val a = Interval.leftOpen(5) // (5, +∞)

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 39, 0, false, false)), List(Tick(2), Tick(39)), List(Label(2, "5"), Label(38, "+∞")), List(Legend("(5,+∞)")))

        actual mustBe expected
      }

      "diagram a leftClosed interval" in {
        val a = Interval.leftClosed(5) // [5, +∞)

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 39, 0, true, false)), List(Tick(2), Tick(39)), List(Label(2, "5"), Label(38, "+∞")), List(Legend("[5,+∞)")))

        actual mustBe expected
      }

      "diagram a rightOpen interval" in {
        val a = Interval.rightOpen(5) // (-∞, 5)

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(0, 37, 0, false, false)), List(Tick(0), Tick(37)), List(Label(0, "-∞"), Label(37, "5")), List(Legend("(-∞,5)")))

        actual mustBe expected
      }

      "diagram a rightClosed interval" in {
        val a = Interval.rightClosed(5) // (-∞, 5]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(0, 37, 0, false, true)), List(Tick(0), Tick(37)), List(Label(0, "-∞"), Label(37, "5")), List(Legend("(-∞,5]")))

        actual mustBe expected
      }

      "diagram several intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val actual = Diagram.make(List(a, b, c, d), view, canvas)
        val expected = Diagram(
          40,
          4,
          List(
            Span(2, 12, 0, true, true),
            Span(12, 25, 1, true, true),
            Span(0, 37, 2, false, true),
            Span(5, 39, 3, false, false)
          ),
          List(Tick(2), Tick(12), Tick(25), Tick(0), Tick(37), Tick(5), Tick(39)),
          List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞")),
          List(Legend("[1,5]"), Legend("[5,10]"), Legend("(-∞,15]"), Legend("(2,+∞)"))
        )

        actual mustBe expected
      }

      "diagram overlapping intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(2, 6)
        val c = Interval.closed(3, 7)
        val d = Interval.closed(4, 8)
        val e = Interval.closed(5, 9)
        val f = Interval.closed(6, 10)

        val actual = Diagram.make(List(a, b, c, d, e, f), view, canvas)
        val expected = Diagram(
          40,
          6,
          List(
            Span(2, 18, 0, true, true),
            Span(6, 21, 1, true, true),
            Span(10, 25, 2, true, true),
            Span(14, 29, 3, true, true),
            Span(18, 33, 4, true, true),
            Span(21, 37, 5, true, true)
          ),
          List(Tick(2), Tick(18), Tick(6), Tick(21), Tick(10), Tick(25), Tick(14), Tick(29), Tick(33), Tick(37)),
          List(Label(2, "1"), Label(18, "5"), Label(6, "2"), Label(21, "6"), Label(10, "3"), Label(25, "7"), Label(14, "4"), Label(29, "8"), Label(33, "9"), Label(36, "10")),
          List(Legend("[1,5]"), Legend("[2,6]"), Legend("[3,7]"), Legend("[4,8]"), Legend("[5,9]"), Legend("[6,10]"))
        )

        actual mustBe expected
      }
    }

    "renderer" should {

      "Theme.Label.None" should {
        val noneLabelTheme = theme.copy(label = Theme.Label.None)

        "draw non-overlapping labels" in {
          val ls = List(Label(2, "5"), Label(36, "10"))

          val r = new Diagram.BasicRenderer(noneLabelTheme)

          val actual   = r.drawLabels(ls, 40)
          val expected = List("  5                                 10  ")

          actual mustBe expected
        }

        "draw meeting labels" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new Diagram.BasicRenderer(noneLabelTheme)

          val actual   = r.drawLabels(ls, 40)
          val expected = List("-∞1  2      5           10          15+∞")

          actual mustBe expected
        }

        "draw overlapping labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"), Label(4, "400"))

          val r = new Diagram.BasicRenderer(noneLabelTheme)

          val actual   = r.drawLabels(ls, 40)
          val expected = List("1003400                                 ")

          actual mustBe expected
        }
      }

      "Theme.Label.NoOverlap" should {
        val noOverlapLabelTheme = theme.copy(label = Theme.Label.NoOverlap)

        "draw only non-overlapping labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"), Label(4, "400"))

          val r = new Diagram.BasicRenderer(noOverlapLabelTheme)

          val actual   = r.drawLabels(ls, 40)
          val expected = List("100 400                                 ")

          actual mustBe expected
        }

        "draw only non-meeting labels" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new Diagram.BasicRenderer(noOverlapLabelTheme)

          val actual   = r.drawLabels(ls, 40)
          val expected = List("-∞   2      5           10          15  ")

          actual mustBe expected
        }
      }

      "Theme.Label.Stacked" should {
        val stackedLabelTheme = theme.copy(label = Theme.Label.Stacked)

        "draw non-overlapping and non-meetinglabels on one line" in {
          val ls = List(Label(2, "5"), Label(36, "10"))

          val r = new Diagram.BasicRenderer(stackedLabelTheme)

          val actual   = r.drawLabels(ls, 40)
          val expected = List("  5                                 10  ")

          actual mustBe expected
        }

        "draw meeting labels as stacked" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new Diagram.BasicRenderer(stackedLabelTheme)

          val actual = r.drawLabels(ls, 40)
          val expected = List(
            "  1  2      5           10          15  ",
            "-∞                                      ",
            "                                      +∞"
          )

          actual mustBe expected
        }
      }

    }
  }
