package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Hello world example.
 *
 * {{{
 *     [****************************]         | [0,5] : a
 *           [****************************]   | [1,6] : b
 *           [**********************]         | [1,5] : c
 *   --+-----+----------------------+-----+-- |
 *     0     1                      5     6   |
 * }}}
 */
object Hello extends App:
  // calculate intersection of two intervals
  val a = Interval.closed(0, 5) // [0,5]
  val b = Interval.closed(1, 6) // [1,6]

  val c = a.intersection(b) // [1,5]

  println(c)
  // [1,5]

  // render the diagram
  val renderer = AsciiRenderer.make[Int]()
  val diagram = Diagram
    .empty[Int]
    .withSection { s =>
      List(a, b, c).zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('a' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
  //     [****************************]         | [0,5] : a
  //           [****************************]   | [1,6] : b
  //           [**********************]         | [1,5] : c
  //   --+-----+----------------------+-----+-- |
  //     0     1                      5     6   |
