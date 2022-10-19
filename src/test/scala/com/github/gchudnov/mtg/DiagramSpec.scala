package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Diagram.Canvas
import com.github.gchudnov.mtg.Diagram.Theme
import com.github.gchudnov.mtg.Diagram.Span
import com.github.gchudnov.mtg.Diagram.Tick
import com.github.gchudnov.mtg.Diagram.Label
import com.github.gchudnov.mtg.Diagram.View

final class DiagramSpec extends TestSpec:

  private val canvas: Canvas  = Canvas.small
  private val view: View[Int] = View.default
  private val theme: Theme    = Theme.default

  "Diagram" when {

    "make" should {
      "diagram a point" in {
        val a       = Interval.degenerate[Int](5) // [5]
        val diagram = Diagram.make(List(a), view, canvas)

        diagram mustBe Diagram(40, 1, List(Span(20, 20, 0, true, true)), List(Tick(20)), List(Label(20, "5")), List("{5}"))
      }
    }
  }

  /*
Diagram(40, 1, List(Span(18, 18, 0, true, true)), List(Tick(18)), List(Label(18, "5")), List("{5}")) 
was not equal to Diagram(40, 1, List(Span(2, 2, 0, true, true)), List(Tick(2)), List(Label(2, "5")), List("{5}"))
  */