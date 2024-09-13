package com.github.gchudnov.mtg.internal.domain

import com.github.gchudnov.mtg.Domain

/**
 * Integral Domain: Int, Long, ...
 */
private[internal] final class IntegralDomain[T: Integral] extends AnyDomain[T]:
  private val intT: Integral[T] = summon[Integral[T]]

  override def succ(x: T): T =
    intT.plus(x, intT.one)

  override def pred(x: T): T =
    intT.minus(x, intT.one)

  override def count(start: T, end: T): Long =
    intT.toLong(intT.plus(intT.minus(end, start), intT.one))

  override def compare(x: T, y: T): Int =
    intT.compare(x, y)
