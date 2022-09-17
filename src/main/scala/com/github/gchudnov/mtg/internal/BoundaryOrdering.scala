package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Value

object BoundaryOrdering:

  // private def optionOrdering[T: Ordering]: Ordering[Option[T]] = new Ordering[Option[T]]:
  //   override def compare(ox: Option[T], oy: Option[T]): Int =
  //     (ox, oy) match
  //       case (None, None) =>
  //         0
  //       case (None, Some(_)) =>
  //         -1
  //       case (Some(_), None) =>
  //         1
  //       case (Some(x), Some(y)) =>
  //         summon[Ordering[T]].compare(x, y)

  private final class BoundaryOrdering[T: Ordering: Value] extends Ordering[Boundary[T]]:

    override def compare(ba: Boundary[T], bb: Boundary[T]): Int =
      val ordT = summon[Ordering[T]]
      val valT = summon[Value[T]]

      // TODO: finish it

      // ordT.lt()

      ???


      // (ba, bb) match
      //   case (LeftBoundary(ox, ix), LeftBoundary(oy, iy)) =>
      //     (ox, oy) match {
      //       case (Some(x), Some(y)) =>
      //         val r = ordT.compare(x, y)
      //         // (ix, iy) match {
      //         //   case (true, false) =>
      //         // }
      //         ???
      //       case (Some(x), None) =>
      //         ???
      //       case (None, Some(y)) =>
      //         ???
      //       case (None, None) =>
      //         ???
      //     }

      //   case (RightBoundary(ox, ix), RightBoundary(oy, iy)) =>
      //     ???

      //   case (LeftBoundary(ox, ix), RightBoundary(oy, iy)) =>
      //     ???

      //   case (RightBoundary(ox, ix), LeftBoundary(oy, iy)) =>
      //     ???


  /**
    * compare:

    * The result sign has the following meaning:
    * 
    * negative if x < y
    * positive if x > y
    * zero otherwise (if x == y)
    *
    * @return
    */