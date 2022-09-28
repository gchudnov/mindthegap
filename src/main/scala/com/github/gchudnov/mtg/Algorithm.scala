package com.github.gchudnov.mtg

trait Algorithm:

  /**
    * Find the Min Boundary
    */
  final def minBoundary[T](xs: Interval[T])(using bOrd: Ordering[Boundary[T]]): Boundary[T] = 
    ???

  /**
   * Intersection
   */
  def intersection[T: Domain](a: Interval[T], b: Interval[T])(using bOrd: Ordering[Boundary[T]]): Interval[T] =
    a.intersection(b)

    
object Algorithm extends Algorithm
