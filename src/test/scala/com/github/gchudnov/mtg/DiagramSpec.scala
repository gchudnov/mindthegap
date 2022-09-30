package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Domains.given
import com.github.gchudnov.mtg.Show.*
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.Label
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant

final class DiagramSpec extends TestSpec:

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  private val canvasWidth: Int = 40
  private val padding: Int     = 2
  private val theme: Theme     = Theme.default

  "Diagram" when {
    "prepare" should {

      "prepare a closed interval" in {
        val a       = Interval.closed[Int](5, 10) // [5, 10]
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        diagram mustBe Diagram(40, 1, List(Span(2, 37, 0, true, true)), List(Label(2, 2, "5"), Label(37, 36, "10")))
      }

      "prepare a closed interval with a negative boundary" in {
        val a       = Interval.closed[Int](-5, 10) // [-5, 10]
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        diagram mustBe Diagram(40, 1, List(Span(2, 37, 0, true, true)), List(Label(2, 1, "-5"), Label(37, 36, "10")))
      }

      "prepare unbounded interval" in {
        val a       = Interval.unbounded[Int] // (-∞, +∞)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        diagram mustBe Diagram(width = 40, height = 1, spans = List(Span(0, 39, 0, false, false)), List(Label(0, 0, "-∞"), Label(39, 38, "+∞")))
      }

      "prepare a leftOpen interval" in {
        val a       = Interval.leftOpen(5) // (5, +∞)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        diagram mustBe Diagram(40, 1, List(Span(2, 39, 0, false, false)), List(Label(2, 2, "5"), Label(39, 38, "+∞")))
      }

      "prepare a leftClosed interval" in {
        val a       = Interval.leftClosed(5) // [5, +∞)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        diagram mustBe Diagram(40, 1, List(Span(2, 39, 0, true, false)), List(Label(2, 2, "5"), Label(39, 38, "+∞")))
      }

      "prepare a rightOpen interval" in {
        val a       = Interval.rightOpen(5) // (-∞, 5)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        diagram mustBe Diagram(40, 1, List(Span(0, 37, 0, false, false)), List(Label(0, 0, "-∞"), Label(37, 37, "5")))
      }

      "prepare a rightClosed interval" in {
        val a       = Interval.rightClosed(5) // (-∞, 5]
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        diagram mustBe Diagram(40, 1, List(Span(0, 37, 0, false, true)), List(Label(0, 0, "-∞"), Label(37, 37, "5")))
      }

      // TODO: FIX THIS

      // "make spans for several intervals" in {
      //   val a = Interval.closed(1, 5)
      //   val b = Interval.closed(5, 10)
      //   val c = Interval.rightClosed(15)
      //   val d = Interval.leftOpen(2)

      //   val data = Show.prepare(List(a, b, c, d), canvasWidth)

      //   data mustBe List("....", ".....")
      // }
    }

    "render" should {
      "display a closed interval" in {
        val a       = Interval.closed[Int](5, 10)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)
        val data    = Diagram.render(diagram, theme)

        data mustBe List(
          "  [**********************************]  ",
          "--+----------------------------------+--",
          "  5                                 10  "
        )
      }

      "display a closed interval with negative boundary" in {
        val a       = Interval.closed[Int](-5, 10)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)
        val data    = Diagram.render(diagram, theme)

        data mustBe List(
          "  [**********************************]  ",
          "--+----------------------------------+--",
          " -5                                 10  "
        )
      }

      "display an unbounded interval" in {
        val a       = Interval.unbounded[Int]
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)
        val data    = Diagram.render(diagram, theme)

        data mustBe List(
          "(**************************************)",
          "+--------------------------------------+",
          "-∞                                    +∞"
        )
      }

      "display a leftOpen interval" in {
        val a       = Interval.leftOpen(5) // (5, +∞)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        val data = Diagram.render(diagram, theme)

        data mustBe List(
          "  (************************************)",
          "--+------------------------------------+",
          "  5                                   +∞"
        )
      }

      "display a leftClosed interval" in {
        val a       = Interval.leftClosed(5) // [5, +∞)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        val data = Diagram.render(diagram, theme)

        data mustBe List(
          "  [************************************)",
          "--+------------------------------------+",
          "  5                                   +∞"
        )
      }

      "display a rightOpen interval" in {
        val a       = Interval.rightOpen(5) // (-∞, 5)
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        val data = Diagram.render(diagram, theme)

        data mustBe List(
          "(************************************)  ",
          "+------------------------------------+--",
          "-∞                                   5  "
        )
      }

      "display a rightClosed interval" in {
        val a       = Interval.rightClosed(5) // (-∞, 5]
        val diagram = Diagram.prepare(List(a), canvasWidth, padding)

        val data = Diagram.render(diagram, theme)

        data mustBe List(
          "(************************************]  ",
          "+------------------------------------+--",
          "-∞                                   5  "
        )
      }
    }

  }
