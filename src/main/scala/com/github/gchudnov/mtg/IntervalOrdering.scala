package com.github.gchudnov.mtg

/**
 * Intervals are ordered:
 *
 * {{{
 *   - if a- < b+ then a < b
 *     if a- == b+ then
 *       - if a+ < b+ then a < b
 *         if a+ == b+ then a == b
 *         else a > b
 *     else a > b
 * }}}
 */
final class IntervalOrdering[T: Domain] extends Ordering[Interval[T]]:

  override def compare(a: Interval[T], b: Interval[T]): Int =
    val ordM = summon[Domain[T]].ordMark
    ordM.compare(a.left, b.left) match
      case -1 =>
        -1
      case 0 =>
        ordM.compare(a.right, b.right)
      case _: Int =>
        1
