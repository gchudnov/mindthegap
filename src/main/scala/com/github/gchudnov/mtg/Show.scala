package com.github.gchudnov.mtg

trait Show[T]:
  extension (t: T) def show: String

object Show:
  private val infinite = '∞'

  private val empty = '∅'

  private val leftOpen    = '('
  private val leftClosed  = '['
  private val rightOpen   = ')'
  private val rightClosed = ']'

  private val leftPoint  = '{'
  private val rightPoint = '}'

  private[mtg] def leftBound(isInclude: Boolean): Char =
    if isInclude then leftClosed else leftOpen

  private[mtg] def rightBound(isInclude: Boolean): Char =
    if isInclude then rightClosed else rightOpen

  private[mtg] def leftValue(x: Option[?]): String =
    x.fold(s"-${infinite}")(_.toString())

  private[mtg] def rightValue(x: Option[?]): String =
    x.fold(s"+${infinite}")(_.toString())

  given Show[Boundary[?]] with
    extension (b: Boundary[?])
      def show: String =
        if b.isLeft then s"${leftBound(b.isInclude)}${leftValue(b.value)}"
        else s"${rightValue(b.value)}${rightBound(b.isInclude)}"

  given Show[Interval[?]] with
    extension (i: Interval[?])
      def show: String =
        i match
          case Interval.Empty =>
            s"${empty}"
          case Interval.Point(a) =>
            s"${leftPoint}${a.toString()}${rightPoint}"
          case Interval.Proper(l, r) =>
            s"${l.show},${r.show}"
