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
  val genIntTupleLteq: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax - 1)
      y <- Gen.choose(x + 1, xMax)
    yield (x, y)

  /**
   * Generator to make to tuple (a, b), whre a <= b.
   *
   * It will allow to construct Proper and Degenerate intervals.
   */
  val genIntTupleLt: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax)
      y <- Gen.choose(x, xMax)
    yield (x, y)

  /**
   * Generator to make any tuple (a, b) where a and b are unordered.
   *
   * That will allow to construct Empty, Degenrate and Proper intervals.
   */
  val genIntTupleAny: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax)
      y <- Gen.choose(xMin, xMax)
    yield (x, y)

  /**
   * Generator to make any tuple (a?, b?) where a and b are unordered.
   *
   * That will allow to construct Empty, Degenrate and Proper intervals.
   */
  val genOptIntTupleAny: Gen[(Option[Int], Option[Int])] =
    for
      x <- Gen.option(Gen.choose(xMin, xMax))
      y <- Gen.option(Gen.choose(xMin, xMax))
    yield (x, y)

  /**
   * Boolean Generator that produces true 50% of the time.
   */
  val genBoolEq: Gen[Boolean] = Gen.oneOf(true, false)

  /**
   * Boolean Generator that produces true 75% of the time.
   */
  val genBool75 = Gen.prob(0.75)

  /**
    * Generate Tuple to build an interval
    */
  val genIntervalTuple: Gen[((Option[Int], Option[Int]), Boolean, Boolean)] =
    for
      ab <- genOptIntTupleAny
      ia <- genBoolEq
      ib <- genBoolEq
    yield (ab, ia, ib)
