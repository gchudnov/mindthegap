package com.github.gchudnov.mtg

object Show:
  private val infinite = "∞"

  private val leftOpen    = "("
  private val leftClosed  = "["
  private val rightOpen   = ")"
  private val rightClosed = "]"

  private def leftBound(isInclude: Boolean): String =
    if isInclude then leftClosed else leftOpen

  private def rightBound(isInclude: Boolean): String =
    if isInclude then rightClosed else rightOpen

  private def leftValue(x: Option[?]): String =
    x.fold(s"-${infinite}")(_.toString())

  private def rightValue(x: Option[?]): String =
    x.fold(s"+${infinite}")(_.toString())

  extension [T: Ordering](a: Interval[T])
    def show: String =
      a match
        case Empty =>
          "∅"
        case Degenerate(a) =>
          s"{${a.toString()}}"
        case Proper(ox1, ox2, includeX1, includeX2) =>
          val lb = leftBound(includeX1)
          val rb = rightBound(includeX2)
          val lv = leftValue(ox1)
          val rv = rightValue(ox2)
          s"${lb}${lv},${rv}${rb}"
