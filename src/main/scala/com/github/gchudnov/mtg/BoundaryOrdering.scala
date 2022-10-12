package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.Domain

final class BoundaryOrdering[T: Ordering: Domain] extends Ordering[Boundary[T]]:

  override def compare(ba: Boundary[T], bb: Boundary[T]): Int =
    (ba.isLeft, bb.isLeft) match
      // Left, Left
      case (true, true) =>
        (ba.effectiveValue, bb.effectiveValue) match
          case (Some(x), Some(y)) =>
            summon[Ordering[T]].compare(x, y)
          case (Some(x), None) =>
            1
          case (None, Some(y)) =>
            -1
          case (None, None) =>
            if ba.isInclude == bb.isInclude then 0 else if ba.isInclude && !bb.isInclude then -1 else 1

      // Right, Right
      case (false, false) =>
        (ba.effectiveValue, bb.effectiveValue) match
          case (Some(x), Some(y)) =>
            summon[Ordering[T]].compare(x, y)
          case (Some(x), None) =>
            -1
          case (None, Some(y)) =>
            1
          case (None, None) =>
            if ba.isInclude == bb.isInclude then 0 else if ba.isInclude && !bb.isInclude then 1 else -1

      // Left, Right
      case (true, false) =>
        (ba.effectiveValue, bb.effectiveValue) match
          case (Some(x), Some(y)) =>
            summon[Ordering[T]].compare(x, y)
          case (Some(x), None) =>
            -1
          case (None, Some(y)) =>
            -1
          case (None, None) =>
            -1

      // Right, Left
      case (false, true) =>
        (ba.effectiveValue, bb.effectiveValue) match
          case (Some(x), Some(y)) =>
            summon[Ordering[T]].compare(x, y)
          case (Some(x), None) =>
            1
          case (None, Some(y)) =>
            1
          case (None, None) =>
            1
