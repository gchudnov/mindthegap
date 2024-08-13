package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Interval
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class DiagramMacroSpec extends AnyWordSpec with Matchers:

  "DiagramMacro" when {
    "names are extracted from a passed list of variables" should {
      "return a list names" in {
        val a = 10
        val b = 20
        val c = 30

        val actual   = com.github.gchudnov.mtg.diagram.internal.DiagramMacro.varNames(List(a, b, c))
        val expected = List("a", "b", "c")

        actual should contain theSameElementsAs (expected)
      }
    }

    "names are extracted from a passed list of interval variables" should {
      "return a list names" in {
        val a = Interval.closed(3, 7)
        val b = Interval.closed(10, 15)
        val c = Interval.closed(12, 20)

        val actual   = com.github.gchudnov.mtg.diagram.internal.DiagramMacro.varNames(List(a, b, c))
        val expected = List("a", "b", "c")

        actual should contain theSameElementsAs (expected)
      }
    }

    "names are extracted from a passed list of constants" should {
      "return an empty list" in {
        val actual   = com.github.gchudnov.mtg.diagram.internal.DiagramMacro.varNames(List(10, 20, 30))
        val expected = List.empty[String]

        actual should contain theSameElementsAs (expected)
      }
    }

    "names are extracted from a passed variable" should {
      "return an empty list" in {
        val xs       = List(10, 20, 30)
        val actual   = com.github.gchudnov.mtg.diagram.internal.DiagramMacro.varNames(xs)
        val expected = List.empty[String]

        actual should contain theSameElementsAs (expected)
      }
    }
  }
