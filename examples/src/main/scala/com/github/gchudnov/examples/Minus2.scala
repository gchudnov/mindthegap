package com.github.gchudnov.examples

import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

/**
 * Munus #2
 *
 * {{{
 * Given
 *     [**********************************)   | [0.0,5.0) : a
 *                                            |
 * And Interval To Remove                     |
 *                   [******)                 | [2.0,3.0) : b
 *                                            |
 * When Subtracted                            |
 *     [*************)                        | [0.0,2.0) : c
 *                          [*************)   | [3.0,5.0) : d
 *   --+-------------+------+-------------+-- |
 *    0.0           2.0    3.0           5.0  |
 * }}}
 */
object Minus2 extends App:
  given domainDouble: Domain[Double] = Domain.makeFractional(0.1)

  val a = Interval.leftClosedRightOpen(0.0, 5.0)
  val b = Interval.leftClosedRightOpen(2.0, 3.0)

  val intervals = List(a)

  val result = intervals.flatMap { x =>
    if x.intersects(b) then Interval.minus(x, b)
    else List(x)
  }

  val renderer = AsciiRenderer.make[Double]()
  val diagram = Diagram
    .empty[Double]
    .withSection { s =>
      result.zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
        s.addInterval(i, s"${('c' + k).toChar}")
      }
    }

  renderer.render(diagram)

  println(renderer.result)
