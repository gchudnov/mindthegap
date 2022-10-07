package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Domain

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

      case (ba1 @ RightBoundary(_, ix), bb1 @ RightBoundary(_, iy)) =>
        (ba1.effectiveValue, bb1.effectiveValue) match
          case (Some(x), Some(y)) =>
            ordT.compare(x, y)
          case (Some(x), None) =>
            -1
          case (None, Some(y)) =>
            1
          case (None, None) =>
            if ix == iy then 0 else if ix && !iy then 1 else -1

      case (ba1 @ LeftBoundary(_, _), bb1 @ RightBoundary(_, _)) =>
        (ba1.effectiveValue, bb1.effectiveValue) match
          case (Some(x), Some(y)) =>
            ordT.compare(x, y)
          case (Some(x), None) =>
            -1
          case (None, Some(y)) =>
            -1
          case (None, None) =>
            -1

      case (ba1 @ RightBoundary(_, _), bb1 @ LeftBoundary(_, _)) =>
        (ba1.effectiveValue, bb1.effectiveValue) match
          case (Some(x), Some(y)) =>
            ordT.compare(x, y)
          case (Some(x), None) =>
            1
          case (None, Some(y)) =>
            1
          case (None, None) =>
            1

object BoundaryOrdering:
  import Domains.given

  given boundaryOrdering[T: Ordering: Domain]: Ordering[Boundary[T]] =
    new BoundaryOrdering[T]

  def makeBoundaryOrdering[T: Ordering: Domain]: Ordering[Boundary[T]] =
    new BoundaryOrdering[T]
