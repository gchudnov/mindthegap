package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.Interval

private[mtg] object Printer:
  private val infinite = '∞'

  private val empty = '∅'

  private val leftOpen    = '('
  private val leftClosed  = '['
  private val rightOpen   = ')'
  private val rightClosed = ']'

  private val leftBrace  = '{'
  private val rightBrace = '}'

  private val sep = ','

  def print[T: Domain](i: Interval[T]): String =
    if i.isEmpty then empty.toString()
    else if i.isPoint then printPoint(i.leftEndpoint)
    else printInterval(i.leftEndpoint, i.rightEndpoint)

  private[mtg] def printLeft[T: Domain](left: Endpoint[T]): String =
    val (x, isInclude) = left match
      case Endpoint.At(x) =>
        (x, !x.isInf)
      case Endpoint.Pred(_) =>
        val x = left.eval
        (x, !x.isInf)
      case Endpoint.Succ(xx) =>
        (xx.eval, false)
    s"${leftBound(isInclude)}${str(x)}"

  private[mtg] def printRight[T: Domain](right: Endpoint[T]): String =
    val (y, isInclude) = right match
      case Endpoint.At(y) =>
        (y, !y.isInf)
      case Endpoint.Pred(yy) =>
        (yy.eval, false)
      case Endpoint.Succ(yy) =>
        val y = right.eval
        (y, !y.isInf)
    s"${str(y)}${rightBound(isInclude)}"

  private[mtg] def printInterval[T: Domain](le: Endpoint[T], re: Endpoint[T]): String =
    val lhs = printLeft(le)
    val rhs = printRight(re)
    s"${lhs}${sep}${rhs}"

  private[mtg] def printPoint[T: Domain](x: Endpoint[T]): String =
    val p = str(x.eval)
    s"${leftBrace}${p}${rightBrace}"

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
