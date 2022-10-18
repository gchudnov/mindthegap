package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
import com.github.gchudnov.mtg.Diagram.View

final class DiagramSpec extends TestSpec:

  private val canvas: Canvas = Canvas.small
  private val view: View[Int]     = View.default
  private val theme: Theme        = Theme.default

  "Diagram" when {

    "make" should {
      "diagram a point" in {
        // val a       = Interval.degenerate[Int](5) // [5]
        // val diagram = Diagram.prepare(List(a), view, canvas)

        // diagram mustBe Diagram(40, 1, List(Span(2, 2, 0, true, true)), List(Tick(2)), List(Label(2, "5")), List("{5}"))
      }
    }
  }
