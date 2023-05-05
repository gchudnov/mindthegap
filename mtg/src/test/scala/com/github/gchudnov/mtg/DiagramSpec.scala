package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.diagram.Renderer
import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
import com.github.gchudnov.mtg.Diagram.Legend
import com.github.gchudnov.mtg.Diagram.Annotation
import com.github.gchudnov.mtg.Diagram.View
import com.github.gchudnov.mtg.Domain
import java.time.OffsetDateTime
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

final class DiagramSpec extends TestSpec:

  private val canvas: Canvas     = Canvas.make(40, 2) // TODO: rename to canvas40p2
  private val infView: View[Int] = View.all[Int]
  private val theme: Theme       = Theme.default      // TODO: rename to defaultTheme

  private val themeNoLegend: Theme              = theme.copy(legend = false)
  private val themeNoLegendNoAnnotations: Theme = theme.copy(legend = false, annotations = false)

  private val renderer = Renderer.ascii

  "Diagram" when {
    "make" should {
      "diagram no intervals" in {
        val actual   = Diagram.make(List.empty[Interval[Int]], infView, canvas)
        val expected = Diagram.empty

        actual mustBe expected
      }

      "diagram an empty interval" in {
        val a = Interval.empty[Int]

        val actual   = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(40, 1, List(Span(1, -1, true, true)), List(), List(), List(Legend("∅")), List(Annotation("a")))

        actual mustBe expected
      }

      "diagram a point" in {
        val a = Interval.point[Int](5) // [5]

        val actual   = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(40, 1, List(Span(20, 20, true, true)), List(Tick(20)), List(Label(20, "5")), List(Legend("{5}")), List(Annotation("a")))

        actual mustBe expected
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
            List(Annotation("a"), Annotation("b"))
          )

        actual mustBe expected
      }

      "diagram a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val actual   = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(40, 1, List(Span(2, 37, true, true)), List(Tick(2), Tick(37)), List(Label(2, "5"), Label(36, "10")), List(Legend("[5,10]")), List(Annotation("a")))

        actual mustBe expected
      }

      "diagram a closed interval with a negative boundary" in {
        val a = Interval.closed[Int](-5, 10) // [-5, 10]

        val actual   = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(40, 1, List(Span(2, 37, true, true)), List(Tick(2), Tick(37)), List(Label(1, "-5"), Label(36, "10")), List(Legend("[-5,10]")), List(Annotation("a")))

        actual mustBe expected
      }

      "diagram an unbounded interval" in {
        val a = Interval.unbounded[Int] // (-∞, +∞)

        val actual = Diagram.make(List(a), infView, canvas)
        val expected =
          Diagram(40, 1, List(Span(0, 39, false, false)), List(Tick(0), Tick(39)), List(Label(0, "-∞"), Label(38, "+∞")), List(Legend("(-∞,+∞)")), List(Annotation("a")))

        actual mustBe expected
      }

      "diagram a leftOpen interval" in {
        val a = Interval.leftOpen(5) // (5, +∞)

        val actual = Diagram.make(List(a), infView, canvas)
        val expected =
          Diagram(40, 1, List(Span(20, 39, false, false)), List(Tick(20), Tick(39)), List(Label(20, "5"), Label(38, "+∞")), List(Legend("(5,+∞)")), List(Annotation("a")))

        actual mustBe expected
      }

      "diagram a leftClosed interval" in {
        val a = Interval.leftClosed(5) // [5, +∞)

        val actual = Diagram.make(List(a), infView, canvas)
        val expected =
          Diagram(40, 1, List(Span(20, 39, true, false)), List(Tick(20), Tick(39)), List(Label(20, "5"), Label(38, "+∞")), List(Legend("[5,+∞)")), List(Annotation("a")))

        actual mustBe expected
      }

      "diagram a rightOpen interval" in {
        val a = Interval.rightOpen(5) // (-∞, 5)

        val actual   = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(40, 1, List(Span(0, 20, false, false)), List(Tick(0), Tick(20)), List(Label(0, "-∞"), Label(20, "5")), List(Legend("(-∞,5)")), List(Annotation("a")))

        actual mustBe expected
      }

      "diagram a rightClosed interval" in {
        val a = Interval.rightClosed(5) // (-∞, 5]

        val actual   = Diagram.make(List(a), infView, canvas)
        val expected = Diagram(40, 1, List(Span(0, 20, false, true)), List(Tick(0), Tick(20)), List(Label(0, "-∞"), Label(20, "5")), List(Legend("(-∞,5]")), List(Annotation("a")))

        actual mustBe expected
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
          List(Annotation("a"))
        )

        actual mustBe expected
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
            Span(5, 39, false, false)
          ),
          List(Tick(0), Tick(2), Tick(5), Tick(12), Tick(25), Tick(37), Tick(39)),
          List(Label(0, "-∞"), Label(2, "1"), Label(5, "2"), Label(12, "5"), Label(24, "10"), Label(36, "15"), Label(38, "+∞")),
          List(Legend("[1,5]"), Legend("[5,10]"), Legend("(-∞,15]"), Legend("(2,+∞)")),
          List(Annotation("a"), Annotation("b"), Annotation("c"), Annotation("d"))
        )

        actual mustBe expected
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
          List(Annotation("a"), Annotation("b"))
        )

        actual mustBe expected
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
          List(Annotation("a"), Annotation("b"), Annotation("c"))
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
            Span(21, 37, true, true)
          ),
          List(Tick(2), Tick(6), Tick(10), Tick(14), Tick(18), Tick(21), Tick(25), Tick(29), Tick(33), Tick(37)),
          List(Label(2, "1"), Label(6, "2"), Label(10, "3"), Label(14, "4"), Label(18, "5"), Label(21, "6"), Label(25, "7"), Label(29, "8"), Label(33, "9"), Label(36, "10")),
          List(Legend("[1,5]"), Legend("[2,6]"), Legend("[3,7]"), Legend("[4,8]"), Legend("[5,9]"), Legend("[6,10]")),
          List(Annotation("a"), Annotation("b"), Annotation("c"), Annotation("d"), Annotation("e"), Annotation("f"))
        )

        actual mustBe expected
      }

      "diagram offset-date-time" in {
        val a = Interval.closed(OffsetDateTime.parse("2020-07-02T12:34Z"), OffsetDateTime.parse("2021-07-02T12:34Z"))

        val odtView: View[OffsetDateTime] = View.all[OffsetDateTime]

        val actual = Diagram.make[OffsetDateTime](List(a), odtView, canvas)
        val expected = Diagram(
          40,
          1,
          List(Span(2, 37, true, true)),
          List(Tick(2), Tick(37)),
          List(Label(0, "2020-07-02T12:34Z"), Label(23, "2021-07-02T12:34Z")),
          List(Legend("[2020-07-02T12:34Z,2021-07-02T12:34Z]")),
          List(Annotation("a"))
        )

        actual mustBe expected
      }
    }

    "render" should {
      "display no intervals" in {
        val diagram = Diagram.make(List.empty[Interval[Int]], infView, canvas)

        val actual   = Diagram.render(diagram, themeNoLegend)
        val expected = List.empty[String]

        actual mustBe expected
      }

      "display an empty interval with no legend and no annotations" in {
        val a       = Interval.empty[Int]
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegendNoAnnotations)
        val expected = List(
          "                                        ",
          "----------------------------------------",
          "                                        "
        )

        actual mustBe expected
      }

      "display an empty interval with no legend but with annotations" in {
        val a       = Interval.empty[Int]
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "                                         | a",
          "---------------------------------------- |",
          "                                         |"
        )

        actual mustBe expected
      }

      "display an empty interval with no legend but with annotations, but the intervals cannot be auto-annotated" in {
        val a       = Interval.empty[Int]
        val as      = List(a)
        val diagram = Diagram.make(as, infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "                                        ",
          "----------------------------------------",
          "                                        "
        )

        actual mustBe expected
      }

      "display an empty interval with a legend and annotations" in {
        val a       = Interval.empty[Int]
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, theme.copy(legend = true))
        val expected = List(
          "                                         | ∅ : a",
          "---------------------------------------- |",
          "                                         |"
        )

        actual mustBe expected
      }

      "display a point" in {
        val a       = Interval.point[Int](5) // [5]
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "                    *                    | a",
          "--------------------+------------------- |",
          "                    5                    |"
        )

        actual mustBe expected
      }

      "display two points" in {
        val a       = Interval.point[Int](5)  // [5]
        val b       = Interval.point[Int](10) // [10]
        val diagram = Diagram.make(List(a, b), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "  *                                      | a",
          "                                     *   | b",
          "--+----------------------------------+-- |",
          "  5                                 10   |"
        )

        actual mustBe expected
      }

      "display a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "  [**********************************]   | a",
          "--+----------------------------------+-- |",
          "  5                                 10   |"
        )

        actual mustBe expected
      }

      "display a closed interval on a custom view" in {
        val a       = Interval.closed[Int](5, 10)
        val view    = View.make(Some(0), Some(20))
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "           [********]                    | a",
          "--+--------+--------+----------------+-- |",
          "  0        5       10               20   |"
        )

        actual mustBe expected
      }

      "display left part of a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val view    = View.make(Some(0), Some(7))
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "                           [************ | a",
          "--+------------------------+---------+-- |",
          "  0                        5         7   |"
        )

        actual mustBe expected
      }

      "display right part of a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val view    = View.make(Some(7), Some(15))
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "***************]                         | a",
          "--+------------+---------------------+-- |",
          "  7           10                    15   |"
        )

        actual mustBe expected
      }

      "display middle part of a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val view    = View.make(Some(7), Some(8))
        val diagram = Diagram.make(List(a), view, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "**************************************** | a",
          "--+----------------------------------+-- |",
          "  7                                  8   |"
        )

        actual mustBe expected
      }

      "display a closed interval with negative boundary" in {
        val a       = Interval.closed[Int](-5, 10)
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "  [**********************************]   | a",
          "--+----------------------------------+-- |",
          " -5                                 10   |"
        )

        actual mustBe expected
      }

      "display an unbounded interval" in {
        val a       = Interval.unbounded[Int]
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "(**************************************) | a",
          "+--------------------------------------+ |",
          "-∞                                    +∞ |"
        )

        actual mustBe expected
      }

      "display a leftOpen interval" in {
        val a       = Interval.leftOpen(5) // (5, +∞)
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "                    (******************) | a",
          "--------------------+------------------+ |",
          "                    5                 +∞ |"
        )

        actual mustBe expected
      }

      "display a leftClosed interval" in {
        val a       = Interval.leftClosed(5) // [5, +∞)
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "                    [******************) | a",
          "--------------------+------------------+ |",
          "                    5                 +∞ |"
        )

        actual mustBe expected
      }

      "display a rightOpen interval" in {
        val a       = Interval.rightOpen(5) // (-∞, 5)
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "(*******************)                    | a",
          "+-------------------+------------------- |",
          "-∞                  5                    |"
        )

        actual mustBe expected
      }

      "display a rightClosed interval" in {
        val a       = Interval.rightClosed(5) // (-∞, 5]
        val diagram = Diagram.make(List(a), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "(*******************]                    | a",
          "+-------------------+------------------- |",
          "-∞                  5                    |"
        )

        actual mustBe expected
      }

      "display a leftClosed and rightClosed overlapping intervals" in {
        val a = Interval.leftClosed(5)   // [5, +∞)
        val b = Interval.rightClosed(10) // (-∞, 10]

        val diagram = Diagram.make(List(a, b), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "  [************************************) | a",
          "(************************************]   | b",
          "+-+----------------------------------+-+ |",
          "-∞5                                 10+∞ |"
        )

        actual mustBe expected
      }

      "display a leftClosed and rightClosed non-overlapping intervals" in {
        val a = Interval.leftClosed(10) // [10, +∞)
        val b = Interval.rightClosed(5) // (-∞, 5]

        val diagram = Diagram.make(List(a, b), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "                                     [*) | a",
          "(*]                                      | b",
          "+-+----------------------------------+-+ |",
          "-∞5                                 10+∞ |"
        )

        actual mustBe expected
      }

      "display several intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val diagram = Diagram.make(List(a, b, c, d), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend)
        val expected = List(
          "  [*********]                            | a",
          "            [************]               | b",
          "(************************************]   | c",
          "     (*********************************) | d",
          "+-+--+------+------------+-----------+-+ |",
          "-∞1  2      5           10          15+∞ |"
        )

        actual mustBe expected
      }

      "display several intervals with a legend" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val diagram = Diagram.make(List(a, b, c, d), infView, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  [*********]                            | [1,5]   : a",
          "            [************]               | [5,10]  : b",
          "(************************************]   | (-∞,15] : c",
          "     (*********************************) | (2,+∞)  : d",
          "+-+--+------+------------+-----------+-+ |",
          "-∞1  2      5           10          15+∞ |"
        )

        actual mustBe expected
      }

      "display several leftClosed intervals" in {
        val a = Interval.leftClosed(1)
        val b = Interval.leftClosed(2)
        val c = Interval.leftClosed(3)
        val d = Interval.leftClosed(4)

        val diagram = Diagram.make(List(a, b, c, d), infView, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  [************************************) | [1,+∞) : a",
          "              [************************) | [2,+∞) : b",
          "                         [*************) | [3,+∞) : c",
          "                                     [*) | [4,+∞) : d",
          "--+-----------+----------+-----------+-+ |",
          "  1           2          3           4+∞ |"
        )

        actual mustBe expected
      }

      "display several rightClosed intervals" in {
        val a = Interval.rightClosed(1)
        val b = Interval.rightClosed(2)
        val c = Interval.rightClosed(3)
        val d = Interval.rightClosed(4)

        val diagram = Diagram.make(List(a, b, c, d), infView, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "(*]                                      | (-∞,1] : a",
          "(*************]                          | (-∞,2] : b",
          "(************************]               | (-∞,3] : c",
          "(************************************]   | (-∞,4] : d",
          "+-+-----------+----------+-----------+-- |",
          "-∞1           2          3           4   |"
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

        val diagram = Diagram.make(List(a, b, c, d, e, f), infView, canvas)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  [***************]                      | [1,5]  : a",
          "      [**************]                   | [2,6]  : b",
          "          [**************]               | [3,7]  : c",
          "              [**************]           | [4,8]  : d",
          "                  [**************]       | [5,9]  : e",
          "                     [***************]   | [6,10] : f",
          "--+---+---+---+---+--+---+---+---+---+-- |",
          "  1   2   3   4   5  6   7   8   9  10   |"
        )

        actual mustBe expected
      }

      "display with default settings [doc]" in {
        val a = Interval.closed(3, 7)
        val b = Interval.closed(10, 15)
        val c = Interval.closed(12, 20)

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram)
        val expected = List(
          "  [*******]                              | [3,7]   : a",
          "                [**********]             | [10,15] : b",
          "                     [***************]   | [12,20] : c",
          "--+-------+-----+----+-----+---------+-- |",
          "  3       7    10   12    15        20   |"
        )

        actual mustBe expected
      }

      "display with custom view [doc]" in {
        val a = Interval.closed(3, 7)
        val b = Interval.closed(10, 15)
        val c = Interval.closed(12, 20)

        val view    = View.make(Some(8), Some(17))
        val diagram = Diagram.make(List(a, b, c), view)

        val actual = Diagram.render(diagram)
        val expected = List(
          "                                         | [3,7]   : a",
          "          [******************]           | [10,15] : b",
          "                  [********************* | [12,20] : c",
          "--+-------+-------+----------+-------+-- |",
          "  8      10      12         15      17   |"
        )

        actual mustBe expected
      }

      "display with custom canvas [doc]" in {
        val a = Interval.closed(3, 7)
        val b = Interval.closed(10, 15)
        val c = Interval.closed(12, 20)

        val canvas  = Canvas.make(20)
        val diagram = Diagram.make(List(a, b, c), canvas)

        val actual = Diagram.render(diagram)
        val expected = List(
          "  [***]              | [3,7]   : a",
          "        [****]       | [10,15] : b",
          "          [******]   | [12,20] : c",
          "--+---+-+-+--+---+-- |",
          "  3   7  12 15  20   |"
        )

        actual mustBe expected
      }

      "display with custom theme [doc]" in {
        val a = Interval.closed(3, 7)
        val b = Interval.closed(10, 15)
        val c = Interval.closed(12, 20)

        val canvas  = Canvas.make(20)
        val diagram = Diagram.make(List(a, b, c), canvas)

        val theme = Theme.default.copy(label = Theme.Label.Stacked)

        val actual = Diagram.render(diagram, theme)
        val expected = List(
          "  [***]              | [3,7]   : a",
          "        [****]       | [10,15] : b",
          "          [******]   | [12,20] : c",
          "--+---+-+-+--+---+-- |",
          "  3   7  12 15  20   |",
          "       10            |"
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

        val diagram = Diagram.make(List(a, b, c, d, e, f), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend.copy(label = Theme.Label.None))
        val expected = List(
          "  [***************]                      | a",
          "    [****************]                   | b",
          "      [******************]               | c",
          "        [********************]           | d",
          "          [**********************]       | e",
          "                     [***************]   | f",
          "--+-+-+-+-+-------+--+---+---+---+---+-- |",
          " 10152025300     500600 700 800 9001000  |"
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

        val diagram = Diagram.make(List(a, b, c, d, e, f), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend.copy(label = Theme.Label.NoOverlap))
        val expected = List(
          "  [***************]                      | a",
          "    [****************]                   | b",
          "      [******************]               | c",
          "        [********************]           | d",
          "          [**********************]       | e",
          "                     [***************]   | f",
          "--+-+-+-+-+-------+--+---+---+---+---+-- |",
          " 100 200 300     500    700 800 900      |"
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

        val diagram = Diagram.make(List(a, b, c, d, e, f), infView, canvas)

        val actual = Diagram.render(diagram, themeNoLegend.copy(label = Theme.Label.Stacked))
        val expected = List(
          "  [***************]                      | a",
          "    [****************]                   | b",
          "      [******************]               | c",
          "        [********************]           | d",
          "          [**********************]       | e",
          "                     [***************]   | f",
          "--+-+-+-+-+-------+--+---+---+---+---+-- |",
          " 100 200 300     500    700 800 900      |",
          "   150 250          600            1000  |"
        )

        actual mustBe expected
      }

      "display intervals with legend and annotations" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val diagram = Diagram.make(List(a, b), infView, canvas, List("a", "b"))

        val actual = Diagram.render(diagram, theme.copy(legend = true, annotations = true))
        val expected = List(
          "  [***************]                      | [1,5]  : a",
          "                  [******************]   | [5,10] : b",
          "--+---------------+------------------+-- |",
          "  1               5                 10   |"
        )

        actual mustBe expected
      }

      "display intervals with legend and without annotations" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val diagram = Diagram.make(List(a, b), infView, canvas, List("a", "b"))

        val actual = Diagram.render(diagram, theme.copy(legend = true, annotations = false))
        val expected = List(
          "  [***************]                      | [1,5]",
          "                  [******************]   | [5,10]",
          "--+---------------+------------------+-- |",
          "  1               5                 10   |"
        )

        actual mustBe expected
      }

      "display intervals without legend and with annotations" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val diagram = Diagram.make(List(a, b), infView, canvas, List("a", "b"))

        val actual = Diagram.render(diagram, theme.copy(legend = false, annotations = true))
        val expected = List(
          "  [***************]                      | a",
          "                  [******************]   | b",
          "--+---------------+------------------+-- |",
          "  1               5                 10   |"
        )

        actual mustBe expected
      }

      "display intervals without legend and without annotations" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val diagram = Diagram.make(List(a, b), infView, canvas, List("a", "b"))

        val actual = Diagram.render(diagram, theme.copy(legend = false, annotations = false))
        val expected = List(
          "  [***************]                     ",
          "                  [******************]  ",
          "--+---------------+------------------+--",
          "  1               5                 10  "
        )

        actual mustBe expected
      }

      "display intervals with OffsetDateTime" in {
        val a = Interval.closed(OffsetDateTime.parse("2020-07-02T12:34Z"), OffsetDateTime.parse("2021-07-02T12:34Z"))

        val diagram = Diagram.make[OffsetDateTime](List(a))

        val actual = Diagram.render(diagram, themeNoLegend.copy(label = Theme.Label.Stacked))
        val expected = List(
          "  [**********************************]   | a",
          "--+----------------------------------+-- |",
          "2020-07-02T12:34Z      2021-07-02T12:34Z |"
        )

        actual mustBe expected
      }

      "display intervals with Instant" in {
        val a = Interval.closed(Instant.parse("2020-07-02T12:34:00Z"), Instant.parse("2021-07-02T12:34:00Z"))

        val diagram = Diagram.make[Instant](List(a))

        val actual = Diagram.render(diagram, themeNoLegend.copy(label = Theme.Label.Stacked))
        val expected = List(
          "  [**********************************]   | a",
          "--+----------------------------------+-- |",
          "2020-07-02T12:34:00Z                     |",
          "                    2021-07-02T12:34:00Z |"
        )

        actual mustBe expected
      }

      "display several timelines" in {
        given Domain[LocalDate] = Domain.makeLocalDate(ChronoUnit.DAYS)

        val a = Interval.leftClosed(LocalDate.parse("2023-01-01"))
        val b = Interval.closed(LocalDate.parse("2023-01-03"), LocalDate.parse("2023-01-15"))
        val c = Interval.empty[LocalDate]
        val d = Interval.closed(LocalDate.parse("2023-01-10"), LocalDate.parse("2023-01-20"))

        val diagram = Diagram.make[LocalDate](List(a, b, c, d))

        val stackedLabelTheme = theme.copy(label = Theme.Label.Stacked)

        val actual = Diagram.render(diagram, stackedLabelTheme)
        val expected = List(
          "  [************************************) | [2023-01-01,+∞)         : a",
          "      [*********************]            | [2023-01-03,2023-01-15] : b",
          "                                         | ∅                       : c",
          "                   [*****************]   | [2023-01-10,2023-01-20] : d",
          "--+---+------------+--------+--------+-+ |",
          "2023-01-01    2023-01-10      2023-01-20 |",
          " 2023-01-03            2023-01-15     +∞ |"
        )

        actual mustBe expected
      }
    }

    "operations" should {
      "display a.intersection(b)" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val c = a.intersection(b)

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "                  [******************]   | [5,10] : a",
          "  [**********************]               | [1,7]  : b",
          "                  [******]               | [5,7]  : c",
          "--+---------------+------+-----------+-- |",
          "  1               5      7          10   |"
        )

        actual mustBe expected
      }

      "display a.span(b)" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val c = a.span(b)

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "                  [******************]   | [5,10] : a",
          "  [**********************]               | [1,7]  : b",
          "  [**********************************]   | [1,10] : c",
          "--+---------------+------+-----------+-- |",
          "  1               5      7          10   |"
        )

        actual mustBe expected
      }

      "display a.span(b) of two disjoint intervals" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(7, 10)

        val c = a.span(b)

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "  [***************]                      | [1,5]  : a",
          "                         [***********]   | [7,10] : b",
          "  [**********************************]   | [1,10] : c",
          "--+---------------+------+-----------+-- |",
          "  1               5      7          10   |"
        )

        actual mustBe expected
      }

      "display a.union(b)" in {
        val a = Interval.closed(1, 5)
        val b = Interval.closed(6, 10)

        val c = a.union(b)

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "  [***************]                      | [1,5]  : a",
          "                     [***************]   | [6,10] : b",
          "  [**********************************]   | [1,10] : c",
          "--+---------------+--+---------------+-- |",
          "  1               5  6              10   |"
        )

        actual mustBe expected
      }

      "display a.union(b) of two disjoint intervals" in {
        val a = Interval.closed(1, 4)
        val b = Interval.closed(6, 10)

        val c = a.union(b)

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "  [***********]                          | [1,4]  : a",
          "                     [***************]   | [6,10] : b",
          "                                         | ∅      : c",
          "--+-----------+------+---------------+-- |",
          "  1           4      6              10   |"
        )

        actual mustBe expected
      }

      "display a.gap(b) of two intersecting intervals" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(4, 15)

        val c = a.gap(b)

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "     [***************]                   | [5,10] : a",
          "  [**********************************]   | [4,15] : b",
          "                                         | ∅      : c",
          "--+--+---------------+---------------+-- |",
          "  4  5              10              15   |"
        )

        actual mustBe expected
      }

      "display a.gap(b) of two disjoint intervals" in {
        val a = Interval.closed(5, 10)
        val b = Interval.closed(15, 20)

        val c = a.gap(b).canonical

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "  [***********]                          | [5,10]  : a",
          "                         [***********]   | [15,20] : b",
          "                [******]                 | [11,14] : c",
          "--+-----------+-+------+-+-----------+-- |",
          "  5          10       14            20   |"
        )

        actual mustBe expected
      }

      "display a.minus(b) if a.overlaps(b)" in {
        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 15)

        val c = a.minus(b).canonical

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "  [**********************]               | [1,10] : a",
          "            [************************]   | [5,15] : b",
          "  [*******]                              | [1,4]  : c",
          "--+-------+-+------------+-----------+-- |",
          "  1       4 5           10          15   |"
        )

        actual mustBe expected
      }

      "display a.minus(b) if a.isOverlappedBy(b)" in {
        val a = Interval.closed(5, 15)
        val b = Interval.closed(1, 10)

        val c = a.minus(b).canonical

        val diagram = Diagram.make(List(a, b, c))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "            [************************]   | [5,15]  : a",
          "  [**********************]               | [1,10]  : b",
          "                           [*********]   | [11,15] : c",
          "--+---------+------------+-+---------+-- |",
          "  1         5           10          15   |"
        )

        actual mustBe expected
      }

      "display Interval.minus(a, b) if a.contains(b)" in {
        val a = Interval.closed(1, 15)
        val b = Interval.closed(5, 10)

        val cs = Interval.minus(a, b).map(_.canonical)

        val diagram = Diagram.make(List(a, b) ++ cs, infView, canvas, List("a", "b", "c1", "c2"))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "  [**********************************]   | [1,15]  : a",
          "            [************]               | [5,10]  : b",
          "  [*******]                              | [1,4]   : c1",
          "                           [*********]   | [11,15] : c2",
          "--+-------+-+------------+-+---------+-- |",
          "  1       4 5           10          15   |"
        )

        actual mustBe expected
      }

      "display short intervals" in {
        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)
        val d = Interval.closed(60, 70)
        val e = Interval.closed(71, 80)

        val input  = List(a, b, c, d, e)
        val splits = Interval.split(input)

        val diagram = Diagram.make(input ++ splits, infView, canvas, List("a", "b", "c", "d", "e", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8"))

        val actual = Diagram.render(diagram, theme)

        val expected = List(
          "  [***]                                  | [0,10]  : a",
          "   [********************]                | [3,50]  : b",
          "           [***]                         | [20,30] : c",
          "                            [****]       | [60,70] : d",
          "                                 [***]   | [71,80] : e",
          "  [)                                     | [0,3)   : s0",
          "   [**]                                  | [3,10]  : s1",
          "      (****)                             | (10,20) : s2",
          "           [***]                         | [20,30] : s3",
          "               (********]                | (30,50] : s4",
          "                        (***)            | (50,60) : s5",
          "                            [****]       | [60,70] : s6",
          "                                 [***]   | [71,80] : s7",
          "--++--+----+---+--------+---+----+---+-- |",
          "  0  10   20  30       50  60   70  80   |"
        )

        actual mustBe expected
      }
    }
  }
