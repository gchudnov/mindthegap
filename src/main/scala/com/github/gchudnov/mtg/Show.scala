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

  def prepare[T](xs: List[Interval[T]], width: Int = 80)(using bOrd: Ordering[Boundary[T]]): List[String] =
    val ys = xs.filter(_.nonEmpty)

    if ys.isEmpty then
      List.empty[String]
    else
      val bs = ys.flatMap(it => List(it.left, it.right))
      val xMin = bs.min // might be -inf
      val xMax = bs.max // might be +inf

      val bounds = bs.filter(_.isBounded)
      val optXmin = bounds.minOption // cannot be -inf
      val optXmax = bounds.maxOption // cannot be +inf

      println((xMin, xMax))
      println((optXmin, optXmax))

    List("aaaa", "bbbb")
