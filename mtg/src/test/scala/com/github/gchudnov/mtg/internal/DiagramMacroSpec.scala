package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.Interval

final class DiagramMacroSpec extends TestSpec:

  "DiagramMacro" when {
    "names are extracted from a passed list of variables" should {
      "return a list names" in {
        val a = 10
        val b = 20
        val c = 30

        val actual   = DiagramMacro.varNames(List(a, b, c))
        val expected = List("a", "b", "c")

        actual must contain theSameElementsAs (expected)
      }
    }

    "names are extracted from a passed list of interval variables" should {
      "return a list names" in {
        val a = Interval.closed(3, 7)
        val b = Interval.closed(10, 15)
        val c = Interval.closed(12, 20)

        val actual   = DiagramMacro.varNames(List(a, b, c))
        val expected = List("a", "b", "c")

        actual must contain theSameElementsAs (expected)
      }
    }

    "names are extracted from a passed list of constants" should {
      "return an empty list" in {
        val actual   = DiagramMacro.varNames(List(10, 20, 30))
        val expected = List.empty[String]

        actual must contain theSameElementsAs (expected)
      }
    }

    "names are extracted from a passed variable" should {
      "return an empty list" in {
        val xs       = List(10, 20, 30)
        val actual   = DiagramMacro.varNames(xs)
        val expected = List.empty[String]

        actual must contain theSameElementsAs (expected)
      }
    }
  }
