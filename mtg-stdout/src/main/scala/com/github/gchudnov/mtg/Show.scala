package com.github.gchudnov.mtg

object Show:
  private val infinite = '∞'

  private val empty = '∅'

  private val leftOpen    = '('
  private val leftClosed  = '['
  private val rightOpen   = ')'
  private val rightClosed = ']'

  private val leftPoint  = '{'
  private val rightPoint = '}'

  private val sep = ','

  extension [T: Domain](i: Interval[T])
    def asString: String =
      Show.print[T](i)

  def print[T: Domain](i: Interval[T]): String =
    if i.isEmpty then empty.toString()
    else if i.isPoint then
      val p = str(i.left.eval)
      s"${leftPoint}${p}${rightPoint}"
    else s"${showLeft(i.left)}${sep}${showRight(i.right)}"

  private[mtg] def leftBound(isInclude: Boolean): Char =
    if isInclude then leftClosed else leftOpen

  private[mtg] def rightBound(isInclude: Boolean): Char =
    if isInclude then rightClosed else rightOpen

  private[mtg] def str[T](x: Value[T]): String =
    x match
      case Value.InfNeg =>
        s"-${infinite}"
      case Value.InfPos =>
        s"+${infinite}"
      case Value.Finite(x) =>
        x.toString()

  private def showLeft[T: Domain](left: Endpoint[T]): String =
    val (x, isInclude) = left match
      case Endpoint.At(x) =>
        (x, !x.isInf)
      case Endpoint.Pred(_) =>
        val x = left.eval
        (x, !x.isInf)
      case Endpoint.Succ(xx) =>
        (xx.eval, false)
    s"${leftBound(isInclude)}${str(x)}"

  private def showRight[T: Domain](right: Endpoint[T]): String =
    val (y, isInclude) = right match
      case Endpoint.At(y) =>
        (y, !y.isInf)
      case Endpoint.Pred(yy) =>
        (yy.eval, false)
      case Endpoint.Succ(yy) =>
        val y = right.eval
        (y, !y.isInf)
    s"${str(y)}${rightBound(isInclude)}"