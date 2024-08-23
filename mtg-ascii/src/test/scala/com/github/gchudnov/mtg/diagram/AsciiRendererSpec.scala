package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.diagram.internal.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final class AsciiRendererSpec extends AnyWordSpec with Matchers:

  private val infView: Viewport[Int] = Viewport.all[Int]

  private val themeDefault: AsciiTheme               = AsciiTheme.default
  private val themeNoLegend: AsciiTheme              = themeDefault.copy(hasLegend = false)
  private val themeNoLegendNoAnnotations: AsciiTheme = themeDefault.copy(hasLegend = false, hasAnnotations = false)

  "AsciiRenderer" when {
    "render" should {
      "display no intervals" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val diagram = Diagram.empty[Int]
        renderer.render(diagram)

        val actual   = renderer.result
        val expected = ""

        actual shouldBe expected
      }

      "display an empty interval with no legend and no annotations" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegendNoAnnotations)

        val a       = Interval.empty[Int]
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "                                        ",
          "----------------------------------------",
          "                                        ",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display an empty interval with no legend but with annotations" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.empty[Int]
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))
        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "                                         | a",
          "---------------------------------------- |",
          "                                         |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display an empty interval with a legend and annotations" in {
        val renderer = AsciiRenderer.make[Int](themeDefault.copy(hasLegend = true))

        val a       = Interval.empty[Int]
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "                                         | ∅ : a",
          "---------------------------------------- |",
          "                                         |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a point" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.point[Int](5) // [5]
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "                    *                    | a",
          "--+-----------------+----------------+-- |",
          "  4                 5                6   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display two points" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.point[Int](5)  // [5]
        val b       = Interval.point[Int](10) // [10]
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  *                                      | a",
          "                                     *   | b",
          "--+----------------------------------+-- |",
          "  5                                 10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a closed interval" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.closed[Int](5, 10)
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [**********************************]   | a",
          "--+----------------------------------+-- |",
          "  5                                 10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a closed interval with negative boundary" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.closed[Int](-5, 10)
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [**********************************]   | a",
          "--+----------------------------------+-- |",
          " -5                                 10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display an unbounded interval" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.unbounded[Int]
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "(**************************************) | a",
          "+--------------------------------------+ |",
          "-∞                                    +∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a leftOpen interval" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.leftOpen(5) // (5, +∞)
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "                    (******************) | a",
          "--+-----------------+----------------+-+ |",
          "  4                 5                6+∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a leftClosed interval" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.leftClosed(5) // [5, +∞)
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "                    [******************) | a",
          "--+-----------------+----------------+-+ |",
          "  4                 5                6+∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a rightOpen interval" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.rightOpen(5) // (-∞, 5)
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "(*******************)                    | a",
          "+-+-----------------+----------------+-- |",
          "-∞4                 5                6   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a rightClosed interval" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a       = Interval.rightClosed(5) // (-∞, 5]
        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "(*******************]                    | a",
          "+-+-----------------+----------------+-- |",
          "-∞4                 5                6   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a leftClosed and rightClosed overlapping intervals" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a = Interval.leftClosed(5)   // [5, +∞)
        val b = Interval.rightClosed(10) // (-∞, 10]

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [************************************) | a",
          "(************************************]   | b",
          "+-+----------------------------------+-+ |",
          "-∞5                                 10+∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a leftClosed and rightClosed non-overlapping intervals" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a = Interval.leftClosed(10) // [10, +∞)
        val b = Interval.rightClosed(5) // (-∞, 5]

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "                                     [*) | a",
          "(*]                                      | b",
          "+-+----------------------------------+-+ |",
          "-∞5                                 10+∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display several intervals" in {
        val renderer = AsciiRenderer.make[Int](themeNoLegend)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [*********]                            | a",
          "            [************]               | b",
          "(************************************]   | c",
          "     (*********************************) | d",
          "+-+--+------+------------+-----------+-+ |",
          "-∞1  2      5           10          15+∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display several intervals with a legend" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)
        val c = Interval.rightClosed(15)
        val d = Interval.leftOpen(2)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [*********]                            | [1,5]   : a",
          "            [************]               | [5,10]  : b",
          "(************************************]   | (-∞,15] : c",
          "     (*********************************) | (2,+∞)  : d",
          "+-+--+------+------------+-----------+-+ |",
          "-∞1  2      5           10          15+∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display several leftClosed intervals" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.leftClosed(1)
        val b = Interval.leftClosed(2)
        val c = Interval.leftClosed(3)
        val d = Interval.leftClosed(4)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [************************************) | [1,+∞) : a",
          "              [************************) | [2,+∞) : b",
          "                         [*************) | [3,+∞) : c",
          "                                     [*) | [4,+∞) : d",
          "--+-----------+----------+-----------+-+ |",
          "  1           2          3           4+∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display several rightClosed intervals" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.rightClosed(1)
        val b = Interval.rightClosed(2)
        val c = Interval.rightClosed(3)
        val d = Interval.rightClosed(4)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "(*]                                      | (-∞,1] : a",
          "(*************]                          | (-∞,2] : b",
          "(************************]               | (-∞,3] : c",
          "(************************************]   | (-∞,4] : d",
          "+-+-----------+----------+-----------+-- |",
          "-∞1           2          3           4   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display overlapping intervals" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(2, 6)
        val c = Interval.closed(3, 7)
        val d = Interval.closed(4, 8)
        val e = Interval.closed(5, 9)
        val f = Interval.closed(6, 10)

        val diagram = Diagram
          .empty[Int]
          .withSection(
            _.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d").addInterval(e, "e").addInterval(f, "f")
          )

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [***************]                      | [1,5]  : a",
          "      [**************]                   | [2,6]  : b",
          "          [**************]               | [3,7]  : c",
          "              [**************]           | [4,8]  : d",
          "                  [**************]       | [5,9]  : e",
          "                     [***************]   | [6,10] : f",
          "--+---+---+---+---+--+---+---+---+---+-- |",
          "  1   2   3   4   5  6   7   8   9  10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display with default settings [doc]" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(3, 7)
        val b = Interval.closed(10, 15)
        val c = Interval.closed(12, 20)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [*******]                              | [3,7]   : a",
          "                [**********]             | [10,15] : b",
          "                     [***************]   | [12,20] : c",
          "--+-------+-----+----+-----+---------+-- |",
          "  3       7    10   12    15        20   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display overlapping intervals with overlapping labels (Theme.Label.None)" in {
        val theme = themeNoLegend.copy(labelPosition = AsciiLabelPosition.None)

        val renderer = AsciiRenderer.make[Int](theme)

        val a = Interval.closed(100, 500)
        val b = Interval.closed(150, 600)
        val c = Interval.closed(200, 700)
        val d = Interval.closed(250, 800)
        val e = Interval.closed(300, 900)
        val f = Interval.closed(600, 1000)

        val diagram = Diagram
          .empty[Int]
          .withSection(
            _.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d").addInterval(e, "e").addInterval(f, "f")
          )

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [***************]                      | a",
          "    [****************]                   | b",
          "      [******************]               | c",
          "        [********************]           | d",
          "          [**********************]       | e",
          "                     [***************]   | f",
          "--+-+-+-+-+-------+--+---+---+---+---+-- |",
          " 10152025300     500600 700 800 9001000  |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display overlapping intervals with overlapping labels (Theme.Label.NoOverlap)" in {
        val theme = themeNoLegend.copy(labelPosition = AsciiLabelPosition.NoOverlap)

        val renderer = AsciiRenderer.make[Int](theme)

        val a = Interval.closed(100, 500)
        val b = Interval.closed(150, 600)
        val c = Interval.closed(200, 700)
        val d = Interval.closed(250, 800)
        val e = Interval.closed(300, 900)
        val f = Interval.closed(600, 1000)

        val diagram = Diagram
          .empty[Int]
          .withSection(
            _.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d").addInterval(e, "e").addInterval(f, "f")
          )

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [***************]                      | a",
          "    [****************]                   | b",
          "      [******************]               | c",
          "        [********************]           | d",
          "          [**********************]       | e",
          "                     [***************]   | f",
          "--+-+-+-+-+-------+--+---+---+---+---+-- |",
          " 100 200 300     500    700 800 900      |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display overlapping intervals with overlapping labels (Theme.Label.Stacked)" in {
        val theme = themeNoLegend.copy(labelPosition = AsciiLabelPosition.Stacked)

        val renderer = AsciiRenderer.make[Int](theme)

        val a = Interval.closed(100, 500)
        val b = Interval.closed(150, 600)
        val c = Interval.closed(200, 700)
        val d = Interval.closed(250, 800)
        val e = Interval.closed(300, 900)
        val f = Interval.closed(600, 1000)

        val diagram = Diagram
          .empty[Int]
          .withSection(
            _.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d").addInterval(e, "e").addInterval(f, "f")
          )

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [***************]                      | a",
          "    [****************]                   | b",
          "      [******************]               | c",
          "        [********************]           | d",
          "          [**********************]       | e",
          "                     [***************]   | f",
          "--+-+-+-+-+-------+--+---+---+---+---+-- |",
          " 100 200 300     500    700 800 900      |",
          "   150 250          600            1000  |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display intervals with legend and annotations" in {
        val theme = themeDefault.copy(hasLegend = true, hasAnnotations = true)

        val renderer = AsciiRenderer.make[Int](theme)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [***************]                      | [1,5]  : a",
          "                  [******************]   | [5,10] : b",
          "--+---------------+------------------+-- |",
          "  1               5                 10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display intervals with legend and without annotations" in {
        val theme = themeDefault.copy(hasLegend = true, hasAnnotations = false)

        val renderer = AsciiRenderer.make[Int](theme)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [***************]                      | [1,5]",
          "                  [******************]   | [5,10]",
          "--+---------------+------------------+-- |",
          "  1               5                 10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display intervals without legend and with annotations" in {
        val theme = themeDefault.copy(hasLegend = false, hasAnnotations = true)

        val renderer = AsciiRenderer.make[Int](theme)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [***************]                      | a",
          "                  [******************]   | b",
          "--+---------------+------------------+-- |",
          "  1               5                 10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display intervals without legend and without annotations" in {
        val theme = themeDefault.copy(hasLegend = false, hasAnnotations = false)

        val renderer = AsciiRenderer.make[Int](theme)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(5, 10)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [***************]                     ",
          "                  [******************]  ",
          "--+---------------+------------------+--",
          "  1               5                 10  ",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display intervals with OffsetDateTime" in {
        given Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.DAYS)

        val theme    = themeNoLegend.copy(labelPosition = AsciiLabelPosition.Stacked)
        val renderer = AsciiRenderer.make[OffsetDateTime](theme)

        val a       = Interval.closed(OffsetDateTime.parse("2020-07-02T12:34Z"), OffsetDateTime.parse("2021-07-02T12:34Z"))
        val diagram = Diagram.empty[OffsetDateTime].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [**********************************]   | a",
          "--+----------------------------------+-- |",
          "2020-07-02T12:34Z      2021-07-02T12:34Z |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display intervals with Instant" in {
        given instantDomain: Domain[Instant] = Domain.makeInstant(ChronoUnit.DAYS)

        val theme    = themeNoLegend.copy(labelPosition = AsciiLabelPosition.Stacked)
        val renderer = AsciiRenderer.make[Instant](theme)

        val a       = Interval.closed(Instant.parse("2020-07-02T12:34:00Z"), Instant.parse("2021-07-02T12:34:00Z"))
        val diagram = Diagram.empty[Instant].withSection(_.addInterval(a, "a"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [**********************************]   | a",
          "--+----------------------------------+-- |",
          "2020-07-02T12:34:00Z                     |",
          "                    2021-07-02T12:34:00Z |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display several timelines" in {
        given Domain[LocalDate] = Domain.makeLocalDate(ChronoUnit.DAYS)

        val theme    = themeDefault.copy(labelPosition = AsciiLabelPosition.Stacked)
        val renderer = AsciiRenderer.make[LocalDate](theme)

        val a = Interval.leftClosed(LocalDate.parse("2023-01-01"))
        val b = Interval.closed(LocalDate.parse("2023-01-03"), LocalDate.parse("2023-01-15"))
        val c = Interval.empty[LocalDate]
        val d = Interval.closed(LocalDate.parse("2023-01-10"), LocalDate.parse("2023-01-20"))
        val diagram =
          Diagram.empty[LocalDate].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c").addInterval(d, "d"))

        renderer.render(diagram)

        val actual = renderer.result
        val expected = List(
          "  [************************************) | [2023-01-01,+∞)         : a",
          "      [*********************]            | [2023-01-03,2023-01-15] : b",
          "                                         | ∅                       : c",
          "                   [*****************]   | [2023-01-10,2023-01-20] : d",
          "--+---+------------+--------+--------+-+ |",
          "2023-01-01    2023-01-10      2023-01-20 |",
          " 2023-01-03            2023-01-15     +∞ |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.intersection(b)" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val c = a.intersection(b)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "                  [******************]   | [5,10] : a",
          "  [**********************]               | [1,7]  : b",
          "                  [******]               | [5,7]  : c",
          "--+---------------+------+-----------+-- |",
          "  1               5      7          10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.span(b)" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(5, 10)
        val b = Interval.closed(1, 7)

        val c = a.span(b)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "                  [******************]   | [5,10] : a",
          "  [**********************]               | [1,7]  : b",
          "  [**********************************]   | [1,10] : c",
          "--+---------------+------+-----------+-- |",
          "  1               5      7          10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.span(b) of two disjoint intervals" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(7, 10)

        val c = a.span(b)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "  [***************]                      | [1,5]  : a",
          "                         [***********]   | [7,10] : b",
          "  [**********************************]   | [1,10] : c",
          "--+---------------+------+-----------+-- |",
          "  1               5      7          10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.union(b)" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(1, 5)
        val b = Interval.closed(6, 10)

        val c = a.union(b)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "  [***************]                      | [1,5]  : a",
          "                     [***************]   | [6,10] : b",
          "  [**********************************]   | [1,10] : c",
          "--+---------------+--+---------------+-- |",
          "  1               5  6              10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.union(b) of two disjoint intervals" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(1, 4)
        val b = Interval.closed(6, 10)

        val c = a.union(b)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "  [***********]                          | [1,4]  : a",
          "                     [***************]   | [6,10] : b",
          "                                         | ∅      : c",
          "--+-----------+------+---------------+-- |",
          "  1           4      6              10   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.gap(b) of two intersecting intervals" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(5, 10)
        val b = Interval.closed(4, 15)

        val c = a.gap(b)

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "     [***************]                   | [5,10] : a",
          "  [**********************************]   | [4,15] : b",
          "                                         | ∅      : c",
          "--+--+---------------+---------------+-- |",
          "  4  5              10              15   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.gap(b) of two disjoint intervals" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(5, 10)
        val b = Interval.closed(15, 20)

        val c = a.gap(b).canonical

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "  [***********]                          | [5,10]  : a",
          "                         [***********]   | [15,20] : b",
          "                [******]                 | [11,14] : c",
          "--+-----------+-+------+-+-----------+-- |",
          "  5          10       14            20   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.minus(b) if a.overlaps(b)" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(1, 10)
        val b = Interval.closed(5, 15)

        val c = a.minus(b).canonical

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "  [**********************]               | [1,10] : a",
          "            [************************]   | [5,15] : b",
          "  [*******]                              | [1,4]  : c",
          "--+-------+-+------------+-----------+-- |",
          "  1       4 5           10          15   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display a.minus(b) if a.isOverlappedBy(b)" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(5, 15)
        val b = Interval.closed(1, 10)

        val c = a.minus(b).canonical

        val diagram = Diagram.empty[Int].withSection(_.addInterval(a, "a").addInterval(b, "b").addInterval(c, "c"))

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "            [************************]   | [5,15]  : a",
          "  [**********************]               | [1,10]  : b",
          "                           [*********]   | [11,15] : c",
          "--+---------+------------+-+---------+-- |",
          "  1         5           10          15   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display Interval.minus(a, b) if a.contains(b)" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(1, 15)
        val b = Interval.closed(5, 10)

        val cs = Interval.minus(a, b).map(_.canonical)

        val diagram = Diagram.empty[Int].withSection { s =>
          val s0 = s.addInterval(a, "a").addInterval(b, "b")
          cs.zipWithIndex.foldLeft(s0) { case (s, (c, j)) => s.addInterval(c, s"c${j}") }
        }

        renderer.render(diagram)

        val actual = renderer.result

        val expected = List(
          "  [**********************************]   | [1,15]  : a",
          "            [************]               | [5,10]  : b",
          "  [*******]                              | [1,4]   : c0",
          "                           [*********]   | [11,15] : c1",
          "--+-------+-+------------+-+---------+-- |",
          "  1       4 5           10          15   |",
        ).mkString("\n")

        actual shouldBe expected
      }

      "display short intervals" in {
        val renderer = AsciiRenderer.make[Int](themeDefault)

        val a = Interval.closed(0, 10)
        val b = Interval.closed(3, 50)
        val c = Interval.closed(20, 30)
        val d = Interval.closed(60, 70)
        val e = Interval.closed(71, 80)

        val input  = List(a, b, c, d, e)
        val splits = Interval.split(input)

        val ia = (input ++ splits).zip(List("a", "b", "c", "d", "e", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7"))
        val diagram = Diagram.empty[Int].withSection { s =>
          ia.foldLeft(s) { case (s, (i, l)) => s.addInterval(i, l) }
        }

        renderer.render(diagram)

        val actual = renderer.result

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
          "  0  10   20  30       50  60   70  80   |",
        ).mkString("\n")

        actual shouldBe expected
      }
    }
  }
