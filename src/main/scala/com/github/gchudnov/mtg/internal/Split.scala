package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Mark
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.Domain

private[mtg] object Split:

  /**
   * A single split
   */
  private[mtg] final case class SplitState[T: Domain](interval: Interval[T], members: Set[Int])

  /**
   * Split
   *
   * Split intervals into a collection of non-overlapping intervals (splits).
   */
  final def split[T: Domain](xs: Seq[Interval[T]])(using ordM: Ordering[Mark[T]]): List[Interval[T]] =
    ???

  /**
   * SplitFind
   *
   * Split intervals into a collection of non-overlapping intervals (splits) and provide membership information.
   */
  final def splitFind[T: Domain](xs: Seq[Interval[T]])(using ordM: Ordering[Mark[T]]): List[SplitState[T]] =
    ???
