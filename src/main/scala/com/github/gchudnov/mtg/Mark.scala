package com.github.gchudnov.mtg

enum Mark[T]:
  case At(value: Value[T])
  case Pred(x: Mark[T])
  case Succ(x: Mark[T])

  def isAt: Boolean =
    this.ordinal == 0

  def isPred: Boolean =
    this.ordinal == 1

  def isSucc: Boolean =
    this.ordinal == 2

  def pred: Mark[T] =
    this match
      case x @ At(_) =>
        Pred(x)
      case x @ Pred(_) =>
        Pred(x)
      case Succ(x) =>
        x

  def succ: Mark[T] =
    this match
      case x @ At(_) =>
        Succ(x)
      case Pred(x) =>
        x
      case x @ Succ(_) =>
        Succ(x)

  def at(using Domain[T]): Mark.At[T] =
    this match
      case x @ At(_) =>
        x
      case Pred(x) =>
        Mark.At(x.eval)
      case Succ(x) =>
        Mark.At(x.eval)

  def eval(using Domain[T]): Value[T] =
    this match
      case At(x) =>
        x
      case Pred(x) =>
        x.eval.pred
      case Succ(x) =>
        x.eval.succ

object Mark:

  given markOrdering[T: Domain](using ordT: Ordering[Value[T]]): Ordering[Mark[T]] =
    new MarkOrdering[T]

  def at[T](x: Value[T]): Mark.At[T] =
    Mark.At(x)

  def at[T](value: T): Mark.At[T] =
    at(Value.Finite(value))

  def pred[T](x: Mark[T]): Mark.Pred[T] =
    Pred(x)

  def pred[T](x: Value[T]): Mark.Pred[T] =
    pred(Mark.At(x))

  def pred[T](value: T): Mark.Pred[T] =
    pred(Value.Finite(value))

  def succ[T](x: Mark[T]): Mark.Succ[T] =
    Succ(x)

  def succ[T](x: Value[T]): Mark.Succ[T] =
    succ(Mark.At(x))

  def succ[T](value: T): Mark.Succ[T] =
    succ(Value.Finite(value))
