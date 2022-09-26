package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Domain
import com.github.gchudnov.mtg.BoundaryOrdering
import com.github.gchudnov.mtg.Interval

trait IntervalRel[+T: Ordering: Domain] { self: Interval[T] =>

  lazy val x = {
    println("CALL")
    1
  }

  val z = x

  // TODO: try to impl lazy val

  // def myfunc(b: Interval[T])(using bOrd: Ordering[Boundary[T]]): Boolean =  
  //   ???
}

/*
TODO: how to call once?
*/