package com.github.gchudnov.mtg

trait Algorithm:

  /**
   * Intersection
   */
  def intersection[T: Domain](a: Interval[T], b: Interval[T])(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    a.intersection(b)

object Algorithm extends Algorithm
