package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Domains.given
import com.github.gchudnov.mtg.Show.*
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.Label
import com.github.gchudnov.mtg.Diagram.SpanStyle
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant

final class DiagramSpec extends TestSpec:

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  private val canvasWidth: Int = 40
  private val theme: Theme     = Theme.default

  "Diagram" when {
    "prepare" should {
      "make spans for unbounded interval" in {
        val a = Interval.unbounded[Int]

        val diagram = Diagram.prepare(List(a), canvasWidth, theme)

        diagram mustBe Diagram(width = 40, height = 1, spans = List(Span(0, 39, 0, SpanStyle('(', '*', ')'))), List(Label(0, 0, "-∞"), Label(39, 38, "+∞")))
      }

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
      "display an unbounded interval" in {
        val a       = Interval.unbounded[Int]
        val diagram = Diagram.prepare(List(a), canvasWidth, theme)
        val data    = Diagram.render(diagram)

        data mustBe List("...")
      }
    }
  }
