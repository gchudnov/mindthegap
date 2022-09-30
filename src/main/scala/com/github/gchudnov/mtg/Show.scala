package com.github.gchudnov.mtg

object Show:
  private val infinite = '∞'

  private val leftOpen    = '('
  private val leftClosed  = '['
  private val rightOpen   = ')'
  private val rightClosed = ']'

  private[mtg] def leftBound(isInclude: Boolean): Char =
    if isInclude then leftClosed else leftOpen

  private[mtg] def rightBound(isInclude: Boolean): Char =
    if isInclude then rightClosed else rightOpen

  private[mtg] def leftValue(x: Option[?]): String =
    x.fold(s"-${infinite}")(_.toString())

  private[mtg] def rightValue(x: Option[?]): String =
    x.fold(s"+${infinite}")(_.toString())

  extension [T](b: Boundary[T])
    def show: String =
      b match
        case LeftBoundary(value, isInclude) =>
          val lb = leftBound(isInclude)
          val lv = leftValue(value)
          s"${lb}${lv}"
        case RightBoundary(value, isInclude) =>
          val rb = rightBound(isInclude)
          val rv = rightValue(value)
          s"${rv}${rb}"

  extension [T](ab: Interval[T])
    def show: String =
      ab match
        case Empty =>
          "∅"
        case Degenerate(a) =>
          s"{${a.toString()}}"
        case Proper(left, right) =>
          s"${left.show},${right.show}"
