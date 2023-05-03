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
      case other =>
        Mark.At(other.eval)

  def eval(using Domain[T]): Value[T] =
    this match
      case At(x) =>
        x
      case Pred(x) =>
        x.eval.pred
      case Succ(x) =>
        x.eval.succ

  /**
   * Return the value without looking at the modifiers.
   */
  def innerValue(using Domain[T]): Value[T] =
    this match
      case At(x) =>
        x
      case Pred(At(x)) =>
        x
      case Succ(At(x)) =>
        x
      case Pred(x) =>
        x.innerValue
      case Succ(x) =>
        x.innerValue

object Mark:

  def at[T](x: Value[T]): Mark.At[T] =
    Mark.At(x)

  def at[T](value: T): Mark.At[T] =
    at(Value.finite(value))

  def pred[T](x: Mark[T]): Mark.Pred[T] =
    Pred(x)

  def pred[T](x: Value[T]): Mark.Pred[T] =
    pred(Mark.at(x))

  def pred[T](value: T): Mark.Pred[T] =
    pred(Value.finite(value))

  def succ[T](x: Mark[T]): Mark.Succ[T] =
    Succ(x)

  def succ[T](x: Value[T]): Mark.Succ[T] =
    succ(Mark.at(x))

  def succ[T](value: T): Mark.Succ[T] =
    succ(Value.finite(value))
