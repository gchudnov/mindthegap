package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Domains.given
import com.github.gchudnov.mtg.Show.*
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.PropertyCheckConfiguration
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.Table
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.forAll

import java.time.Instant

final class DiagramSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb226

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(minSuccessful = 100)

  given bOrd: Ordering[Boundary[Int]] = BoundaryOrdering.boundaryOrdering[Int]

  private val canvasWidth = 40

  "Diagram" when {
    "prepare" should {
      "make spans for unbounded interval" in {
        val a = Interval.unbounded[Int]

        val diagram = Diagram.prepare(List(a), canvasWidth)

        diagram mustBe Diagram(List(Span(0, 39, 0, SpanStyle('(', '*', ')'))))
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
  }
