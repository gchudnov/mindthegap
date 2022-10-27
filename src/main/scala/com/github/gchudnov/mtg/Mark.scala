package com.github.gchudnov.mtg

enum Mark[T]:
  case At(value: Value[T])
  case Succ(x: Mark[T])
  case Pred(x: Mark[T])

  def isAt: Boolean =
    this.ordinal == 0

  def isSucc: Boolean =
    this.ordinal == 1

  def ispred: Boolean =
    this.ordinal == 2

  def at(using d: Domain[T]): Value[T] =
    this match
      case At(x) =>
        x
      case Succ(x) =>
        // At(x.at.value)
        // summon[Domain[U]].succ(x.at)
        ???
      case Pred(x) =>
        x.at
