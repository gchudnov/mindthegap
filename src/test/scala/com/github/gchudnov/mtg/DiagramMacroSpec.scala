package com.github.gchudnov.mtg

final class DiagramMacroSpec extends TestSpec:

  "DiagramMacro" when {
    "DiagramMacro.names" should {
      "return a list of variable names" in {
        val a = 10
        val b = 20
        val c = 30

        val xs = List(a, b, c)

        val actual   = DiagramMacro.names(a, b, c)
        val expected = List("x")

        actual must contain theSameElementsAs (expected)
      }
    }
  }
