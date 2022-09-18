package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain

object BoundaryOrdering:

  private final class BoundaryOrdering[T: Ordering: Domain] extends Ordering[Boundary[T]]:

    override def compare(ba: Boundary[T], bb: Boundary[T]): Int =
      val ordT = summon[Ordering[T]]

      (ba, bb) match
        case (ba1 @ LeftBoundary(_, ix), bb1 @ LeftBoundary(_, iy)) =>
          (ba1.effectiveValue, bb1.effectiveValue) match
            case (Some(x), Some(y)) =>
              ordT.compare(x, y)
            case (Some(x), None) =>
              1
            case (None, Some(y)) =>
              -1
            case (None, None) =>
              if ix == iy then 0 else if ix && !iy then -1 else 1

        case (RightBoundary(ox, ix), RightBoundary(oy, iy)) =>
          ???

        case (LeftBoundary(ox, ix), RightBoundary(oy, iy)) =>
          ???

        case (RightBoundary(ox, ix), LeftBoundary(oy, iy)) =>
          ???

  def boundaryOrdering[T: Ordering: Domain]: Ordering[Boundary[T]] =
    new BoundaryOrdering[T]
