package com.github.gchudnov.mtg.ordering

/**
 * Option Ordering
 *
 * {{{
 *   O(x),   O(y)
 *
 * }}}
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
