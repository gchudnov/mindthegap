package com.github.gchudnov.mtg

import org.scalacheck.Gen

object Arbitraries:

  final case class IntRange(min: Int, max: Int)
  final case class IntProb(empty: Int, point: Int, proper: Int)

  val intRange10: IntRange = IntRange(min = -10, max = 10)
  val intRange5: IntRange  = IntRange(min = -5, max = 5)

  val intProb226: IntProb = IntProb(empty = 2, point = 2, proper = 6)
  val intProb127: IntProb = IntProb(empty = 1, point = 2, proper = 7)

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
  type IntArgs = ((Option[Int], Boolean), (Option[Int], Boolean))

  /**
   * Generate a tuple (a, a) where both values have the same value. | Point intervals
   */
  private def genIntTupleEq(using ir: IntRange): Gen[(Int, Int)] =
    for x <- Gen.choose(ir.min, ir.max)
    yield (x, x)

  /**
   * Generate a tuple (a, b), whre a < b. | Proper intervals
   */
  private def genIntTupleLteq(using ir: IntRange): Gen[(Int, Int)] =
    for
      x <- Gen.choose(ir.min, ir.max - 1)
      y <- Gen.choose(x + 1, ir.max)
    yield (x, y)

  /**
   * Generate a tuple (a, b), whre a <= b. | Point, Proper intervals
   */
  private def genIntTupleLt(using ir: IntRange): Gen[(Int, Int)] =
    for
      x <- Gen.choose(ir.min, ir.max)
      y <- Gen.choose(x, ir.max)
    yield (x, y)

  /**
   * Generate a tuple (b, a), where b > a | Empty
   */
  private def genIntTupleGt(using ir: IntRange): Gen[(Int, Int)] =
    for
      x <- Gen.choose(ir.min + 1, ir.max)
      y <- Gen.choose(ir.min, x - 1)
    yield (x, y)

  /**
   * Generate a tuple (a, b) where a and b are random. | Empty, Degenrate or Proper intervals
   */
  private def genIntTupleAny(using ir: IntRange): Gen[(Int, Int)] =
    for
      x <- Gen.choose(ir.min, ir.max)
      y <- Gen.choose(ir.min, ir.max)
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
  def genEmptyIntArgs(using ir: IntRange): Gen[IntArgs] =
    // [b, a] = (b, a) = [b, a) = (b, a]
    val g1 = for
      ab <- genIntTupleGt.map(toSome)
      ia <- genBoolEq
      ib <- genBoolEq
    yield ((ab._1, ia), (ab._2, ib))

    // (a, a) = [a, a) = (a, a]
    val g2 = for
      ab <- genIntTupleEq.map(toSome)
      ia <- genBoolEq
      ib <- if (ia) then Gen.const(false) else genBoolEq // if we selected '[', the second boundary cannot be ']', otherwise it will produce a point interval.
    yield ((ab._1, ia), (ab._2, ib))

    Gen.oneOf(g1, g2)

  /**
   * Generate Point Intervals
   * {{{
   *   [a, a] = {a}
   * }}}
   */
  def genPointIntArgs(using ir: IntRange): Gen[IntArgs] =
    for ab <- genIntTupleEq.map(toSome) yield ((ab._1, true), (ab._2, true))

  /**
   * Generate Proper Intervals
   *
   * Neither Empty nor Point
   * {{{
   *   {a, b}
   * }}}
   */
  def genProperIntArgs(using ir: IntRange): Gen[IntArgs] =
    for
      ab <- genIntTupleLteq
      oa <- Gen.option(Gen.const(ab._1))
      ob <- Gen.option(Gen.const(ab._2))
      ia <- genBoolEq
      ib <- genBoolEq
    yield ((oa, ia), (ob, ib))

  /**
   * Generate any interval tuple.
   */
  def genAnyIntArgs(using ir: IntRange): Gen[IntArgs] =
    for
      ab <- genIntTupleAny
      oa <- Gen.option(Gen.const(ab._1))
      ob <- Gen.option(Gen.const(ab._2))
      ia <- genBoolEq
      ib <- genBoolEq
    yield ((oa, ia), (ob, ib))

  /**
   * Generate one of the (Empty, Point, Proper) intervals
   */
  def genOneOfIntArgs(using ir: IntRange, ip: IntProb): Gen[IntArgs] =
    Gen.frequency(
      ip.empty  -> genEmptyIntArgs,
      ip.point  -> genPointIntArgs,
      ip.proper -> genProperIntArgs
    )

  /**
   * Converts a tuple of values to the tuple of some values.
   */
  private def toSome[T](t: Tuple2[T, T]): Tuple2[Option[T], Option[T]] =
    (Some(t._1), Some(t._2))
