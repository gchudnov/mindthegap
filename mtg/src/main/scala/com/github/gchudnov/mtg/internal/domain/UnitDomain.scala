package com.github.gchudnov.mtg.internal.domain

import com.github.gchudnov.mtg.Domain

private[internal] final class UnitDomain extends Domain[Unit]:

  override def succ(x: Unit): Unit =
    x

  override def pred(x: Unit): Unit =
    x

  override def count(start: Unit, end: Unit): Long =
    1

  override def compare(x: Unit, y: Unit): Int =
    0
