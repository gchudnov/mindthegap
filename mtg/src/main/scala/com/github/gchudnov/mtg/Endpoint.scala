package com.github.gchudnov.mtg

/**
 * Interval's endpoint.
 */
enum Endpoint[T]:
  case At(value: Value[T])
  case Pred(x: Endpoint[T])
  case Succ(x: Endpoint[T])

  def isAt: Boolean =
    this.ordinal == 0

  def isPred: Boolean =
    this.ordinal == 1

  def isSucc: Boolean =
    this.ordinal == 2

  def pred: Endpoint[T] =
    this match
      case x @ At(_) =>
        Pred(x)
      case x @ Pred(_) =>
        Pred(x)
      case Succ(x) =>
        x

  def succ: Endpoint[T] =
    this match
      case x @ At(_) =>
        Succ(x)
      case Pred(x) =>
        x
      case x @ Succ(_) =>
        Succ(x)

  def at(using Domain[T]): Endpoint.At[T] =
    this match
      case x @ At(_) =>
        x
      case other =>
        Endpoint.At(other.eval)

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
   * 
   * TODO: most likely remove -- check dependencies in diagraming
   */
  private[mtg] def innerValue(using Domain[T]): Value[T] =
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

object Endpoint:

  def at[T](x: Value[T]): Endpoint.At[T] =
    Endpoint.At(x)

  def at[T](value: T): Endpoint.At[T] =
    at(Value.finite(value))

  def pred[T](x: Endpoint[T]): Endpoint.Pred[T] =
    Pred(x)

  def pred[T](x: Value[T]): Endpoint.Pred[T] =
    pred(Endpoint.at(x))

  def pred[T](value: T): Endpoint.Pred[T] =
    pred(Value.finite(value))

  def succ[T](x: Endpoint[T]): Endpoint.Succ[T] =
    Succ(x)

  def succ[T](x: Value[T]): Endpoint.Succ[T] =
    succ(Endpoint.at(x))

  def succ[T](value: T): Endpoint.Succ[T] =
    succ(Value.finite(value))
