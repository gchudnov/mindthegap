package com.github.gchudnov.mtg

enum Value[+T]:
  case Finite(x: T)
  case InfPos extends Value[Nothing]
  case InfNeg extends Value[Nothing]

  def isFinite: Boolean =
    this.ordinal == 0

  def isInfPos: Boolean =
    this.ordinal == 1

  def isInfNeg: Boolean =
    this.ordinal == 2

  def succ[U >: T: Domain]: Value[U] =
    this match
      case Value.Finite(x) =>
        Value.Finite(summon[Domain[U]].succ(x))
      case x @ Value.InfPos =>
        x
      case x @ Value.InfNeg =>
        x

  def pred[U >: T: Domain]: Value[U] =
    this match
      case Value.Finite(x) =>
        Value.Finite(summon[Domain[U]].pred(x))
      case x @ Value.InfPos =>
        x
      case x @ Value.InfNeg =>
        x

object Value:

  given valueOrdering[T: Ordering]: Ordering[Value[T]] =
    new ValueOrdering[T]
