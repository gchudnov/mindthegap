package com.github.gchudnov.mtg

import org.scalacheck.Gen

object Arbitraries:

  /**
   * Parameters to contruct an integer interval `({a, b}, isIncludeA, IsIncludeB)`, where:
   *
   * {{{
   *   a   -- Option[Int]: None if 'inf', Some(x) if a finite value.
   *   b   -- Option[Int]: None if 'inf', Some(x) if a finite value.
   *   '{' -- one of the boundaries: '[' (isIncludeA == true), '(' (isIncludeA == false).
   *   '}' -- one of the boundaries: ']' (isIncludeB == true), ')' (isIncludeA == false).
   * }}}
   */
  type IntTuple = ((Option[Int], Option[Int]), Boolean, Boolean)

  private val xMin = -10
  private val xMax = 10

  /**
   * Generate a tuple (a, a) where both values have the same value. | Degenerate intervals
   */
  private val genIntTupleEq: Gen[(Int, Int)] =
    for x <- Gen.choose(xMin, xMax)
    yield (x, x)

  /**
   * Generate a tuple (a, b), whre a < b. | Proper intervals
   */
  private val genIntTupleLteq: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax - 1)
      y <- Gen.choose(x + 1, xMax)
    yield (x, y)

  /**
   * Generate a tuple (a, b), whre a <= b. | Degenerate, Proper intervals
   */
  private val genIntTupleLt: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax)
      y <- Gen.choose(x, xMax)
    yield (x, y)

  /**
   * Generate a tuple (b, a), where b > a | Empty
   */
  private val genIntTupleGt: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin + 1, xMax)
      y <- Gen.choose(xMin, x - 1)
    yield (x, y)

  /**
   * Generate a tuple (a, b) where a and b are random. | Empty, Degenrate or Proper intervals
   */
  private val genIntTupleAny: Gen[(Int, Int)] =
    for
      x <- Gen.choose(xMin, xMax)
      y <- Gen.choose(xMin, xMax)
    yield (x, y)

  /**
   * Boolean Generator that produces true 50% of the time.
   */
  private val genBoolEq: Gen[Boolean] =
    Gen.oneOf(true, false)

  /**
   * Boolean Generator that produces true 75% of the time.
   */
  private val genBool75: Gen[Boolean] =
    Gen.prob(0.75)

  /**
   * Generate Empty Intervals
   *
   * {{{
   *   [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
   * }}}
   */
  val genEmptyIntTuple: Gen[IntTuple] =
    // [b, a] = (b, a) = [b, a) = (b, a]
    val g1 = genIntTupleGt
    val g11 = for
      ab <- g1.map(toSome)
      ia <- genBoolEq
      ib <- genBoolEq
    yield (ab, ia, ib)

    // (a, a) = [a, a) = (a, a]
    val g2 = genIntTupleEq
    val g21 = for
      ab <- g1.map(toSome)
      ia <- genBoolEq
      ib <- if (ia) then Gen.const(false) else genBoolEq // if we selected '[', the second boundary cannot be ']', otherwise it will produce a degenerate interval.
    yield (ab, ia, ib)

    Gen.oneOf(g11, g21)

  /**
   * Generate Degenerate Intervals
   * {{{
   *   [a, a] = {a}
   * }}}
   */
  val genDegenerateIntTuple: Gen[IntTuple] =
    for ab <- genIntTupleEq.map(toSome) yield (ab, true, true)

  /**
   * Generate Proper Intervals
   * {{{
   *   neither Empty nor Degenerate
   * }}}
   */
  val genProperIntTuple: Gen[IntTuple] =
    ???

  /**
   * Generator to make any tuple (a?, b?) where a and b are unordered.
   *
   * That will allow to construct Empty, Degenrate and Proper intervals.
   */
  private val genOptIntTupleAny: Gen[(Option[Int], Option[Int])] =
    for
      x <- Gen.option(Gen.choose(xMin, xMax))
      y <- Gen.option(Gen.choose(xMin, xMax))
    yield (x, y)

  /**
   * Generate Tuple to build an interval
   */
  val genIntervalTuple: Gen[((Option[Int], Option[Int]), Boolean, Boolean)] =
    for
      ab <- genOptIntTupleAny
      ia <- genBoolEq
      ib <- genBoolEq
    yield (ab, ia, ib)

  private def toSome(t: Tuple2[Int, Int]): Tuple2[Option[Int], Option[Int]] =
    (Some(t._1), Some(t._2))
