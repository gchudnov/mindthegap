package com.github.gchudnov.mtg.ordering

/**
 * Option Ordering
 */
given OptionOrdering[T: Ordering]: Ordering[Option[T]] with
  override def compare(ox: Option[T], oy: Option[T]): Int =
    (ox, oy) match
      case (None, None) =>
        0
      case (None, Some(_)) =>
        -1
      case (Some(_), None) =>
        1
      case (Some(x), Some(y)) =>
        summon[Ordering[T]].compare(x, y)

/**
 * Option Partial Ordering
 */
given OptionPartialOrdering[T: Ordering]: PartialOrdering[Option[T]] with
  override def tryCompare(ox: Option[T], oy: Option[T]): Option[Int] =
    (ox, oy) match
      case (None, None) =>
        Some(0)
      case (None, Some(_)) =>
        None
      case (Some(_), None) =>
        None
      case (Some(x), Some(y)) =>
        Some(summon[Ordering[T]].compare(x, y))

  override def lteq(ox: Option[T], oy: Option[T]): Boolean =
    (ox, oy) match
      case (None, None) =>
        true
      case (None, Some(_)) =>
        false
      case (Some(_), None) =>
        false
      case (Some(x), Some(y)) =>
        summon[Ordering[T]].lteq(x, y)
