package com.github.gchudnov.mtg.internal.domain

import com.github.gchudnov.mtg.Domain

/**
 * Fractional Domain: Double, Float with step size
 */
private[internal] final class FractionalDomain[T: Fractional](unit: T) extends Domain[T]:
  val fracT = summon[Fractional[T]]

  override def succ(x: T): T =
    fracT.plus(x, unit)

  override def pred(x: T): T =
    fracT.minus(x, unit)

  override def count(start: T, end: T): Long =
    fracT.toLong(fracT.div(fracT.plus(fracT.minus(end, start), unit), unit))

  override def compare(x: T, y: T): Int =
    fracT.compare(x, y)
