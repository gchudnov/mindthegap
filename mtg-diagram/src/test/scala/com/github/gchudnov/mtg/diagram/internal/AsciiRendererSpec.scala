package com.github.gchudnov.mtg.diagram.internal

import com.github.gchudnov.mtg.diagram.Diagram.Canvas
import com.github.gchudnov.mtg.diagram.Diagram.Theme
import com.github.gchudnov.mtg.diagram.Diagram.Tick
import com.github.gchudnov.mtg.diagram.Diagram.Label
import com.github.gchudnov.mtg.diagram.internal.AsciiRenderer
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec


final class DiagramSpec extends AnyWordSpec with Matchers:

  private val canvas: Canvas = Canvas.make(40, 2)

  private val defaultTheme: Theme  = Theme.default
  private val themeNoLegend: Theme = defaultTheme.copy(legend = false)

  "AsciiRenderer" when {

    "renderer" should {

      "Theme.Label.None" should {
        val noneLabelTheme = defaultTheme.copy(label = Theme.Label.None)

        "draw an empty collection of labels" in {
          val ls = List.empty[Label]

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(noneLabelTheme, ls, canvas.width)
          val expected = List("                                        ")

          actual mustBe expected
        }

        "draw non-overlapping labels" in {
          val ls = List(Label(2, "5"), Label(36, "10"))

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(noneLabelTheme, ls, canvas.width)
          val expected = List("  5                                 10  ")

          actual mustBe expected
        }

        "draw meeting labels" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(noneLabelTheme, ls, canvas.width)
          val expected = List("-∞1  2      5           10          15+∞")

          actual mustBe expected
        }

        "draw overlapping labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"), Label(4, "400"))

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(noneLabelTheme, ls, canvas.width)
          val expected = List("1003400                                 ")

          actual mustBe expected
        }
      }

      "Theme.Label.NoOverlap" should {
        val noOverlapLabelTheme = defaultTheme.copy(label = Theme.Label.NoOverlap)

        "draw an empty collection of labels" in {
          val ls = List.empty[Label]

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(noOverlapLabelTheme, ls, canvas.width)
          val expected = List("                                        ")

          actual mustBe expected
        }

        "draw only non-overlapping labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"), Label(4, "400"))

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(noOverlapLabelTheme, ls, canvas.width)
          val expected = List("100 400                                 ")

          actual mustBe expected
        }

        "draw meeting labels if one of them is non-numeric" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(noOverlapLabelTheme, ls, canvas.width)
          val expected = List("-∞1  2      5           10          15+∞")

          actual mustBe expected
        }

        "draw only non-meeting labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"))

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(noOverlapLabelTheme, ls, canvas.width)
          val expected = List("100                                     ")

          actual mustBe expected
        }
      }

      "Theme.Label.Stacked" should {
        val stackedLabelTheme = defaultTheme.copy(label = Theme.Label.Stacked)

        "draw an empty collection of labels" in {
          val ls = List.empty[Label]

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(stackedLabelTheme, ls, canvas.width)
          val expected = List("                                        ")

          actual mustBe expected
        }

        "draw non-overlapping and non-meeting labels on one line" in {
          val ls = List(Label(2, "5"), Label(36, "10"))

          val r = new AsciiRenderer()

          val actual   = r.drawLabels(stackedLabelTheme, ls, canvas.width)
          val expected = List("  5                                 10  ")

          actual mustBe expected
        }

        "draw meeting labels" in {
          val ls = List(Label(2, "1"), Label(12, "5"), Label(24, "10"), Label(0, "-∞"), Label(36, "15"), Label(5, "2"), Label(38, "+∞"))

          val r = new AsciiRenderer()

          val actual = r.drawLabels(stackedLabelTheme, ls, canvas.width)
          val expected = List(
            "-∞   2      5           10          15  ",
            "  1                                   +∞",
          )

          actual mustBe expected
        }

        "draw overlapping labels" in {
          val ls = List(Label(0, "100"), Label(3, "300"), Label(4, "400"))

          val r = new AsciiRenderer()

          val actual = r.drawLabels(stackedLabelTheme, ls, canvas.width)
          val expected = List(
            "100 400                                 ",
            "   300                                  ",
          )

          actual mustBe expected
        }
      }

      "draw an empty collection of ticks" in {
        val ts = List.empty[Tick]

        val r = new AsciiRenderer()

        val actual   = r.drawTicks(themeNoLegend, ts, canvas.width)
        val expected = List("----------------------------------------")

        actual mustBe expected
      }

      "draw ticks" in {
        val ts = List(Tick(2), Tick(12), Tick(25), Tick(0), Tick(37), Tick(5), Tick(39))

        val r = new AsciiRenderer()

        val actual   = r.drawTicks(themeNoLegend, ts, canvas.width)
        val expected = List("+-+--+------+------------+-----------+-+")

        actual mustBe expected
      }

      "pad with empty lines an empty array and N = 0" in {
        val as = List.empty[String]
        val n  = 0

        val r = new AsciiRenderer()

        val actual   = r.padWithEmptyLines(n)(as)
        val expected = List.empty[String]

        actual mustBe expected
      }

      "pad with empty lines an empty array and N = 1" in {
        val as = List.empty[String]
        val n  = 1

        val r = new AsciiRenderer()

        val actual   = r.padWithEmptyLines(n)(as)
        val expected = List("")

        actual mustBe expected
      }

      "pad with empty lines a non-empty array and N > array.size" in {
        val as = List("a", "b")
        val n  = 3

        val r = new AsciiRenderer()

        val actual   = r.padWithEmptyLines(n)(as)
        val expected = List("a", "b", "")

        actual mustBe expected
      }

      "pad with empty lines a non-empty array and N == array.size" in {
        val as = List("a", "b")
        val n  = 2

        val r = new AsciiRenderer()

        val actual   = r.padWithEmptyLines(n)(as)
        val expected = List("a", "b")

        actual mustBe expected
      }

      "pad with empty lines a non-empty array and N < array.size" in {
        val as = List("a", "b")
        val n  = 1

        val r = new AsciiRenderer()

        val actual   = r.padWithEmptyLines(n)(as)
        val expected = List("a", "b")

        actual mustBe expected
      }
    }
  }
