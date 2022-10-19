package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
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
        val expected = Diagram(40, 1, List(Span(20, 20, 0, true, true)), List(Tick(20)), List(Label(20, "5")), List("{5}"))

        actual mustBe expected
      }

      "diagram two points" in {
        val a = Interval.degenerate[Int](5)  // [5]
        val b = Interval.degenerate[Int](10) // [10]

        val actual = Diagram.make(List(a, b), view, canvas)
        val expected =
          Diagram(40, 2, List(Span(2, 2, 0, true, true), Span(37, 37, 1, true, true)), List(Tick(2), Tick(37)), List(Label(2, "5"), Label(36, "10")), List("{5}", "{10}"))

        actual mustBe expected
      }

      "diagram a closed interval" in {
        val a = Interval.closed[Int](5, 10) // [5, 10]

        val actual   = Diagram.make(List(a), view, canvas)
        val expected = Diagram(40, 1, List(Span(2, 37, 0, true, true)), List(Tick(2), Tick(37)), List(Label(2, "5"), Label(36, "10")), List("[5,10]"))

        actual mustBe expected
      }
    }
  }
