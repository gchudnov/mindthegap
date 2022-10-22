package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
import com.github.gchudnov.mtg.Diagram.Legend
import com.github.gchudnov.mtg.Diagram.View
import java.time.OffsetDateTime
import java.time.Instant

final class DiagramSpec extends TestSpec:

  private val canvas: Canvas  = Canvas.make(40, 2)
  private val view: View[Int] = View.empty[Int]
  private val theme: Theme    = Theme.default

  "Diagram" when {
    "make" should {
      "diagram an empty interval" in {
        val a = Interval.empty[Int]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram.empty

        actual mustBe expected
      }

      "diagram a point" in {
        val a = Interval.degenerate[Int](5) // [5]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(20, 20, true, true)), List(Tick(20)), List(Label(20, "5")), List(Legend("{5}")))

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
            List(Span(2, 2, true, true), Span(37, 37, true, true)),
            List(Tick(2), Tick(37)),
            List(Label(2, "5"), Label(36, "10")),
            List(Legend("{5}"), Legend("{10}"))
          )

        actual mustBe expected
      }

      "diagram a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 37, true, true)), List(Tick(2), Tick(37)), List(Label(2, "5"), Label(36, "10")), List(Legend("[5,10]")))

        actual mustBe expected
      }

      "diagram a closed interval with a negative boundary" in {
        val a = Interval.closed[Int](-5, 10) // [-5, 10]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 37, true, true)), List(Tick(2), Tick(37)), List(Label(1, "-5"), Label(36, "10")), List(Legend("[-5,10]")))

        actual mustBe expected
      }

      "diagram an unbounded interval" in {
        val a = Interval.unbounded[Int] // (-∞, +∞)

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(0, 39, false, false)), List(Tick(0), Tick(39)), List(Label(0, "-∞"), Label(38, "+∞")), List(Legend("(-∞,+∞)")))

        actual mustBe expected
      }

      "diagram a leftOpen interval" in {
        val a = Interval.leftOpen(5) // (5, +∞)

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 39, false, false)), List(Tick(2), Tick(39)), List(Label(2, "5"), Label(38, "+∞")), List(Legend("(5,+∞)")))

        actual mustBe expected
      }

      "diagram a leftClosed interval" in {
        val a = Interval.leftClosed(5) // [5, +∞)

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 39, true, false)), List(Tick(2), Tick(39)), List(Label(2, "5"), Label(38, "+∞")), List(Legend("[5,+∞)")))

        actual mustBe expected
      }

      "diagram a rightOpen interval" in {
        val a = Interval.rightOpen(5) // (-∞, 5)

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(0, 37, false, false)), List(Tick(0), Tick(37)), List(Label(0, "-∞"), Label(37, "5")), List(Legend("(-∞,5)")))

        actual mustBe expected
      }

      "diagram a rightClosed interval" in {
        val a = Interval.rightClosed(5) // (-∞, 5]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(0, 37, false, true)), List(Tick(0), Tick(37)), List(Label(0, "-∞"), Label(37, "5")), List(Legend("(-∞,5]")))

        actual mustBe expected
      }

      "diagram left part of a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val actual = Diagram.make(List(a), view.copy(left = Some(0), right = Some(7)), canvas) // [0, 7]
        val expected = Diagram(
          40,
          1,
          List(Span(27, 52, true, true)),
          List(Tick(2), Tick(27), Tick(37), Tick(52)),
          List(Label(2, "0"), Label(27, "5"), Label(37, "7"), Label(51, "10")),
          List(Legend("[5,10]"))
        )

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
            Span(2, 12, true, true),
            Span(12, 25, true, true),
            Span(0, 37, false, true),
            Span(5, 39, false, false)
          ),
          List(Tick(0), Tick(2), Tick(5), Tick(12), Tick(25), Tick(37), Tick(39)),
          List(Label(0, "-∞"), Label(2, "1"), Label(5, "2"), Label(12, "5"), Label(24, "10"), Label(36, "15"), Label(38, "+∞")),
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
            Span(2, 18, true, true),
            Span(6, 21, true, true),
            Span(10, 25, true, true),
            Span(14, 29, true, true),
            Span(18, 33, true, true),
            Span(21, 37, true, true)
          ),
          List(Tick(2), Tick(6), Tick(10), Tick(14), Tick(18), Tick(21), Tick(25), Tick(29), Tick(33), Tick(37)),
          List(Label(2, "1"), Label(6, "2"), Label(10, "3"), Label(14, "4"), Label(18, "5"), Label(21, "6"), Label(25, "7"), Label(29, "8"), Label(33, "9"), Label(36, "10")),
          List(Legend("[1,5]"), Legend("[2,6]"), Legend("[3,7]"), Legend("[4,8]"), Legend("[5,9]"), Legend("[6,10]"))
        )

        actual mustBe expected
      }

      "diagram offset-date-time" in {
        import Diagram.given

        val odtView: View[OffsetDateTime] = View.empty[OffsetDateTime]
        val a                             = Interval.closed(OffsetDateTime.parse("2020-07-02T12:34Z"), OffsetDateTime.parse("2021-07-02T12:34Z"))

        val actual = Diagram.make[OffsetDateTime](List(a), odtView, canvas)
        val expected = Diagram(
          40,
          1,
          List(Span(2, 37, true, true)),
          List(Tick(2), Tick(37)),
          List(Label(0, "2020-07-02T12:34Z"), Label(23, "2021-07-02T12:34Z")),
          List(Legend("[2020-07-02T12:34Z,2021-07-02T12:34Z]"))
        )

        actual mustBe expected
      }
    }

    "renderer" should {

      "Theme.Label.None" should {
        val noneLabelTheme = theme.copy(label = Theme.Label.None)

        "draw an empty collection of labels" in {
          val ls = List.empty[Label]

          val r = new Diagram.BasicRenderer(noneLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("                                        ")

          actual mustBe expected
        }

        "draw non-overlapping labels" in {
          val ls = List(Label(2, "5"), Label(36, "10"))

          val r = new Diagram.BasicRenderer(noneLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("  5                                 10  ")

          actual mustBe expected
        }

        "draw meeting labels" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new Diagram.BasicRenderer(noneLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("-∞1  2      5           10          15+∞")

          actual mustBe expected
        }

        "draw overlapping labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"), Label(4, "400"))

          val r = new Diagram.BasicRenderer(noneLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("1003400                                 ")

          actual mustBe expected
        }
      }

      "Theme.Label.NoOverlap" should {
        val noOverlapLabelTheme = theme.copy(label = Theme.Label.NoOverlap)

        "draw an empty collection of labels" in {
          val ls = List.empty[Label]

          val r = new Diagram.BasicRenderer(noOverlapLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("                                        ")

          actual mustBe expected
        }

        "draw only non-overlapping labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"), Label(4, "400"))

          val r = new Diagram.BasicRenderer(noOverlapLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("100 400                                 ")

          actual mustBe expected
        }

        "draw meeting labels if one of them is non-numeric" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new Diagram.BasicRenderer(noOverlapLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("-∞1  2      5           10          15+∞")

          actual mustBe expected
        }

        "draw only non-meeting labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"))

          val r = new Diagram.BasicRenderer(noOverlapLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("100                                     ")

          actual mustBe expected
        }
      }

      "Theme.Label.Stacked" should {
        val stackedLabelTheme = theme.copy(label = Theme.Label.Stacked)

        "draw an empty collection of labels" in {
          val ls = List.empty[Label]

          val r = new Diagram.BasicRenderer(stackedLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("                                        ")

          actual mustBe expected
        }

        "draw non-overlapping and non-meetinglabels on one line" in {
          val ls = List(Label(2, "5"), Label(36, "10"))

          val r = new Diagram.BasicRenderer(stackedLabelTheme)

          val actual   = r.drawLabels(ls, canvas.width)
          val expected = List("  5                                 10  ")

          actual mustBe expected
        }

        "draw meeting labels" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new Diagram.BasicRenderer(stackedLabelTheme)

          val actual = r.drawLabels(ls, canvas.width)
          val expected = List(
            "-∞   2      5           10          15  ",
            "  1                                   +∞"
          )

          actual mustBe expected
        }

        "draw overlapping labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"), Label(4, "400"))

          val r = new Diagram.BasicRenderer(stackedLabelTheme)

          val actual = r.drawLabels(ls, canvas.width)
          val expected = List(
            "100 400                                 ",
            "   300                                  "
          )

          actual mustBe expected
        }
      }

      "draw an empty collection of ticks" in {
        val ts = List.empty[Tick]

        val r = new Diagram.BasicRenderer(theme)

        val actual   = r.drawTicks(ts, canvas.width)
        val expected = List("----------------------------------------")

        actual mustBe expected
      }

      "draw ticks" in {
        val ts = List(Tick(2), Tick(12), Tick(25), Tick(0), Tick(37), Tick(5), Tick(39))

        val r = new Diagram.BasicRenderer(theme)

        val actual   = r.drawTicks(ts, canvas.width)
        val expected = List("+-+--+------+------------+-----------+-+")

        actual mustBe expected
      }
    }

    "render" should {
      "display an empty interval" in {
        val a       = Interval.empty[Int]
        val diagram = Diagram.make(List(a), view, canvas)

        val actual   = Diagram.render(diagram, theme)
        val expected = List.empty[String]

        actual mustBe expected
      }

      "display an empty interval with a legend" in {
        val a       = Interval.empty[Int]
        val diagram = Diagram.make(List(a), view, canvas)

        val actual   = Diagram.render(diagram, theme.copy(legend = true))
        val expected = List.empty[String]

        actual mustBe expected
      }

      "display a point" in {
        val a       = Interval.degenerate[Int](5) // [5]
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "                    *                   ",
          "--------------------+-------------------",
          "                    5                   "
        )

        actual mustBe expected
      }

      "display two points" in {
        val a       = Interval.degenerate[Int](5)  // [5]
        val b       = Interval.degenerate[Int](10) // [10]
        val diagram = Diagram.make(List(a, b), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  *                                     ",
          "                                     *  ",
          "--+----------------------------------+--",
          "  5                                 10  "
        )

        actual mustBe expected
      }

      "display a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  [**********************************]  ",
          "--+----------------------------------+--",
          "  5                                 10  "
        )

        actual mustBe expected
      }

      "display a closed interval on a custom view" in {
        val a       = Interval.closed[Int](5, 10)
        val diagram = Diagram.make(List(a), view.copy(left = Some(0), right = Some(20)), canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "           [********]                   ",
          "--+--------+--------+----------------+--",
          "  0        5       10               20  "
        )

        actual mustBe expected
      }

      "display left part of a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val diagram = Diagram.make(List(a), view.copy(left = Some(0), right = Some(7)), canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "                           [************",
          "--+------------------------+---------+--",
          "  0                        5         7  "
        )

        actual mustBe expected
      }

      "display right part of a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val diagram = Diagram.make(List(a), view.copy(left = Some(7), right = Some(15)), canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "***************]                        ",
          "--+------------+---------------------+--",
          "  7           10                    15  "
        )

        actual mustBe expected
      }

      "display middle part of a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val diagram = Diagram.make(List(a), view.copy(left = Some(7), right = Some(8)), canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "****************************************",
          "--+----------------------------------+--",
          "  7                                  8  "
        )

        actual mustBe expected
      }

      "display a closed interval with negative boundary" in {
        val a       = Interval.closed[Int](-5, 10)
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  [**********************************]  ",
          "--+----------------------------------+--",
          " -5                                 10  "
        )

        actual mustBe expected
      }

      "display an unbounded interval" in {
        val a       = Interval.unbounded[Int]
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "(**************************************)",
          "+--------------------------------------+",
          "-∞                                    +∞"
        )

        actual mustBe expected
      }

      "display a leftOpen interval" in {
        val a       = Interval.leftOpen(5) // (5, +∞)
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  (************************************)",
          "--+------------------------------------+",
          "  5                                   +∞"
        )

        actual mustBe expected
      }

      "display a leftClosed interval" in {
        val a       = Interval.leftClosed(5) // [5, +∞)
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val exptected = List(
          "  [************************************)",
          "--+------------------------------------+",
          "  5                                   +∞"
        )

        actual mustBe exptected
      }

      "display a rightOpen interval" in {
        val a       = Interval.rightOpen(5) // (-∞, 5)
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "(************************************)  ",
          "+------------------------------------+--",
          "-∞                                   5  "
        )

        actual mustBe expected
      }

      "display a rightClosed interval" in {
        val a       = Interval.rightClosed(5) // (-∞, 5]
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "(************************************]  ",
          "+------------------------------------+--",
          "-∞                                   5  "
        )

        actual mustBe expected
      }

      "display several intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val diagram = Diagram.make(List(a, b, c, d), view, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  [*********]                           ",
          "            [************]              ",
          "(************************************]  ",
          "     (*********************************)",
          "+-+--+------+------------+-----------+-+",
          "-∞1  2      5           10          15+∞"
        )

        actual mustBe expected
      }

      "display several intervals with legend" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val diagram = Diagram.make(List(a, b, c, d), view, canvas)

        val actual = Diagram.render(diagram, theme.copy(legend = true))
        val expected = List(
          "  [*********]                            | [1,5]",
          "            [************]               | [5,10]",
          "(************************************]   | (-∞,15]",
          "     (*********************************) | (2,+∞)",
          "+-+--+------+------------+-----------+-+ |",
          "-∞1  2      5           10          15+∞ |"
        )

        actual mustBe expected
      }

      "display overlapping intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(2, 6)
        val c = Interval.closed(3, 7)
        val d = Interval.closed(4, 8)
        val e = Interval.closed(5, 9)
        val f = Interval.closed(6, 10)

        val diagram = Diagram.make(List(a, b, c, d, e, f), view, canvas)

        val actual = Diagram.render(diagram, theme.copy(legend = true))
        val expected = List(
          "  [***************]                      | [1,5]",
          "      [**************]                   | [2,6]",
          "          [**************]               | [3,7]",
          "              [**************]           | [4,8]",
          "                  [**************]       | [5,9]",
          "                     [***************]   | [6,10]",
          "--+---+---+---+---+--+---+---+---+---+-- |",
          "  1   2   3   4   5  6   7   8   9  10   |"
        )

        actual mustBe expected
      }

      "display overlapping intervals with overlapping labels (Theme.Label.None)" in {
        val a = Interval.closed(100, 500)
        val b = Interval.closed(150, 600)
        val c = Interval.closed(200, 700)
        val d = Interval.closed(250, 800)
        val e = Interval.closed(300, 900)
        val f = Interval.closed(600, 1000)

        val diagram = Diagram.make(List(a, b, c, d, e, f), view, canvas)

        val actual = Diagram.render(diagram, theme.copy(label = Theme.Label.None))
        val expected = List(
          "  [***************]                     ",
          "    [****************]                  ",
          "      [******************]              ",
          "        [********************]          ",
          "          [**********************]      ",
          "                     [***************]  ",
          "--+-+-+-+-+-------+--+---+---+---+---+--",
          " 10152025300     500600 700 800 9001000 "
        )

        actual mustBe expected
      }

      "display overlapping intervals with overlapping labels (Theme.Label.NoOverlap)" in {
        val a = Interval.closed(100, 500)
        val b = Interval.closed(150, 600)
        val c = Interval.closed(200, 700)
        val d = Interval.closed(250, 800)
        val e = Interval.closed(300, 900)
        val f = Interval.closed(600, 1000)

        val diagram = Diagram.make(List(a, b, c, d, e, f), view, canvas)

        val actual = Diagram.render(diagram, theme.copy(label = Theme.Label.NoOverlap))
        val expected = List(
          "  [***************]                     ",
          "    [****************]                  ",
          "      [******************]              ",
          "        [********************]          ",
          "          [**********************]      ",
          "                     [***************]  ",
          "--+-+-+-+-+-------+--+---+---+---+---+--",
          " 100 200 300     500    700 800 900     "
        )

        actual mustBe expected
      }

      "display overlapping intervals with overlapping labels (Theme.Label.Stacked)" in {
        val a = Interval.closed(100, 500)
        val b = Interval.closed(150, 600)
        val c = Interval.closed(200, 700)
        val d = Interval.closed(250, 800)
        val e = Interval.closed(300, 900)
        val f = Interval.closed(600, 1000)

        val diagram = Diagram.make(List(a, b, c, d, e, f), view, canvas)

        val actual = Diagram.render(diagram, theme.copy(label = Theme.Label.Stacked))
        val expected = List(
          "  [***************]                     ",
          "    [****************]                  ",
          "      [******************]              ",
          "        [********************]          ",
          "          [**********************]      ",
          "                     [***************]  ",
          "--+-+-+-+-+-------+--+---+---+---+---+--",
          " 100 200 300     500    700 800 900     ",
          "   150 250          600            1000 "
        )

        actual mustBe expected
      }

      "display intervals with OffsetDateTime" in {
        import Diagram.given

        val a = Interval.closed(OffsetDateTime.parse("2020-07-02T12:34Z"), OffsetDateTime.parse("2021-07-02T12:34Z"))

        val diagram = Diagram.make[OffsetDateTime](List(a), canvas)

        val actual = Diagram.render(diagram, theme.copy(label = Theme.Label.Stacked))
        val expected = List(
          "  [**********************************]  ",
          "--+----------------------------------+--",
          "2020-07-02T12:34Z      2021-07-02T12:34Z"
        )

        actual mustBe expected
      }

      "display intervals with Instant" in {
        import Diagram.given

        val a = Interval.closed(Instant.parse("2020-07-02T12:34:00Z"), Instant.parse("2021-07-02T12:34:00Z"))

        val diagram = Diagram.make[Instant](List(a), canvas)

        val actual = Diagram.render(diagram, theme.copy(label = Theme.Label.Stacked))
        val expected = List(
          "  [**********************************]  ",
          "--+----------------------------------+--",
          "2020-07-02T12:34:00Z                    ",
          "                    2021-07-02T12:34:00Z"
        )

        actual mustBe expected
      }
    }
  }
