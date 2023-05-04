package com.github.gchudnov.mtg

enum Value[+T]:
  case Finite(x: T)
  case InfNeg extends Value[Nothing]
  case InfPos extends Value[Nothing]

  def isFinite: Boolean =
    this.ordinal == 0

  def isInfNeg: Boolean =
    this.ordinal == 1

  def isInfPos: Boolean =
    this.ordinal == 2

  def isInf: Boolean =
    isInfNeg || isInfPos

  def get: T =
    this match
      case Value.Finite(x) =>
        x
      case Value.InfNeg =>
        throw new NoSuchElementException("InfNeg.get")
      case Value.InfPos =>
        throw new NoSuchElementException("InfPos.get")

  def succ[U >: T: Domain]: Value[U] =
    this match
      case Value.Finite(x) =>
        Value.Finite(summon[Domain[U]].succ(x))
      case x @ Value.InfNeg =>
        x
      case x @ Value.InfPos =>
        x

  def pred[U >: T: Domain]: Value[U] =
    this match
      case Value.Finite(x) =>
        Value.Finite(summon[Domain[U]].pred(x))
      case x @ Value.InfNeg =>
        x
      case x @ Value.InfPos =>
        x

object Value:

  // given valueOrdering[T: Ordering]: Ordering[Value[T]] =
  //   new ValueOrdering[T]

  def finite[T](value: T): Value[T] =
    Value.Finite(value)

  val infNeg: Value[Nothing] =
    Value.InfNeg

  val infPos: Value[Nothing] =
    Value.InfPos
