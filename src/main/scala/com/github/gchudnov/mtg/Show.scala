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

  def asString[T: Domain](i: Interval[T])(using Ordering[Mark[T]]): String =
    if i.isEmpty then empty.toString()
    else if i.isPoint then
      val p = str(i.left.eval)
      s"${leftPoint}${p}${rightPoint}"
    else
      (i.left, i.right) match
        case (Mark.At(x), Mark.At(y)) =>
          val p = str(x)
          val q = str(y)
          s"${leftClosed}${p}${sep}${q}${rightClosed}"
        case (Mark.At(x), Mark.Pred(yy)) =>
          val p = str(x)
          val q = str(yy.eval)
          s"${leftClosed}${p}${sep}${q}${rightOpen}"
        case (Mark.Succ(xx), Mark.Pred(yy)) =>
          val p = str(xx.eval)
          val q = str(yy.eval)
          s"${leftOpen}${p}${sep}${q}${rightOpen}"
        case (Mark.Succ(xx), Mark.At(y)) =>
          val p = str(xx.eval)
          val q = str(y)
          s"${leftOpen}${p}${sep}${q}${rightClosed}"
        case (xx, yy) =>
          val p = str(xx.eval)
          val q = str(yy.eval)
          s"${leftClosed}${p}${sep}${q}${rightClosed}"
