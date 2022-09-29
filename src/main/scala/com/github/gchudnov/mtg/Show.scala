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

  def prepare[T](intervals: List[Interval[T]], width: Int = 80)(using bOrd: Ordering[Boundary[T]]): List[List[Char]] =
    val xs = intervals.filter(_.nonEmpty)

    if xs.isEmpty then
      List.empty[String]
    else
      val bs = xs.flatMap(it => List(it.left, it.right))
      val (ls, rs) = bs.partition(_.isInstanceOf[LeftBoundary[?]])

      val bMin = ls.min // might be -inf
      val bMax = rs.max // might be +inf

      val fMin = ls.filter(_.isBounded).minOption // != -inf
      val fMax = rs.filter(_.isBounded).maxOption // != +inf


      
      ///////////////////
      val padding = 2
      val cxMin = 0 + padding
      val cxMax = width - padding





    List("aaaa".toList, "bbbb".toList)


  /**
    * Span to use when drawing the interval
    *
    * @param x0 starting x-coordinate
    * @param x1 ending x-coordinate
    * @param y y-coordinate
    * @param style style to use
    */
  final case class Span(x0: Int, x1: Int, y: Int, style: Char)