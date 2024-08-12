package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.internal.Endpoint
import com.github.gchudnov.mtg.internal.Value
import org.scalacheck.Gen

object Arbitraries:

  final case class IntRange(min: Int, max: Int)

  /**
   * Probabilities & Frequencies
   *
   * @param empty
   *   frequency of an empty interval: (empty / (empty + point + proper))
   * @param point
   *   frequency of an point interval: (point / (empty + point + proper))
   * @param proper
   *   frequency of an proper interval: (proper / (empty + point + proper))
   */
  final case class IntProb(empty: Int, point: Int, proper: Int)

  val intRange100: IntRange = IntRange(min = -100, max = 100)
  val intRange10: IntRange  = IntRange(min = -10, max = 10)
  val intRange5: IntRange   = IntRange(min = -5, max = 5)

  val intProb226: IntProb = IntProb(empty = 2, point = 2, proper = 6)
  val intProb127: IntProb = IntProb(empty = 1, point = 2, proper = 7)
  val intProb028: IntProb = IntProb(empty = 0, point = 2, proper = 8)

  /**
   * Arguments to construct an integer interval
   */
  case class IntArgs(left: Endpoint[Int], right: Endpoint[Int])

  /**
   * Generate a tuple (a, a) where both values have the same value. | Point
   */
  private def genIntTupleEq(using ir: IntRange): Gen[(Int, Int)] =
    for x <- Gen.choose(ir.min, ir.max)
    yield (x, x)

  /**
   * Generate a tuple (a, b), where a < b. | Proper
   */
  private def genIntTupleLt(using ir: IntRange): Gen[(Int, Int)] =
    for
      x <- Gen.choose(ir.min, ir.max - 1)
      y <- Gen.choose(x + 1, ir.max)
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
   * Generate a tuple (a, b) where a and b are random. | Empty, Degenerate or Proper
   */
  private def genIntTupleAny(using ir: IntRange): Gen[(Int, Int)] =
    for
      x <- Gen.choose(ir.min, ir.max)
      y <- Gen.choose(ir.min, ir.max)
    yield (x, y)

  /**
   * Boolean Generator that produces 'true' 50% of the time.
   */
  private val genBool50: Gen[Boolean] =
    Gen.oneOf(true, false)

  /**
   * Generate Empty Intervals
   *
   * {{{
   *   [b, a] = (b, a) = [b, a) = (b, a] = (a, a) = [a, a) = (a, a] = {} = ∅
   *
   *   where b > a
   * }}}
   */
  def genEmptyIntArgs(using ir: IntRange): Gen[IntArgs] =
    // [b, a] = (b, a) = [b, a) = (b, a]
    val g1 = for
      ab <- genIntTupleGt.map(toSome)
      ia <- genBool50
      ib <- genBool50
    yield IntArgs(toLeft(ab._1, ia), toRight(ab._2, ib))

    // (a, a) = [a, a) = (a, a]
    val g2 = for
      ab <- genIntTupleEq.map(toSome)
      ia <- genBool50
      ib <- if ia then Gen.const(false)
            else genBool50 // if we selected '[', the second boundary cannot be ']', otherwise it will produce a point interval.
    yield IntArgs(toLeft(ab._1, ia), toRight(ab._2, ib))

    Gen.oneOf(g1, g2)

  /**
   * Generate Point Intervals
   * {{{
   *   [a, a] = {a}
   * }}}
   */
  def genPointIntArgs(using ir: IntRange): Gen[IntArgs] =
    for ab <- genIntTupleEq.map(toSome) yield IntArgs(toLeft(ab._1, true), toRight(ab._2, true))

  /**
   * Generate Proper Intervals
   *
   * Neither Empty nor Point
   * {{{
   *   {a, b}
   *
   *   where a < b
   *
   *   NOTE:
   *   (A) if d(b-a) == 1, both boundaries can be only inclusive, otherwise we will get a negative (empty) interval or a point:
   *     [1, 2] ok | proper
   *     (1, 2) x  | empty
   *     (1, 2] x  | point
   *     [1, 2) x  | point
   *
   *   (B) if d(b-a) == 2, then if first boundary is (, the second cannot be ), otherwise it will be a point
   *     [1, 3] ok | proper
   *     (1, 3) x  | point
   *     (1, 3] ok | proper
   *     [1, 3) ok | proper
   * }}}
   */
  def genProperIntArgs(using ir: IntRange): Gen[IntArgs] =
    for
      ab <- genIntTupleLt
      oa <- Gen.option(Gen.const(ab._1))
      ob <- Gen.option(Gen.const(ab._2))
      d   = ab._2 - ab._1
      ia <- if d == 1 then Gen.const(true) else genBool50
      ib <- if d == 1 then Gen.const(true) else if (d == 2) && !ia then Gen.const(true) else genBool50
    yield IntArgs(toLeft(oa, ia), toRight(ob, ib))

  /**
   * Generate non-empty intervals (Point or Proper)
   */
  def genNonEmptyIntArgs(using ir: IntRange, ip: IntProb): Gen[IntArgs] =
    Gen.frequency(
      ip.point  -> genPointIntArgs,
      ip.proper -> genProperIntArgs,
    )

  /**
   * Generate one of the (Empty, Point, Proper) intervals
   */
  def genAnyIntArgs(using ir: IntRange, ip: IntProb): Gen[IntArgs] =
    Gen.frequency(
      ip.empty  -> genEmptyIntArgs,
      ip.point  -> genPointIntArgs,
      ip.proper -> genProperIntArgs,
    )

  /**
   * Generate any interval tuple.
   */
  def genRandomIntArgs(using ir: IntRange): Gen[IntArgs] =
    for
      ab <- genIntTupleAny
      oa <- Gen.option(Gen.const(ab._1))
      ob <- Gen.option(Gen.const(ab._2))
      ia <- genBool50
      ib <- genBool50
    yield IntArgs(toLeft(oa, ia), toRight(ob, ib))

  /**
   * Converts a tuple of values to the tuple of some values.
   */
  private def toSome[T](t: Tuple2[T, T]): Tuple2[Option[T], Option[T]] =
    (Some(t._1), Some(t._2))

  private def toLeft[T](value: Option[T], isInclude: Boolean): Endpoint[T] =
    (value, isInclude) match
      case (Some(x), true) =>
        Endpoint.at(Value.finite(x))
      case (Some(x), false) =>
        Endpoint.succ(Value.finite(x))
      case (None, true) =>
        Endpoint.at(Value.infNeg)
      case (None, false) =>
        Endpoint.succ(Value.infNeg)

  private def toRight[T](value: Option[T], isInclude: Boolean): Endpoint[T] =
    (value, isInclude) match
      case (Some(x), true) =>
        Endpoint.at(Value.finite(x))
      case (Some(x), false) =>
        Endpoint.pred(Value.finite(x))
      case (None, true) =>
        Endpoint.at(Value.infPos)
      case (None, false) =>
        Endpoint.pred(Value.infPos)
