package com.github.gchudnov.mtg

import org.scalacheck.Gen

object Arbitraries:

  private val xMin = -10
  private val xMax = 10

  /**
   * Generator to make to tuple (a, b), whre a < b.
   *
   * It will allow to construct Proper intervals.
   */
  val intTupleXleY: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax - 1)
      y <- Gen.choose(x + 1, xMax)
    yield (x, y)

  /**
   * Generator to make to tuple (a, b), whre a <= b.
   *
   * It will allow to construct Proper and Degenerate intervals.
   */
  val intTupleXleqY: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax)
      y <- Gen.choose(x, xMax)
    yield (x, y)

  /**
   * Generator to make any tuple (a, b) where a and b are unordered.
   *
   * That will allow to construct Empty, Degenrate and Proper intervals.
   */
  val intTupleXanyY: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax)
      y <- Gen.choose(xMin, xMax)
    yield (x, y)
